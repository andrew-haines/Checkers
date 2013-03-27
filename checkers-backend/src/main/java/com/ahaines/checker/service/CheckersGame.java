package com.ahaines.checker.service;

import java.util.EnumSet;

import com.ahaines.ai.search.game.GameFinishedException;
import com.ahaines.ai.search.game.TurnDrivenGameService;
import com.ahaines.ai.search.minmax.model.MinMaxState;
import com.ahaines.ai.search.minmax.model.TurnDrivenState.Turn;
import com.ahaines.ai.search.minmax.service.AlphaBetaPrunningSuccessorService;
import com.ahaines.ai.search.minmax.service.MinMaxSuccessorService;
import com.ahaines.ai.search.minmax.service.TurnDrivenGoalService;
import com.ahaines.ai.search.service.CachedSuccessorService;
import com.ahaines.ai.search.service.SuccessorNodeService;
import com.ahaines.ai.search.service.SuccessorService;
import com.ahaines.ai.search.service.heurstic.service.CostFunctionService;
import com.ahaines.ai.search.service.heurstic.service.HeuristicSearchService.HeuristicSearchServiceBuilder;
import com.ahaines.boardgame.model.Piece;
import com.ahaines.boardgame.model.PieceLookup;
import com.ahaines.boardgame.model.Player;
import com.ahaines.boardgame.model.PlayerLookup;
import com.ahaines.boardgame.model.Position;
import com.ahaines.checkers.model.CheckersBoard;
import com.ahaines.checkers.model.CheckersPiece;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription.Type;
import com.ahaines.checkers.model.Move;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class CheckersGame {

	private final TurnDrivenGameService<CheckersBoard> gameService;
	private final CheckersPieceLookup pieceLookup;
	private final BoardIdService boardIdService;
	
	private CheckersGame(TurnDrivenGameService<CheckersBoard> gameService, CheckersPieceLookup pieceLookup, BoardIdService boardIdService){
		this.gameService = gameService;
		this.pieceLookup = pieceLookup;
		this.boardIdService = boardIdService;
	}
	
	public Boolean isWon() {
		return gameService.isStateWon();
	}

	public CheckersBoard getNextMove() throws GameFinishedException {
		boolean isWon = gameService.playNextMove();
		
		return gameService.getCurrentState();
	}
	
	private Piece<CheckersPieceDescription> getPieceAtPossition(Position pos, CheckersBoard board){
		if (board.isValidBoardPosition(pos)){
			Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(board, pos);
			if (piece != null){
				return piece;
			}
		}
		throw new IllegalArgumentException("There is no piece at: position: "+pos);
		
	}
	
	public CheckersBoard playMove(Iterable<Move> moves) throws GameFinishedException {
		CheckersBoard board = gameService.getCurrentState();
		CheckersBoard nextState = new CheckersBoard(boardIdService.nextId(), board);
		Iterable<Move> reverseStack = Lists.reverse(Lists.newArrayList(moves));
		for (Move move: reverseStack){
			
			Piece<CheckersPieceDescription> piece = getPieceAtPossition(move.getFrom(), nextState);
			
			if (!board.isValidBoardPosition(move.getTo())){
				throw new IllegalArgumentException("The move to: "+move.getTo()+" is not within the confines of the board");
			}
			
			nextState.removePiece(piece);
			
			CheckersPieceDescription pieceToPlace = upgradePieceIfRequired(piece.getPiece(), move.getTo(), nextState);
			Piece<CheckersPieceDescription> nextPiece = new Piece<CheckersPieceDescription>(move.getTo(), pieceToPlace);
			nextState.addPiece(nextPiece);
			// create new state from current state
			
			// remove any opponents that might have been captured with this move
			
			removeAnyCapturedPieces(move, nextState);
			
		}
		
		gameService.playNextMove(nextState);
		
		return gameService.getCurrentState();
	}
	
	private void removeAnyCapturedPieces(Move move, CheckersBoard nextState){
		int takenPieceXCoord = (move.getFrom().getXCoord()+move.getTo().getXCoord()) / 2;
		int takenPieceYCoord = (move.getFrom().getYCoord()+move.getTo().getYCoord()) / 2;
		Position takenPosition = new Position(takenPieceXCoord, takenPieceYCoord);
		if (!move.getFrom().equals(takenPosition) && !move.getTo().equals(takenPosition)){ // neither the from or the 2 are the calculated middle position then it has moved more then one place. Thus we must be capturing a piece
			for (Piece<CheckersPieceDescription> removedPiece: nextState.getPiecesAtPlacement(takenPosition)){
				nextState.removePiece(removedPiece); // no need to check whether this is an opponents piece as this move will become invalidated in the game service.
			}
		}
	}
	
	private CheckersPieceDescription upgradePieceIfRequired(CheckersPieceDescription piece, Position newPosition, CheckersBoard board) {
		if (piece.getType() == Type.STANDARD){
			// no need to check if this is a move forward or back ward as this is a new move so the direction is handled in the calling code
			if (newPosition.getYCoord() == board.getBoardExtremity().getYCoord()-1 || newPosition.getYCoord() == 0){
				// upgrading to king as it is on the y border
				
				return pieceLookup.getKingForPiece(piece);
			}
		}
		return piece;
	}

	public static class CheckersGameBuilder{
		
		private static final int DEFAULT_DEPTH_LIMIT = 40;
		private boolean useCaching;
		private boolean useAlphaBetaPrunning;
		private int depthLimit;
		private CostFunctionService<CheckersBoard> checkersCostFunctionService;
		private PlayerLookup<CheckersPieceDescription> playerLookup;
		private final BoardIdService boardIdService;
		private TurnDrivenGoalService<CheckersBoard> checkersGoalService;
		private Turn startingTurn;
		
		public <T extends Enum<T> & Turn> CheckersGameBuilder(PlayerLookup<CheckersPieceDescription> playerLookup, BoardIdService boardIdService, Class<T> turnType){
			
			if (playerLookup.getAllPlayers().size() != 2){
				throw new IllegalArgumentException("2 players are required for checkers");
			}
			this.playerLookup = playerLookup;
			
			// defaults
			checkersGoalService = new CheckersGoalService(playerLookup);
			this.checkersCostFunctionService = new CheckersCostFunctionService(playerLookup, checkersGoalService);
			this.useCaching = true;
			this.useAlphaBetaPrunning = true;
			this.depthLimit = DEFAULT_DEPTH_LIMIT;
			this.boardIdService = boardIdService;
			
			// random starting turn
			
			EnumSet<T> turns = EnumSet.allOf(turnType);
			
			startingTurn = Iterators.get(turns.iterator(), (int)(Math.random() * turns.size()));
		}
		
		public CheckersGameBuilder useCaching(boolean val){
			this.useCaching = val;
			return this;
		}
		
		public CheckersGameBuilder useAlphaBetaPrunning(boolean val){
			this.useAlphaBetaPrunning = val;
			return this;
		}
		
		public CheckersGameBuilder setCheckersCostFunctionService(CostFunctionService<CheckersBoard> costFunctionService){
			this.checkersCostFunctionService = costFunctionService;
			return this;
		}
		
		public CheckersGameBuilder setStartingTurn(Turn turn){
			this.startingTurn = turn;
			return this;
		}
		
		public CheckersGameBuilder setDepthLimit(int depthLimit){
			this.depthLimit = depthLimit;
			
			return this;
		}
		
		public CheckersGame build(){
			
			CheckersPieceLookup pieceLookup = new CheckersPieceLookup(playerLookup);
			
			SuccessorService<CheckersBoard> checkersSuccessorService = new CheckersSuccessorService(playerLookup, pieceLookup,  boardIdService);
			
			MinMaxSuccessorService<CheckersBoard> minMaxSuccessorFunction = new MinMaxSuccessorService<CheckersBoard>(checkersSuccessorService, checkersGoalService, checkersCostFunctionService);
			SuccessorNodeService<MinMaxState<CheckersBoard>> successorNodeService = minMaxSuccessorFunction;
			
			if (useAlphaBetaPrunning){
				successorNodeService = new AlphaBetaPrunningSuccessorService<CheckersBoard>(minMaxSuccessorFunction);
			}
			
			if (useCaching){
				successorNodeService = new CachedSuccessorService<MinMaxState<CheckersBoard>>(successorNodeService);
			}
			HeuristicSearchServiceBuilder<MinMaxState<CheckersBoard>> searchService = new HeuristicSearchServiceBuilder<MinMaxState<CheckersBoard>>(successorNodeService);
			
			CheckersBoard startingState = createStartingState(playerLookup, pieceLookup);
			
			return new CheckersGame(new TurnDrivenGameService<CheckersBoard>(searchService.build(), startingState, checkersGoalService, depthLimit), pieceLookup, boardIdService);
		}

		private CheckersBoard createStartingState(PlayerLookup<CheckersPieceDescription> playerLookup, PieceLookup<CheckersPieceDescription> pieceLookup) {
			
			
			CheckersBoard startingState = new CheckersBoard(boardIdService.nextId(), startingTurn, pieceLookup);
			
			Player<CheckersPieceDescription> firstPlayer = playerLookup.getPlayer(startingTurn.getId());
			int player1PlacedPiece = 1;
			int player2PlacedPiece = 0;
			for (CheckersPieceDescription piece: pieceLookup.getAllPieces()){
				Position position;
				if (piece.getType() == Type.STANDARD){
					if (piece.getPlayer().equals(firstPlayer)){
						int xCoord = (int)(player1PlacedPiece % startingState.getBoardExtremity().getXCoord());
						int yCoord = player1PlacedPiece / startingState.getBoardExtremity().getXCoord();
						if (yCoord == 1){
							xCoord--;
						}
						position = new Position(xCoord, yCoord);
						
						player1PlacedPiece +=2;
					} else{
						int xCoord = (int)(player2PlacedPiece % (startingState.getBoardExtremity().getXCoord()));
						int yCoord = (startingState.getBoardExtremity().getYCoord()-1) - (player2PlacedPiece / (startingState.getBoardExtremity().getXCoord()));
						if (yCoord == 6){
							xCoord++;
						}
						position = new Position(xCoord, yCoord);
						player2PlacedPiece += 2;
					}
					
					System.out.println("adding position at: "+position+" for player "+piece.getPlayer().getPlayerId()+" player1PlacedPiece="+player1PlacedPiece+" player2PlacedPiece="+player2PlacedPiece);
					CheckersPiece positionedPiece = new CheckersPiece(position, piece);
					
					startingState.addPiece(positionedPiece);
				}
			}
			
			return startingState;
		}
	}

	public CheckersBoard getCurrentState() {
		return gameService.getCurrentState();
		
	}

	public void setDepthLimit(int newDepthLimit) {
		this.gameService.setDepthLimit(newDepthLimit);
	}
}
