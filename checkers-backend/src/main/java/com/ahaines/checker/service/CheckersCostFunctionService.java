package com.ahaines.checker.service;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ahaines.ai.search.minmax.service.TurnDrivenGoalService;
import com.ahaines.ai.search.service.heurstic.service.CostFunctionService;
import com.ahaines.boardgame.model.Player;
import com.ahaines.boardgame.model.PlayerLookup;
import com.ahaines.boardgame.model.Position;
import com.ahaines.checkers.model.CheckersBoard;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription;
import com.ahaines.checkers.model.CheckersStats;
import com.ahaines.checkers.model.CheckersStats.Counts;

/**
 * This cost function is determined by:
 * 
 * <ol>
 * 	<li>If the state is won for the player that just made the move. This has the greatest weighting</li>
 *  <li>If the state is not won for the player, is the state won for the opponent. This has the greatest negative weighting</li>
 *  <li>If neither side if won from this state, look at the amount of pieces current player has over their opponent</li>
 *  <li>Provide a bonus for each piece that is a king</li>
 *  <li>Also looks at the number of pieces on the edges that a player has over their opponent (pieces on the edge cant be taken)</li>
 *  <li>Also looks at pieces on the corner as these </li>
 * </ol>
 * @author andrewhaines
 *
 */
public class CheckersCostFunctionService implements CostFunctionService<CheckersBoard>{
	
	private static final int POINTS_FOR_A_WIN = 2000;
	private static final int POINTS_FOR_A_LOSE = -2000;
	private static final int PIECE_WEIGHTING = 4;
	private static final int EDGE_PIECE_WEIGHTING = 1;
	private final PlayerLookup<CheckersPieceDescription> playerLookup;
	private final Set<Position> edgePositions;
	private final TurnDrivenGoalService<CheckersBoard> goalService;
	private final Logger LOG = LoggerFactory.getLogger(CheckersCostFunctionService.class);
	
	public CheckersCostFunctionService(PlayerLookup<CheckersPieceDescription> playerLookup, TurnDrivenGoalService<CheckersBoard> goalService){
		this.playerLookup = playerLookup;
		this.edgePositions = new HashSet<Position>();
		populateEdgePositionSet(edgePositions);
		this.goalService = goalService;
	}

	private void populateEdgePositionSet(Set<Position> edgePositions) {
		for (int i = 0 ; i < CheckersBoard.CHECKERS_BOARD_MAX; i++){ 
			// both edges along x
			edgePositions.add(new Position(i, 0));
			edgePositions.add(new Position(i, CheckersBoard.CHECKERS_BOARD_MAX-1));
			
			// both edges along y
			
			edgePositions.add(new Position(0, i));
			edgePositions.add(new Position(CheckersBoard.CHECKERS_BOARD_MAX-1, i));
		}
	}

	public int calculateCost(CheckersBoard state) {
		Player<CheckersPieceDescription> currentPlayer = playerLookup.getPlayer(state.getTurn().nextTurn().getId());
		Player<CheckersPieceDescription> opponent = playerLookup.getPlayer(state.getTurn().getId());
		
		if (goalService.isStateWon(state, state.getTurn().getId())){
			//LOG.debug("Found win state for user: "+currentPlayer);
			return POINTS_FOR_A_WIN;
		} else if (goalService.isStateWon(state, state.getTurn().nextTurn().getId())){
			//LOG.debug("Found win state for opponent: "+currentPlayer);
			return POINTS_FOR_A_LOSE;
		}
		CheckersStats stats = state.getBoardStats();
		Counts opponentCounts = stats.getCounts(opponent);
		Counts currentPlayerCounts = stats.getCounts(currentPlayer);
		int opponentCount = opponentCounts.getTotalPieceCount();
		int playerCount = currentPlayerCounts.getTotalPieceCount();
		
		// weight the pieceDelta more heavily then the other heuristics
		int pieceDelta = (playerCount - opponentCount) * PIECE_WEIGHTING;
		pieceDelta += opponentCounts.getKingPieceCount();
		
		int edgePiecesHeld = getPieceHeldInPositions(state, edgePositions, currentPlayer);
		int edgePieceOpponentHolds = getPieceHeldInPositions(state, edgePositions, opponent);
		
		int edgePiecesDelta = (edgePiecesHeld - edgePieceOpponentHolds) * EDGE_PIECE_WEIGHTING;
		
		return pieceDelta + edgePiecesDelta;
		
	}

	private int getPieceHeldInPositions(CheckersBoard board, Set<Position> positions, Player<CheckersPieceDescription> player) {
		int count = 0;
		
		for (Position pos: positions){
			if (board.holdsPieceAtPoint(player, pos)){
				count++;
			}
		}
		return count;
	}

}
