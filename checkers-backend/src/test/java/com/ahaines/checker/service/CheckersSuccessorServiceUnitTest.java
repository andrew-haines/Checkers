package com.ahaines.checker.service;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ahaines.boardgame.model.Piece;
import com.ahaines.boardgame.model.Player;
import com.ahaines.boardgame.model.Player.PlayerType;
import com.ahaines.boardgame.model.PlayerId;
import com.ahaines.boardgame.model.PlayerLookup;
import com.ahaines.boardgame.model.Position;
import com.ahaines.checkers.model.CheckersBoard;
import com.ahaines.checkers.model.CheckersPiece;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription.Type;
import com.ahaines.ai.search.minmax.model.TurnDrivenState.SimpleTurn;
import com.ahaines.ai.search.minmax.model.TurnDrivenState.Turn;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;

public class CheckersSuccessorServiceUnitTest {

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
	
	private CheckersSuccessorService candidate;
	private Player<CheckersPieceDescription> player1;
	private Player<CheckersPieceDescription> player2;
	private CheckersPieceLookup checkersPieceLookup;
	private CheckersBoard board;
	
	@Before
	public void before(){
		player1 = new Player<CheckersPieceDescription>(Players.PLAYER1, PlayerType.COMPUTER, Collections.<Piece<CheckersPieceDescription>>emptyList());
		player2 = new Player<CheckersPieceDescription>(Players.PLAYER2, PlayerType.HUMAN, Collections.<Piece<CheckersPieceDescription>>emptyList());
		
		PlayerLookup<CheckersPieceDescription> playerLookup = new CheckersPlayerLookup(player1, player2);
		this.checkersPieceLookup = new CheckersPieceLookup(playerLookup);
		this.candidate = new CheckersSuccessorService(playerLookup, checkersPieceLookup, new IncrementalBoardIdService());
		this.board = new CheckersBoard(1, Players.PLAYER1, checkersPieceLookup);
	}
	
	/**
	 * 
	 *    |
	 *    |            X
	 *    |          /  \
	 *    |         X    X
	 */
	@Test
	public void givenNoPiecesToTakeForPlayerMovingSouth_whenCallingGetSuccessors_thenReturnsTwoSouthBoundDiagnonalSuccessors(){
		board.addPiece(new CheckersPiece(new Position(4, 4), checkersPieceLookup.getPiece(49153))); // random add a random piece of alice's to the board
		
		System.out.println(board);
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(2)));
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER2)));
		
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(3, 5));
		assertThat(piece.getPlacement(), is(equalTo(new Position(3, 5))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(49153))));
		assertThat(piece.getPlayer(), is(equalTo(player1)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(1)));
		
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER2)));
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(1), new Position(5, 5));
		assertThat(piece.getPlacement(), is(equalTo(new Position(5, 5))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(49153))));
		assertThat(piece.getPlayer(), is(equalTo(player1)));
		assertThat(Iterables.size(successorStates.get(1).getPieces()), is(equalTo(1)));
	}

	/**
	 * 
	 *    |------------
	 *    | X
	 *    |  \
	 *    |   X
	 */
	@Test
	public void givenNoPiecesToTakeOnNorthLeftBoundaryOfBoardForPlayerMovingSouth_whenCallingGetSuccessors_thenReturnsOneSouthBoundDiagnonalSuccessors(){
		
		board.addPiece(new CheckersPiece(new Position(0, 0), checkersPieceLookup.getPiece(49153))); // random add a random piece of alice's to the board
		System.out.println(board);
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(1)));
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER2)));
		
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(1, 1));
		assertThat(piece.getPlacement(), is(equalTo(new Position(1, 1))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(49153))));
		assertThat(piece.getPlayer(), is(equalTo(player1)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(1)));
	}
	
	/**
	 * 
	 *    |------------
	 *               X |
	 *             /   |
	 *            X    |
	 *                 |
	 */
	@Test
	public void givenNoPiecesToTakeOnNorthRightBoundaryOfBoardForPlayerMovingSouth_whenCallingGetSuccessors_thenReturnsOneSouthBoundDiagnonalSuccessors(){
		board.addPiece(new CheckersPiece(new Position(7, 0), checkersPieceLookup.getPiece(49153))); // random add a random piece of alice's to the board
		System.out.println(board);
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(1)));
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER2)));
		
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(6, 1));
		assertThat(piece.getPlacement(), is(equalTo(new Position(6, 1))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(49153))));
		assertThat(piece.getPlayer(), is(equalTo(player1)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(1)));
	}
	
	/**
	 * 
	 *    |
	 *    |X
	 *    |  \
	 *    |   K
	 *    |______
	 */
	@Test
	public void givenNoPiecesToTakeOnSouthLeftBoundaryOfBoardForPlayerMovingSouth_whenCallingGetSuccessors_thenReturnsOneSuccessorsOfPieceUpgrade(){
		CheckersPiece originalPlacedPiece = new CheckersPiece(new Position(0, 6), checkersPieceLookup.getPiece(49153));
		board.addPiece(originalPlacedPiece); // random add a random piece of alice's to the board
		System.out.println(board);
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(1)));
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER2)));
		
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(1, 7));
		assertThat(piece.getPlacement(), is(equalTo(new Position(1, 7))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(checkersPieceLookup.getKingForPiece(originalPlacedPiece.getPiece()).getId()))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.KING)));
		assertThat(piece.getPlayer(), is(equalTo(player1)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(1)));
	}
	
	/**
	 * 
	 *   
	 *                         X |
	 *                       /   |
	 *                     K     |
	 *   ________________________|
	 */
	@Test
	public void givenNoPiecesToTakeOnSouthRightBoundaryOfBoardForPlayerMovingSouth_whenCallingGetSuccessors_thenReturnsOneSuccessorsOfPieceUpgrade(){
		CheckersPiece originalPlacedPiece = new CheckersPiece(new Position(7, 6), checkersPieceLookup.getPiece(49153));
		board.addPiece(originalPlacedPiece); // random add a random piece of alice's to the board
		System.out.println(board);
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(1)));
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER2)));
		
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(6, 7));
		assertThat(piece.getPlacement(), is(equalTo(new Position(6, 7))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(checkersPieceLookup.getKingForPiece(originalPlacedPiece.getPiece()).getId()))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.KING)));
		assertThat(piece.getPlayer(), is(equalTo(player1)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(1)));
	}
	/**
	 * 
	 *    |
	 *    |                 X
	 *    |               /   \
	 *    |              K     K
	 *    |__________________________
	 */
	@Test
	public void givenNoPiecesToTakeOnSouthBoundaryOfBoardForPlayerMovingSouth_whenCallingGetSuccessors_thenReturnsTwoSuccessorsOfPieceUpgrade(){
		CheckersPiece originalPlacedPiece = new CheckersPiece(new Position(4, 6), checkersPieceLookup.getPiece(49153));
		board.addPiece(originalPlacedPiece); // random add a random piece of alice's to the board
		System.out.println(board);
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(2)));
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER2)));
		
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(3, 7));
		assertThat(piece.getPlacement(), is(equalTo(new Position(3, 7))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(checkersPieceLookup.getKingForPiece(originalPlacedPiece.getPiece()).getId()))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.KING)));
		assertThat(piece.getPlayer(), is(equalTo(player1)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(1)));
		
		assertThat(successorStates.get(1).getTurn(), is(equalTo((Turn)Players.PLAYER2)));
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(1), new Position(5, 7));
		assertThat(piece.getPlacement(), is(equalTo(new Position(5, 7))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(checkersPieceLookup.getKingForPiece(originalPlacedPiece.getPiece()).getId()))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.KING)));
		assertThat(piece.getPlayer(), is(equalTo(player1)));
		assertThat(Iterables.size(successorStates.get(1).getPieces()), is(equalTo(1)));
	}
	
	/**
	 * 
	 *    |          Y     Y
	 *    |           \   /
	 *    |             Y
	 *    |         
	 */
	@Test
	public void givenNoPiecesToTakeForPlayerMovingNorth_whenCallingGetSuccessors_thenReturnsTwoNorthBoundDiagnonalSuccessors(){
		this.board = new CheckersBoard(1, Players.PLAYER2, checkersPieceLookup);
		board.addPiece(new CheckersPiece(new Position(4, 4), checkersPieceLookup.getPiece(16388))); // random add a random piece of alice's to the board
		System.out.println(board);
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(2)));
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER1)));
		
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(3, 3));
		assertThat(piece.getPlacement(), is(equalTo(new Position(3, 3))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(16388))));
		assertThat(piece.getPlayer(), is(equalTo(player2)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(1)));
		
		assertThat(successorStates.get(1).getTurn(), is(equalTo((Turn)Players.PLAYER1)));
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(1), new Position(5, 3));
		assertThat(piece.getPlacement(), is(equalTo(new Position(5, 3))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(16388))));
		assertThat(piece.getPlayer(), is(equalTo(player2)));
		assertThat(Iterables.size(successorStates.get(1).getPieces()), is(equalTo(1)));
	}
	
	/**
	 * 
	 *    |------------
	 *    |   K
	 *    |  / 
	 *    |Y   
	 */
	@Test
	public void givenNoPiecesToTakeOnNorthLeftBoundaryOfBoardForPlayerMovingNorth_whenCallingGetSuccessors_thenReturnsOneNorthBoundDiagnonalSuccessorsOfPieceUpgrade(){
		this.board = new CheckersBoard(1, Players.PLAYER2, checkersPieceLookup);
		CheckersPiece originalPlacedPiece = new CheckersPiece(new Position(0, 1), checkersPieceLookup.getPiece(16388));
		board.addPiece(originalPlacedPiece); // random add a random piece of alice's to the board
		System.out.println(board);
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(1)));
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER1)));
		
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(1, 0));
		assertThat(piece.getPlacement(), is(equalTo(new Position(1, 0))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(checkersPieceLookup.getKingForPiece(originalPlacedPiece.getPiece()).getId()))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.KING)));
		assertThat(piece.getPlayer(), is(equalTo(player2)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(1)));
	}
	
	/**
	 * 
	 *    |------------
	 *             K   |
	 *              \  |
	 *               Y |
	 *                 |
	 */
	public void givenNoPiecesToTakeOnNorthRightBoundaryOfBoardForPlayerMovingNorth_whenCallingGetSuccessors_thenReturnsOneNorthBoundDiagnonalSuccessorsOfPieceUpgarde(){
		this.board = new CheckersBoard(1, Players.PLAYER2, checkersPieceLookup);
		CheckersPiece originalPlacedPiece = new CheckersPiece(new Position(0, 1), checkersPieceLookup.getPiece(16388));
		board.addPiece(originalPlacedPiece); // random add a random piece of alice's to the board
		System.out.println(board);
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(1)));
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER1)));
		
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(1, 0));
		assertThat(piece.getPlacement(), is(equalTo(new Position(1, 0))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(checkersPieceLookup.getKingForPiece(originalPlacedPiece.getPiece()).getId()))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.KING)));
		assertThat(piece.getPlayer(), is(equalTo(player2)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(1)));
	}
	
	/**
	 * 
	 *    |
	 *    |     Y
	 *    |   /
	 *    | Y
	 *    |______
	 */
	@Test
	public void givenNoPiecesToTakeOnSouthLeftBoundaryOfBoardForPlayerMovingNorth_whenCallingGetSuccessors_thenReturnsOneNorthBoundSuccessor(){
		this.board = new CheckersBoard(1, Players.PLAYER2, checkersPieceLookup);
		CheckersPiece originalPlacedPiece = new CheckersPiece(new Position(0, 7), checkersPieceLookup.getPiece(16388));
		board.addPiece(originalPlacedPiece); // random add a random piece of alice's to the board
		System.out.println(board);
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(1)));
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER1)));
		
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(1, 6));
		assertThat(piece.getPlacement(), is(equalTo(new Position(1, 6))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(16388))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.STANDARD)));
		assertThat(piece.getPlayer(), is(equalTo(player2)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(1)));
	}
	
	/**
	 * 
	 *   
	 *                      Y    |
	 *                       \   |
	 *                         Y |
	 *   ________________________|
	 */
	@Test
	public void givenNoPiecesToTakeOnSouthRightBoundaryOfBoardForPlayerMovingNorth_whenCallingGetSuccessors_thenReturnsOneNorthBoundSuccessor(){
		this.board = new CheckersBoard(1, Players.PLAYER2, checkersPieceLookup);
		CheckersPiece originalPlacedPiece = new CheckersPiece(new Position(7, 7), checkersPieceLookup.getPiece(16388));
		board.addPiece(originalPlacedPiece); // random add a random piece of alice's to the board
		System.out.println(board);
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(1)));
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER1)));
		
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(6, 6));
		assertThat(piece.getPlacement(), is(equalTo(new Position(6, 6))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(16388))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.STANDARD)));
		assertThat(piece.getPlayer(), is(equalTo(player2)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(1)));
	}
	
	/**
	 * 
	 *    |
	 *    |          Y       Y
	 *    |            \   /   
	 *    |              Y
	 *    |__________________________
	 */
	@Test
	public void givenNoPiecesToTakeOnSouthBoundaryOfBoardForPlayerMovingNorth_whenCallingGetSuccessors_thenReturnsTwoNorthBoundSuccessors(){
		this.board = new CheckersBoard(1, Players.PLAYER2, checkersPieceLookup);
		CheckersPiece originalPlacedPiece = new CheckersPiece(new Position(4, 4), checkersPieceLookup.getPiece(16388));
		board.addPiece(originalPlacedPiece); // random add a random piece of alice's to the board
		System.out.println(board);
		
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(2)));
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER1)));
		
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(3, 3));
		assertThat(piece.getPlacement(), is(equalTo(new Position(3, 3))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(16388))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.STANDARD)));
		assertThat(piece.getPlayer(), is(equalTo(player2)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(1)));
		
		assertThat(successorStates.get(1).getTurn(), is(equalTo((Turn)Players.PLAYER1)));
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(1), new Position(5, 3));
		assertThat(piece.getPlacement(), is(equalTo(new Position(5, 3))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(16388))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.STANDARD)));
		assertThat(piece.getPlayer(), is(equalTo(player2)));
		assertThat(Iterables.size(successorStates.get(1).getPieces()), is(equalTo(1)));
	}
	

	/**
	 * 
	 *                         Y
	 *    |                   /
	 *    |                  X (forces capture of X. Moving to the left is not permitted in this scenerio)
	 *    |                /   
	 *    |              Y
	 *    |
	 */
	@Test
	public void givenOnePiecesToTakeOnSouthBoundaryOfBoardForPlayerMovingNorth_whenCallingGetSuccessors_thenForcesTheOneNorthBoundCaptureMove(){
		this.board = new CheckersBoard(1, Players.PLAYER2, checkersPieceLookup);
		CheckersPiece originalPlacedPiece = new CheckersPiece(new Position(4, 4), checkersPieceLookup.getPiece(16388));
		board.addPiece(originalPlacedPiece); // random add a random piece of alice's to the board
		board.addPiece(new CheckersPiece(new Position(5,3), checkersPieceLookup.getPiece(49153))); // opponent
		System.out.println(board);
		
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(1)));
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER1)));
		
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(6, 2));
		assertThat(piece.getPlacement(), is(equalTo(new Position(6, 2))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(16388))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.STANDARD)));
		assertThat(piece.getPlayer(), is(equalTo(player2)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(1))); // piece taken
	}
	
	/**
	 * 
	 *             Y            Y
	 *    |         \          /
	 *    |          X       X
	 *    |            \   /   
	 *    |              Y
	 *    |
	 */
	@Test
	public void givenTwoPiecesToTakeOnSouthBoundaryOfBoardForPlayerMovingNorth_whenCallingGetSuccessors_thenReturnsTwoNorthBoundCaptureMove(){
		this.board = new CheckersBoard(1, Players.PLAYER2, checkersPieceLookup);
		CheckersPiece originalPlacedPiece = new CheckersPiece(new Position(4, 4), checkersPieceLookup.getPiece(16388));
		board.addPiece(originalPlacedPiece); // random add a random piece of bobs to the board
		board.addPiece(new CheckersPiece(new Position(5,3), checkersPieceLookup.getPiece(49153))); // opponent
		board.addPiece(new CheckersPiece(new Position(3,3), checkersPieceLookup.getPiece(49158))); // opponent
		System.out.println(board);
		
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(2)));
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER1)));
		
		// first successor
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(2, 2));
		assertThat(piece.getPlacement(), is(equalTo(new Position(2, 2))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(16388))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.STANDARD)));
		assertThat(piece.getPlayer(), is(equalTo(player2)));
		
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(5, 3));
		assertThat(piece.getPlacement(), is(equalTo(new Position(5, 3))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(49153))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.STANDARD)));
		assertThat(piece.getPlayer(), is(equalTo(player1)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(2))); // piece taken
		
		// second successor
		assertThat(successorStates.get(1).getTurn(), is(equalTo((Turn)Players.PLAYER1)));
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(1), new Position(6, 2));
		assertThat(piece.getPlacement(), is(equalTo(new Position(6, 2))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(16388))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.STANDARD)));
		assertThat(piece.getPlayer(), is(equalTo(player2)));
		
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(1), new Position(3, 3));
		assertThat(piece.getPlacement(), is(equalTo(new Position(3, 3))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(49158))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.STANDARD)));
		assertThat(piece.getPlayer(), is(equalTo(player1)));
		assertThat(Iterables.size(successorStates.get(1).getPieces()), is(equalTo(2))); // piece taken
	}
	
	/**
	 * 
	 *              Y
	 *    |       /
	 *    |     X
	 *    |   /   
	 *    | Y
	 *    |
	 */
	@Test
	public void givenOnePiecesToTakeOnSouthLeftBoundaryOfBoardForPlayerMovingNorth_whenCallingGetSuccessors_thenReturnsOneNorthBoundCaptureMove(){
		this.board = new CheckersBoard(1, Players.PLAYER2, checkersPieceLookup);
		CheckersPiece originalPlacedPiece = new CheckersPiece(new Position(0, 4), checkersPieceLookup.getPiece(16388));
		board.addPiece(originalPlacedPiece); // random add a random piece of alice's to the board
		board.addPiece(new CheckersPiece(new Position(1,3), checkersPieceLookup.getPiece(49153))); // opponent
		
		System.out.println(board);
		
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(1)));
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER1)));
		
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(2, 2));
		assertThat(piece.getPlacement(), is(equalTo(new Position(2, 2))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(16388))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.STANDARD)));
		assertThat(piece.getPlayer(), is(equalTo(player2)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(1))); //piece taken

	}
	
	/**
	 * 
	 *              X
	 *    |       /
	 *    |     X
	 *    |   /   
	 *    | Y
	 *    |
	 */
	@Test
	public void givenOnePiecesToTakeOnButBlockedForPlayerMovingNorth_whenCallingGetSuccessors_thenReturnsNoPossibleMoves(){
		this.board = new CheckersBoard(1, Players.PLAYER2, checkersPieceLookup);
		CheckersPiece originalPlacedPiece = new CheckersPiece(new Position(0, 4), checkersPieceLookup.getPiece(16388));
		board.addPiece(originalPlacedPiece); // random add a random piece of alice's to the board
		board.addPiece(new CheckersPiece(new Position(1,3), checkersPieceLookup.getPiece(49153))); // opponent
		board.addPiece(new CheckersPiece(new Position(2,2), checkersPieceLookup.getPiece(49158))); // another opponent
		
		System.out.println(board);
		
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(0)));
		

	}
	
	/**
	 *
	 *    | Y       Y
  	 *    |   \   /
	 *    |     Y
	 *    |      
	 *    | Y
	 *    |
	 */
	@Test
	public void givenTwoPiecesButBlockedForPlayerMovingNorth_whenCallingGetSuccessors_thenReturnsOnlyMostNorthPositionPiecePossibleMoves(){
		this.board = new CheckersBoard(1, Players.PLAYER2, checkersPieceLookup);
		CheckersPiece originalPlacedPiece = new CheckersPiece(new Position(0, 4), checkersPieceLookup.getPiece(16388));
		board.addPiece(originalPlacedPiece); // random add a random piece of alice's to the board
		board.addPiece(new CheckersPiece(new Position(1,3), checkersPieceLookup.getPiece(16389))); // another friendly piece
		
		System.out.println(board);
		
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(2))); // only one of the pieces moves
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER1)));
		
		// first successor
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(0, 4));
		assertThat(piece.getPlacement(), is(equalTo(new Position(0, 4))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(16388))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.STANDARD)));
		assertThat(piece.getPlayer(), is(equalTo(player2)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(2))); 
		
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(0, 2));
		assertThat(piece.getPlacement(), is(equalTo(new Position(0, 2))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(16389))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.STANDARD)));
		assertThat(piece.getPlayer(), is(equalTo(player2)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(2))); 
		
		// second successor
		assertThat(successorStates.get(1).getTurn(), is(equalTo((Turn)Players.PLAYER1)));
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(0, 4));
		assertThat(piece.getPlacement(), is(equalTo(new Position(0, 4))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(16388))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.STANDARD)));
		assertThat(piece.getPlayer(), is(equalTo(player2)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(2))); 
		
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(1), new Position(2, 2));
		assertThat(piece.getPlacement(), is(equalTo(new Position(2, 2))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(16389))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.STANDARD)));
		assertThat(piece.getPlayer(), is(equalTo(player2)));
		assertThat(Iterables.size(successorStates.get(1).getPieces()), is(equalTo(2))); // piece taken

	}
	
	/**
	 *
	 *    |
	 *    |                       X
	 *    |                         \
	 *    |                          Y
	 *    |                           \
	 *    |                             -
	 *    |                              \
	 *    |                               Y
	 *    |                                \
	 *                                      X
	 */
	@Test
	public void givenMultiPieceJumpForPlayerMovingSouth_whenCallingGetSuccessors_thenReturnsOnlyTheSuccessorThatCapturesAPiece(){
		CheckersPiece originalPlacedPiece = new CheckersPiece(new Position(1, 1), checkersPieceLookup.getPiece(49153));
		board.addPiece(originalPlacedPiece); // random add a random piece of alice's to the board
		board.addPiece(new CheckersPiece(new Position(2,2), checkersPieceLookup.getPiece(16388))); // opponent
		board.addPiece(new CheckersPiece(new Position(4,4), checkersPieceLookup.getPiece(16389))); // another opponent
		
		System.out.println(board);
		
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(1))); // must force a move to take a piece
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER2)));
		
		// first successor
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(5, 5));
		assertThat(piece.getPlacement(), is(equalTo(new Position(5, 5))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(49153))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.STANDARD)));
		assertThat(piece.getPlayer(), is(equalTo(player1)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(1))); //both pieces 
	}
	
	/**
	 *
	 *    |
	 *    |                       X
	 *    |                         \
	 *    |                          Y
	 *    |                           \
	 *    |                             -
	 *    |                           /
	 *    |                          Y
	 *    |                         /
	 *                             X
	 */
	@Test
	public void givenMultiPieceJump2ForPlayerMovingSouth_whenCallingGetSuccessors_thenReturnsOnlyTheSuccessorThatCapturesAPiece(){
		CheckersPiece originalPlacedPiece = new CheckersPiece(new Position(1, 1), checkersPieceLookup.getPiece(49153));
		board.addPiece(originalPlacedPiece); // random add a random piece of alice's to the board
		board.addPiece(new CheckersPiece(new Position(2,2), checkersPieceLookup.getPiece(16388))); // opponent
		board.addPiece(new CheckersPiece(new Position(2,4), checkersPieceLookup.getPiece(16389))); // another opponent
		
		System.out.println(board);
		
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(1))); // must force a move to take a piece
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER2)));
		
		// first successor
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(1, 5));
		assertThat(piece.getPlacement(), is(equalTo(new Position(1, 5))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(49153))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.STANDARD)));
		assertThat(piece.getPlayer(), is(equalTo(player1)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(1))); //both pieces 
	}
	
	/**
	 *
	 *    |
	 *    |                       X
	 *    |                         \
	 *    |                          Y
	 *    |                           \
	 *    |                             -
	 *    |                           /
	 *    |                          Y
	 *    |                         /
	 *    |                        K
	 *    ---------------------------------------
	 */
	@Test
	public void givenMultiPieceJump2ForPlayerMovingSouthAndResultsInKing_whenCallingGetSuccessors_thenReturnsOnlyTheSuccessorThatCapturesAPieceAndUpgradesPieceToKing(){
		CheckersPiece originalPlacedPiece = new CheckersPiece(new Position(1, 3), checkersPieceLookup.getPiece(49153));
		board.addPiece(originalPlacedPiece); // random add a random piece of alice's to the board
		board.addPiece(new CheckersPiece(new Position(2,4), checkersPieceLookup.getPiece(16388))); // opponent
		board.addPiece(new CheckersPiece(new Position(2,6), checkersPieceLookup.getPiece(16389))); // another opponent
		
		System.out.println(board);
		
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(1))); // must force a move to take a piece
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER2)));
		
		// first successor
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(1, 7));
		assertThat(piece.getPlacement(), is(equalTo(new Position(1, 7))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getKingForPiece(originalPlacedPiece.getPiece()))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.KING)));
		assertThat(piece.getPlayer(), is(equalTo(player1)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(1))); //both pieces 
	}
	
	/**
	 *
	 *    |
	 *    |                       K
	 *    |                         \
	 *    |                          Y
	 *    |                           \
	 *    |                             K
	 *    |                           /
	 *    |                          Y
	 *    |                         /
	 *    |                        K
	 *    
	 */
	@Test
	public void givenMultiPieceJump2ForKingPlayerMovingSouthAndResults_whenCallingGetSuccessors_thenTwoSuccessorsReturnedThatCaptureInBothDirections(){
		CheckersPiece originalPlacedPiece = new CheckersPiece(new Position(4, 4), checkersPieceLookup.getPiece(114694)); // king piece for alice
		board.addPiece(originalPlacedPiece); // random add a random piece of alice's to the board
		board.addPiece(new CheckersPiece(new Position(3,3), checkersPieceLookup.getPiece(16388))); // opponent
		board.addPiece(new CheckersPiece(new Position(3,5), checkersPieceLookup.getPiece(16389))); // another opponent
		
		System.out.println(board);
		
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(2))); // must force a move to take a pieces
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER2)));
		
		// first successor
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(3, 3));
		assertThat(piece.getPlacement(), is(equalTo(new Position(3, 3))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(16388))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.STANDARD)));
		assertThat(piece.getPlayer(), is(equalTo(player2)));
		
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(2, 6));
		assertThat(piece.getPlacement(), is(equalTo(new Position(2, 6))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(114694))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.KING)));
		assertThat(piece.getPlayer(), is(equalTo(player1)));
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(2))); // can only take one piece in each successor
		
		// seccond successor
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(1), new Position(3, 5));
		assertThat(piece.getPlacement(), is(equalTo(new Position(3, 5))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(16389))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.STANDARD)));
		assertThat(piece.getPlayer(), is(equalTo(player2)));
		
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(1), new Position(2, 2));
		assertThat(piece.getPlacement(), is(equalTo(new Position(2, 2))));
		assertThat(piece.getPiece(), is(equalTo(checkersPieceLookup.getPiece(114694))));
		assertThat(piece.getPiece().getType(), is(equalTo(Type.KING)));
		assertThat(piece.getPlayer(), is(equalTo(player1)));
		assertThat(Iterables.size(successorStates.get(1).getPieces()), is(equalTo(2))); // can only take one piece in each successor
	}
	
	/**
        0      1      2      3      4      5      6      7   
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  0 |      |Alice |      |Alice |      |Alice |      |Alice |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  1 |Alice |      |Alice |      |Alice |      |Alice |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  2 |      |      |      |Alice |      |Alice |      |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  3 |Alice |      |      |      |      |      |Alice*|      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  4 |      |      |      |      |      | Bob  |      |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  5 | Bob  |      | Bob  |      |      |      | Bob  |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  6 |      | Bob  |      | Bob  |      | Bob  |      | Bob  |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  7 | Bob  |      | Bob  |      | Bob  |      | Bob  |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
	 */
	@Test
	public void givenFullBoardReadyToTake_whenCallingGetSuccessors_SingleMoveReturned(){
		int[] state = new int[]{0, 49156, 0, 49152, 0, 16392, 0, 16388, 49160, 0, 0, 0, 0, 0, 16384, 0, 0, 49157, 0, 0, 0, 16393, 0, 16389, 49161, 0, 49153, 0, 0, 0, 16385, 0, 0, 49158, 0, 0, 0, 0, 0, 16390, 49162, 0, 49154, 0, 16394, 0, 16386, 0, 0, 49159, 0, 49155, 0, 16395, 0, 16391, 49163, 0, 0, 0, 0, 0, 16387, 0};
		board = new CheckersBoard(1, state, Players.PLAYER2, checkersPieceLookup);
		
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(1))); // must force a move to take alice's piece
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER1)));
		
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(7, 2));
		assertThat(piece, is(not(nullValue())));
		
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(5, 4));
		assertThat(piece, is(nullValue()));
	}
	
	/**
       0      1      2      3      4      5      6      7   
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  0 |      |Alice |      |Alice |      |Alice |      |Alice |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  1 |      |      |      |      |      |      |Alice |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  2 |      |      |      |Alice |      |Alice |      |Alice |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  3 |      |      |      |      |      |      |      |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  4 |      |Alice*|      |      |      |      |      |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  5 | Bob  |      | Bob  |      |      |      | Bob  |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  6 |      |      |      |      |      | Bob  |      | Bob  |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  7 | Bob  |      |      |      | Bob  |      | Bob  |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
 */
	@Test
	public void givenFullBoardReadyToTakeAndJump_whenCallingGetSuccessors_SingleMoveReturned(){
		int[] state = new int[]{0, 0, 0, 0, 0, 16389, 0, 16388, 49160, 0, 0, 0, 49157, 0, 0, 0, 0, 0, 0, 0, 0, 16385, 0, 0, 49161, 0, 49158, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16390, 49162, 0, 49154, 0, 0, 0, 16386, 0, 0, 49159, 0, 0, 0, 16395, 0, 16391, 49163, 0, 16394, 0, 0, 0, 16387, 0};
		board = new CheckersBoard(1, state, Players.PLAYER2, checkersPieceLookup);
		
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(2))); // must force a move to take alice's piece
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER1)));
		
		//first successor
		
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(4, 1));
		assertThat(piece, is(not(nullValue())));
		
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(0, 5));
		assertThat(piece, is(nullValue()));
		
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(15))); // 2 pieces captured
		
		// second successor
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(1), new Position(0, 3));
		assertThat(piece, is(not(nullValue())));
		
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(1), new Position(2, 5));
		assertThat(piece, is(nullValue()));
		
		assertThat(Iterables.size(successorStates.get(1).getPieces()), is(equalTo(16)));
		
	}
	/**
	 *       
	   0      1      2      3      4      5      6      7   
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  0 |      |      |      |      |      |Alice |      |Alice |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  1 |      |      | Bob^ |      |      |      |Alice |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  2 |      |Alice |      |      |      |Alice |      | Bob  |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  3 |      |      |      |      |      |      |      |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  4 |      |Alice*|      |      |      | Bob  |      |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  5 | Bob  |      |      |      | Bob  |      |      |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  6 |      |      |      |      |      |      |      | Bob  |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  7 |      |      | Bob  |      | Bob  |      | Bob  |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
	 */
	@Test
	public void givenFullBoardAndKingReadyToTakeAndMultiJump_whenCallingGetSuccessors_SingleMoveReturned(){
		int[] state = new int[]{0, 0, 0, 0, 0, 16392, 0, 0, 0, 0, 49160, 0, 49157, 0, 0, 0, 0, 81924, 0, 0, 0, 0, 0, 16389, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16386, 0, 16390, 49162, 0, 49158, 0, 16385, 0, 0, 0, 0, 49159, 0, 0, 0, 0, 0, 16391, 49163, 0, 16394, 0, 0, 0, 16387, 0};
		board = new CheckersBoard(1, state, Players.PLAYER2, checkersPieceLookup);
		
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(2))); // must force a move to take alice's piece
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER1)));
		
		//first successor - normal piece does double jump
		
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(0, 1));
		assertThat(piece, is(not(nullValue())));
		
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(0, 5));
		assertThat(piece, is(nullValue()));
		
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(13))); // 2 pieces captured
		
		// second successor - king piece does double jump
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(1), new Position(2, 5));
		assertThat(piece, is(not(nullValue())));
		
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(1), new Position(2, 1));
		assertThat(piece, is(nullValue()));
		
		assertThat(Iterables.size(successorStates.get(1).getPieces()), is(equalTo(13))); // 2 pieces taken
	}
	/**
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  0 |      |      |      |      |      |      |      |Alice |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  1 |Alice |      |      |      |      |      |Alice |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  2 |      |      |      |Alice |      |      |      | Bob  |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  3 |      |      |      |      |      |      |      |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  4 |      | Bob  |      | Bob^ |      |      |      | Bob  |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  5 | Bob  |      |      |      |      |      |      |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  6 |      | Bob *|      |      |      | Bob  |      |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
    |      |      |      |      |      |      |      |      |
  7 |      |      | Bob  |      | Bob  |      |      |      |
    |      |      |      |      |      |      |      |      |
    |------|------|------|------|------|------|------|------|
	 */
	@Test
	public void givenFullBoardAndReadyToTake_whenCallingGetSuccessors_SingleMoveReturned(){
		int[] state = new int[]{0, 49156, 0, 0, 0, 16392, 0, 16388, 0, 0, 0, 0, 16384, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16389, 0, 0, 49162, 0, 81929, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16390, 0, 0, 0, 0, 0, 0, 16391, 0, 0, 49159, 0, 0, 0, 0, 0, 0, 49163, 0, 16394, 0, 16395, 0, 0, 0};
		board = new CheckersBoard(1, state, Players.PLAYER2, checkersPieceLookup);
		
		List<CheckersBoard> successorStates = Lists.newArrayList(candidate.getSuccessors(board));
		
		assertThat(successorStates.size(), is(equalTo(1))); // must force a move to take alice's piece
		assertThat(successorStates.get(0).getTurn(), is(equalTo((Turn)Players.PLAYER1)));
		
		//first successor - normal piece does double jump
		
		Piece<CheckersPieceDescription> piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(5, 0));
		assertThat(piece, is(not(nullValue())));
		
		piece = CheckersBoard.getPieceAtPosition(successorStates.get(0), new Position(7, 2));
		assertThat(piece, is(nullValue()));
		
		assertThat(Iterables.size(successorStates.get(0).getPieces()), is(equalTo(12))); // 2 pieces captured
		
	}
}
