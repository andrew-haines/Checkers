package com.ahaines.checkers.model;

import java.util.Iterator;

import com.ahaines.ai.search.minmax.model.TurnDrivenState;
import com.ahaines.ai.search.model.Identifiable;
import com.ahaines.boardgame.model.Piece;
import com.ahaines.boardgame.model.PieceLookup;
import com.ahaines.boardgame.model.Position;
import com.ahaines.boardgame.model.SimpleArrayBackedBoard;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription;

public class CheckersBoard extends SimpleArrayBackedBoard<CheckersPieceDescription, CheckersStats> implements Identifiable, TurnDrivenState{

	public static final int CHECKERS_BOARD_MAX = 8;
	private static int NEXT_INT = 0;
	
	private final int id;
	private final Turn turn;

	public CheckersBoard(int id, Turn turn, PieceLookup<CheckersPieceDescription> pieceLookup) {
		super(pieceLookup, CHECKERS_BOARD_MAX, CHECKERS_BOARD_MAX);
		this.id = id;
		this.turn = turn;
	}
	
	/**
	 * Constructor for testing
	 * 
	 * @param id
	 * @param state
	 * @param turn
	 * @param pieceLookup
	 */
	public CheckersBoard(int id, int[] state, Turn turn, PieceLookup<CheckersPieceDescription> pieceLookup){
		super(state, pieceLookup, 8, 8);
		this.id = id;
		this.turn = turn;
	}
	
	/**
	 * Copy constructor for an existing board. Note that this constructor will automatically advance the players move. 
	 * 
	 * Use {@link #CheckersBoard(int, CheckersBoard, com.ahaines.ai.search.minmax.model.TurnDrivenState.Turn)} if you do
	 * not want this to happen.
	 * 
	 * @param id
	 * @param previousState
	 */
	public CheckersBoard(int id, CheckersBoard previousState){
		this(id, previousState, previousState.getTurn().nextTurn());
	}
	
	public CheckersBoard(int id, CheckersBoard previousState, Turn turn){
		super(previousState);
		this.id = id;
		this.turn = turn;
	}

	public int getId() {
		return id;
	}

	public Turn getTurn() {
		return turn;
	}

	public static int getNextId() {
		return NEXT_INT++;
	}

	@Override
	protected int getIdFromPiece(CheckersPieceDescription piece) {
		return piece.getId();
	}
	
	public static Piece<CheckersPieceDescription> getPieceAtPosition(CheckersBoard board, Position position) {
		Iterator<Piece<CheckersPieceDescription>> pieces = board.getPiecesAtPlacement(position).iterator();
		
		if (pieces.hasNext()){
			Piece<CheckersPieceDescription> piece = pieces.next();
			if (pieces.hasNext()){
				throw new IllegalStateException("There should not be more then one piece occupying a single position in checkers");
			}
			return piece;
		}
		return null;
	}

	@Override
	protected CheckersStats createStat() {
		return new CheckersStats();
	}

	@Override
	protected CheckersStats createStat(CheckersStats stats) {
		return new CheckersStats(stats);
	}

}
