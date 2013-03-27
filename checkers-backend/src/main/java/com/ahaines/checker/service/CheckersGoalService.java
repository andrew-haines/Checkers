package com.ahaines.checker.service;

import com.ahaines.ai.search.minmax.service.TurnDrivenGoalService;
import com.ahaines.boardgame.model.PlayerLookup;
import com.ahaines.checkers.model.CheckersBoard;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription;

public class CheckersGoalService implements TurnDrivenGoalService<CheckersBoard>{

	private final PlayerLookup<CheckersPieceDescription> playerLookup;
	
	public CheckersGoalService(PlayerLookup<CheckersPieceDescription> playerLookup){
		this.playerLookup = playerLookup;
	}
	
	public boolean isStateWon(CheckersBoard state, int playerId) {
		int playerPieceCount = state.getBoardStats().getPieceCountForPlayer(playerLookup.getPlayer(playerId));
		return playerPieceCount == 0; // player has no more pieces left
	}

	public boolean isStateWon(CheckersBoard state) {
		return isStateWon(state, state.getTurn().getId());
	}

}
