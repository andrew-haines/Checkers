package com.ahaines.boardgame.model;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ahaines.boardgame.model.Board.Stats;
import com.ahaines.boardgame.model.Player.PlayerType;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(MockitoJUnitRunner.class)
public class SimpleArrayBackedBoardUnitTest {
	
	private static final int NUMBER_EXPECTED_PIECES = 19;
	private static final int NUMBER_OF_EXPECTED_PIECE_3 = 3;
	
	private enum TestPlayerId implements PlayerId{
		PLAYER1(2, "Alice"),
		PLAYER2(3, "Bob");
		
		private final int id;
		private final String name;
		
		private TestPlayerId(int id, String name){
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public String getPlayerName() {
			return name;
		}
		
	}
	

	private static final int[] SAMPLE_STATE = new int[]{0, 0, 2, 0, 2, 2, 2, 2, 2,0, 0, 0, 0, 0, 3, 2, 2, 3, 2, 2,3, 0, 0, 0,2, 2, 0, 0,2 , 0, 2, 0, 2, 0, 2, 0};

	private SimpleArrayBackedBoard<TestPieceDescription, TestStats> candidate;
	
	@Mock
	private PieceLookup<TestPieceDescription> pieceLookupMock;
	
	private Player<TestPieceDescription> player1;
	
	private Player<TestPieceDescription> player2;
	
	@Before
	public void before(){

		player1 = new Player<TestPieceDescription>(TestPlayerId.PLAYER1, PlayerType.COMPUTER, Collections.<Piece<TestPieceDescription>>emptyList());
		TestPieceDescription description1 = new TestPieceDescription(player1);
		player2 = new Player<TestPieceDescription>(TestPlayerId.PLAYER2, PlayerType.COMPUTER, Collections.<Piece<TestPieceDescription>>emptyList());
		TestPieceDescription description2 = new TestPieceDescription(player2);
		when(pieceLookupMock.getPiece(2)).thenReturn(description1);
		when(pieceLookupMock.getPiece(3)).thenReturn(description2);
		candidate = new SimpleArrayBackedBoard<TestPieceDescription, TestStats>(SAMPLE_STATE, pieceLookupMock, 4, 4){

			@Override
			protected TestStats createStat() {
				return new TestStats();
			}

			@Override
			protected TestStats createStat(TestStats stat) {
				return new TestStats();
			}
			
		};
	}
	
	@Test
	public void givenSampleArray_whenCallingGetPieces_thenCorrectNumberReturned(){
		Iterable<Piece<TestPieceDescription>> pieces = candidate.getPieces();
		
		int count = 0;
		for (Piece<TestPieceDescription> piece:pieces){
			assertThat(piece, is(not(nullValue())));
			count++;
		}
		
		assertThat(count, is(equalTo(NUMBER_EXPECTED_PIECES)));
	}
	
	@Test
	public void givenSampleArray_whenCallingGetPiecesForPlayer1_thenCorrectNumberReturned(){
		Iterable<Piece<TestPieceDescription>> pieces = candidate.getPieces(player1);
		
		int count = 0;
		for (Piece<TestPieceDescription> piece:pieces){
			assertThat(piece, is(not(nullValue())));
			count++;
		}
		
		assertThat(count, is(equalTo(NUMBER_EXPECTED_PIECES - NUMBER_OF_EXPECTED_PIECE_3)));
	}
	
	@Test
	public void givenSampleArray_whenCallingGetPiecesForPlayer2_thenCorrectNumberReturned(){
		Iterable<Piece<TestPieceDescription>> pieces = candidate.getPieces(player2);
		
		int count = 0;
		for (Piece<TestPieceDescription> piece:pieces){
			assertThat(piece, is(not(nullValue())));
			count++;
		}
		
		assertThat(count, is(equalTo(NUMBER_OF_EXPECTED_PIECE_3)));
	}
	
	private static class TestPieceDescription extends PieceDescription<TestPieceDescription>{

		protected TestPieceDescription(Player<TestPieceDescription> player) {
			super(player);
		}
		
	}
	
	private static class TestStats implements Stats<TestPieceDescription>{

		public void removePiece(TestPieceDescription piece) {
			// TODO Auto-generated method stub
			
		}

		public void addPiece(TestPieceDescription piece) {
			// TODO Auto-generated method stub
			
		}

		public int getPieceCountForPlayer(Player<?> player) {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
}
