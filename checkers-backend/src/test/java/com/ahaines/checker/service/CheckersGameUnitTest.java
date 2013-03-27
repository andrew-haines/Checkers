package com.ahaines.checker.service;

import java.util.Collections;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ahaines.ai.search.game.GameFinishedException;
import com.ahaines.ai.search.minmax.model.TurnDrivenState.SimpleTurn;
import com.ahaines.ai.search.minmax.model.TurnDrivenState.Turn;

import com.ahaines.boardgame.model.Piece;
import com.ahaines.boardgame.model.Player;
import com.ahaines.boardgame.model.Player.PlayerType;
import com.ahaines.boardgame.model.PlayerId;
import com.ahaines.boardgame.model.PlayerLookup;
import com.ahaines.boardgame.model.Position;
import com.ahaines.checkers.model.CheckersBoard;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription.Type;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;

public class CheckersGameUnitTest {

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
	
	private final Player<CheckersPieceDescription> player1 = new Player<CheckersPieceDescription>(Players.PLAYER1, PlayerType.COMPUTER, Collections.<Piece<CheckersPieceDescription>>emptyList());
	private final Player<CheckersPieceDescription> player2 = new Player<CheckersPieceDescription>(Players.PLAYER2, PlayerType.HUMAN, Collections.<Piece<CheckersPieceDescription>>emptyList());
	
	private CheckersGame candidate;
	
	@Before
	public void before(){
		
		PlayerLookup<CheckersPieceDescription> playerLookup = new CheckersPlayerLookup(player1,player2);

		this.candidate = new CheckersGame.CheckersGameBuilder(playerLookup, new IncrementalBoardIdService(), Players.class)
						.setDepthLimit(8)
						.useCaching(false)
						.useAlphaBetaPrunning(true)
						.setStartingTurn(Players.PLAYER1).build();
	}
	
	@Test
	public void givenNewGame_whenCallingGetCurrentState_thenVerifyStartingStateIsCorrect(){
		CheckersBoard board = candidate.getCurrentState();
		
		assertThat(board.getTurn().getId(), is(equalTo(player1.getPlayerId().getId())));
		System.out.println(board);
		assertPiece(board, player1, Type.STANDARD, new Position(1,0));
		assertPiece(board, player1, Type.STANDARD, new Position(3,0));
		assertPiece(board, player1, Type.STANDARD, new Position(5,0));
		assertPiece(board, player1, Type.STANDARD, new Position(7,0));
		assertPiece(board, player1, Type.STANDARD, new Position(0,1));
		assertPiece(board, player1, Type.STANDARD, new Position(2,1));
		assertPiece(board, player1, Type.STANDARD, new Position(4,1));
		assertPiece(board, player1, Type.STANDARD, new Position(6,1));
		assertPiece(board, player1, Type.STANDARD, new Position(1,2));
		assertPiece(board, player1, Type.STANDARD, new Position(3,2));
		assertPiece(board, player1, Type.STANDARD, new Position(5,2));
		assertPiece(board, player1, Type.STANDARD, new Position(7,2));
		
		assertPiece(board, player2, Type.STANDARD, new Position(0,5));
		assertPiece(board, player2, Type.STANDARD, new Position(2,5));
		assertPiece(board, player2, Type.STANDARD, new Position(4,5));
		assertPiece(board, player2, Type.STANDARD, new Position(6,5));
		assertPiece(board, player2, Type.STANDARD, new Position(1,6));
		assertPiece(board, player2, Type.STANDARD, new Position(3,6));
		assertPiece(board, player2, Type.STANDARD, new Position(5,6));
		assertPiece(board, player2, Type.STANDARD, new Position(7,6));
		assertPiece(board, player2, Type.STANDARD, new Position(0,7));
		assertPiece(board, player2, Type.STANDARD, new Position(2,7));
		assertPiece(board, player2, Type.STANDARD, new Position(4,7));
		assertPiece(board, player2, Type.STANDARD, new Position(6,7));
	}
	
	@Test
	@Ignore
	public void givenNewGame_whenCallingGetNextMove_bestMoveChoosen() throws GameFinishedException{
		CheckersBoard board = candidate.getNextMove();
		
		assertThat(board.getTurn().getId(), is(equalTo(player2.getPlayerId().getId())));
		System.out.println(board);
		assertPiece(board, player1, Type.STANDARD, new Position(1,0));
		assertPiece(board, player1, Type.STANDARD, new Position(3,0));
		assertPiece(board, player1, Type.STANDARD, new Position(5,0));
		assertPiece(board, player1, Type.STANDARD, new Position(7,0));
		assertPiece(board, player1, Type.STANDARD, new Position(0,1));
		assertPiece(board, player1, Type.STANDARD, new Position(2,1));
		assertPiece(board, player1, Type.STANDARD, new Position(4,1));
		assertPiece(board, player1, Type.STANDARD, new Position(6,1));
		assertPiece(board, player1, Type.STANDARD, new Position(1,2));
		assertPiece(board, player1, Type.STANDARD, new Position(4,3));
		assertPiece(board, player1, Type.STANDARD, new Position(5,2));
		assertPiece(board, player1, Type.STANDARD, new Position(7,2));
		
		assertPiece(board, player2, Type.STANDARD, new Position(0,5));
		assertPiece(board, player2, Type.STANDARD, new Position(2,5));
		assertPiece(board, player2, Type.STANDARD, new Position(4,5));
		assertPiece(board, player2, Type.STANDARD, new Position(6,5));
		assertPiece(board, player2, Type.STANDARD, new Position(1,6));
		assertPiece(board, player2, Type.STANDARD, new Position(3,6));
		assertPiece(board, player2, Type.STANDARD, new Position(5,6));
		assertPiece(board, player2, Type.STANDARD, new Position(7,6));
		assertPiece(board, player2, Type.STANDARD, new Position(0,7));
		assertPiece(board, player2, Type.STANDARD, new Position(2,7));
		assertPiece(board, player2, Type.STANDARD, new Position(4,7));
		assertPiece(board, player2, Type.STANDARD, new Position(6,7));
	}
	
	private void assertPiece(CheckersBoard board, Player<CheckersPieceDescription> player, Type type, Position position){
		
		Piece<CheckersPieceDescription> description = CheckersBoard.getPieceAtPosition(board, position);
		assertThat(description.getPlacement(), is(equalTo(position)));
		assertThat(description.getPlayer(), is(equalTo(player)));
		assertThat(description.getPiece().getType(), is(equalTo(type)));
		assertThat(description.getPiece().getPlayer(), is(equalTo(player)));
	}
}
