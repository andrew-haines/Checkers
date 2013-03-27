package com.ahaines.checker.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;

import com.ahaines.ai.search.minmax.model.TurnDrivenState.Turn;
import com.ahaines.ai.search.service.SuccessorService;
import com.ahaines.boardgame.model.Piece;
import com.ahaines.boardgame.model.Player;
import com.ahaines.boardgame.model.PlayerLookup;
import com.ahaines.boardgame.model.Position;
import com.ahaines.checkers.model.CheckersBoard;
import com.ahaines.checkers.model.CheckersPiece;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription.Type;

public class CheckersSuccessorService implements SuccessorService<CheckersBoard>{

	private final PlayerLookup<CheckersPieceDescription> playerLookup;
	private final CheckersPieceLookup pieceLookup;
	private final BoardIdService boardIdService;
	
	public CheckersSuccessorService(PlayerLookup<CheckersPieceDescription> playerLookup, CheckersPieceLookup pieceLookup, BoardIdService boardIdService){
		this.playerLookup = playerLookup;
		this.pieceLookup = pieceLookup;
		this.boardIdService = boardIdService;
	}
	public Iterable<CheckersBoard> getSuccessors(CheckersBoard state) {
		Collection<CheckersBoard> possibleMoves = new LinkedList<CheckersBoard>();
		
		Player<CheckersPieceDescription> player1 = playerLookup.getAllPlayers().iterator().next(); //first player plays on the north of the board
		Player<CheckersPieceDescription> playerWithMove = playerLookup.getPlayer(state.getTurn().getId());
		
		int sideMultiplier = 1;
		if (player1.getPlayerId().getId() != playerWithMove.getPlayerId().getId()){ // this is the guy that moves north so negate their y coords
			sideMultiplier = -1;
		}
		
		Map<MoveType, Collection<CheckersBoard>> possibleMovesType = new EnumMap<MoveType, Collection<CheckersBoard>>(MoveType.class);
			
		for (Piece<CheckersPieceDescription> piece: state.getPieces(playerWithMove)){
			
			addAllStatesFromPiece(state.getTurn().nextTurn(), piece, state, playerWithMove, sideMultiplier, possibleMovesType, false);
			
		}
		
		// now add jump moves or single moves, not both (ie take precedence of jump moves to insist that if a piece can be taken then it should).
		
		if (possibleMovesType.containsKey(MoveType.JUMP)){
			possibleMoves.addAll(possibleMovesType.get(MoveType.JUMP));
		} else if (possibleMovesType.containsKey(MoveType.SINGLE)){
			possibleMoves.addAll(possibleMovesType.get(MoveType.SINGLE));
		}
		return possibleMoves;
	}
	
	private void addAllStatesFromPiece(Turn nextTurn, Piece<CheckersPieceDescription> piece, CheckersBoard state, Player<CheckersPieceDescription> playerWithMove, int sideMultiplier, Map<MoveType, Collection<CheckersBoard>> possibleMoves, boolean isJumpMove) {
		//move left
		Position left = new Position(piece.getPlacement());
		left.move(-1, sideMultiplier);
		
		Position right = new Position(piece.getPlacement());
		right.move(1, sideMultiplier);
		
		boolean movePossible = false;
		movePossible |= addAllStatesFromInitialMove(nextTurn, piece, left, true, state, playerWithMove, sideMultiplier, possibleMoves, isJumpMove);
		movePossible |= addAllStatesFromInitialMove(nextTurn, piece, right, false, state, playerWithMove, sideMultiplier, possibleMoves, isJumpMove);
		
		if (piece.getPiece().getType() == Type.KING){ // consider the other multiplier for the kings (ie the other direction
			Position otherDirectionLeft = new Position(piece.getPlacement());
			sideMultiplier = -sideMultiplier;
			otherDirectionLeft.move(-1, sideMultiplier);
			
			Position otherDirectionRight = new Position(piece.getPlacement());
			otherDirectionRight.move(1, sideMultiplier);
			
			movePossible |= addAllStatesFromInitialMove(nextTurn, piece, otherDirectionLeft, true, state, playerWithMove, sideMultiplier, possibleMoves, isJumpMove);
			movePossible |= addAllStatesFromInitialMove(nextTurn, piece, otherDirectionRight, false, state, playerWithMove, sideMultiplier, possibleMoves, isJumpMove);
		}
		if (!movePossible && isJumpMove){ // no further move was possible from this jump move so add state as a possible move
			addToPossibleMovesMap(MoveType.JUMP, state, possibleMoves);
		}

	}
	/**
	 * 
	 * @param piece
	 * @param newPosition
	 * @param leftOrientatedMove
	 * @param state
	 * @param playerWithMove
	 * @param sideMultiplier
	 * @param possibleMoves
	 * @param considerSingleMoves
	 * @return Whether a move was possible with this move
	 */
	private boolean addAllStatesFromInitialMove(Turn nextTurn, Piece<CheckersPieceDescription> piece, Position newPosition, boolean leftOrientatedMove, CheckersBoard state, Player<CheckersPieceDescription> playerWithMove, int sideMultiplier, Map<MoveType, Collection<CheckersBoard>> possibleMoves, boolean isJumpMove){
		
		if (state.isValidBoardPosition(newPosition)){
			Piece<CheckersPieceDescription> atPotentialMove = CheckersBoard.getPieceAtPosition(state, newPosition);
			if (atPotentialMove != null && atPotentialMove.getPlayer().getPlayerId() != playerWithMove.getPlayerId()){ // favor a move to capture a piece if it is possible.
				// check that there is an adjacent free square that we can jump to before we can capture a piece.
				
				Position jumpSpace = new Position(newPosition);
				if (leftOrientatedMove){
					jumpSpace.move(-1, sideMultiplier);
				} else{
					jumpSpace.move(1, sideMultiplier);
				}
				if (state.isValidBoardPosition(jumpSpace) && CheckersBoard.getPieceAtPosition(state, jumpSpace) == null){ // we can move to this location after we have jumped the opponents piece only if it is in range of the board and not occupied by another piece
					addAllCapturedMoves(nextTurn, piece, atPotentialMove, jumpSpace, state, possibleMoves,playerWithMove, sideMultiplier);
					return true;
				} 
			}
			else if (atPotentialMove == null && !isJumpMove && !possibleMoves.containsKey(MoveType.JUMP)){// we can move here as it is unoccupied and this is not a jump move and we havent seen a jump move yet on this piece
				
				CheckersBoard newState = new CheckersBoard(boardIdService.nextId(), state, nextTurn);
				movePiece(new CheckersPiece(piece), newState, newPosition);
				
				addToPossibleMovesMap(MoveType.SINGLE, newState, possibleMoves);
				return true;
			}
			// if we get to here then the space is occupied by our own piece so is not a valid move
		}
		return false;
	}
	
	private void addToPossibleMovesMap(MoveType type, CheckersBoard newState, Map<MoveType, Collection<CheckersBoard>> possibleMoves) {
		
		Collection<CheckersBoard> movesOfThisType = possibleMoves.get(type);
		if (movesOfThisType == null){
			movesOfThisType = new LinkedList<CheckersBoard>();
		}
		movesOfThisType.add(newState);
		possibleMoves.put(type, movesOfThisType);
		
	}
	private void addAllCapturedMoves(Turn nextTurn, Piece<CheckersPieceDescription> piece, Piece<CheckersPieceDescription> capturedPiece, Position newPosition, CheckersBoard oldState, Map<MoveType, Collection<CheckersBoard>> possibleMoves, Player<CheckersPieceDescription> playerWithMove, int sideMultiplier) {
		// remove opponent piece.
		
		CheckersBoard newState = new CheckersBoard(boardIdService.nextId(), oldState, nextTurn); // note there is a possibility when using multiple jumps of captured pieces that this new state might simply be forgotten as another jump state succeeds it.
		
		// move piece to new position
		Piece<CheckersPieceDescription> newPiece = movePiece(new CheckersPiece(piece), newState, newPosition);
				
		newState.removePiece(capturedPiece);
		
		// now chain additional moves only if the piece hasnt changed (ie hasnt been upgraded)
		if (newPiece.getPiece().equals(piece.getPiece())){
			addAllStatesFromPiece(nextTurn, newPiece, newState, playerWithMove, sideMultiplier, possibleMoves, true);
		} else{
			addToPossibleMovesMap(MoveType.JUMP, newState, possibleMoves);
		}
	}
	
	private Piece<CheckersPieceDescription> movePiece(Piece<CheckersPieceDescription> piece, CheckersBoard newState, Position newPosition){
		
		
		newState.removePiece(piece);
		piece = ammendPiece(piece, newState, newPosition);
		
		newState.addPiece(piece);

		return piece;
	}
	
	private Piece<CheckersPieceDescription> ammendPiece(Piece<CheckersPieceDescription> piece, CheckersBoard oldState, Position newPosition) {
		
		if (piece.getPiece().getType() == Type.STANDARD){
			// no need to check if this is a move forward or back ward as this is a new move so the direction is handled in the calling code
			if (newPosition.getYCoord() == oldState.getBoardExtremity().getYCoord()-1 || newPosition.getYCoord() == 0){
				// upgrading to king as it is on the y border
				
				return new Piece<CheckersPieceDescription>(newPosition, pieceLookup.getKingForPiece(piece.getPiece()));
			}
		}
		piece.getPlacement().setCoords(newPosition.getXCoord(), newPosition.getYCoord());
		
		return piece;
	}
	
	private static enum MoveType {
		SINGLE,
		JUMP
	}
}
