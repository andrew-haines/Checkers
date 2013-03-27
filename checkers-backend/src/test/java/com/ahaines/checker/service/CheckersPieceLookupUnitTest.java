package com.ahaines.checker.service;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.ahaines.ai.search.minmax.model.TurnDrivenState.SimpleTurn;
import com.ahaines.ai.search.minmax.model.TurnDrivenState.Turn;
import com.ahaines.boardgame.model.Piece;
import com.ahaines.boardgame.model.Player;
import com.ahaines.boardgame.model.Player.PlayerType;
import com.ahaines.boardgame.model.PlayerId;
import com.ahaines.boardgame.model.PlayerLookup;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;

public class CheckersPieceLookupUnitTest {
	
	private static enum Players implements PlayerId, Turn {
		PLAYER1(SimpleTurn.MAX, "Alice"),
		PLAYER2(SimpleTurn.MIN, "Bob");
		
		private final int id;
		private final String playerName;
		private final Turn turn;
		
		private Players(Turn turn, String playerName){
			this.id = turn.getMultiplier();
			this.playerName = playerName;
			this.turn = turn;
		}

		public int getId() {
			return id;
		}

		public String getPlayerName() {
			return playerName;
		}

		public int getMultiplier() {
			return turn.getMultiplier();
		}

		public Turn nextTurn() {
			return (this == PLAYER1)?PLAYER2:PLAYER1;
		}
	}

	private CheckersPieceLookup candidate;
	
	@Before
	public void before(){
		PlayerLookup<CheckersPieceDescription> playerLookup = new CheckersPlayerLookup(new Player<CheckersPieceDescription>(Players.PLAYER1, PlayerType.COMPUTER, Collections.<Piece<CheckersPieceDescription>>emptyList()),
															  						   new Player<CheckersPieceDescription>(Players.PLAYER2, PlayerType.HUMAN, Collections.<Piece<CheckersPieceDescription>>emptyList()));
		candidate = new CheckersPieceLookup(playerLookup);
	}
	
	@Test
	public void givenPieceLookup_whenCallingGetAllPieces_thenReturnsAllPieces(){
		assertThat(candidate.getAllPieces().size(), is(equalTo(48)));
	}
	
	@Test
	public void givenPieceLookup_whenCallingGetAllPieces_thenNoNegativesFound(){
		for (CheckersPieceDescription piece: candidate.getAllPieces()){
			assertThat(piece.getId() >= 0, is(equalTo(true)));
		}
	}
}
