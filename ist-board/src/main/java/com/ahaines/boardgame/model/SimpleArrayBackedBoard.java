package com.ahaines.boardgame.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public abstract class SimpleArrayBackedBoard<T extends PieceDescription<T>, S extends com.ahaines.boardgame.model.Board.Stats<T>> implements Board<T>{

	private static final int NO_PIECE_ID = 0;
	protected final int[] state;
	private final Position extremity;
	private final PieceLookup<T> pieceLookup;
	private final S stats;
	
	private Position newMove;
	
	protected SimpleArrayBackedBoard(SimpleArrayBackedBoard<T, S> board){
		
		this.state = new int[board.state.length];
		System.arraycopy(board.state, 0, this.state, 0, board.state.length);
		this.extremity = board.getBoardExtremity();
		this.pieceLookup = board.pieceLookup;
		this.stats = createStat(board.stats);
	}
	
	protected SimpleArrayBackedBoard(PieceLookup<T> pieceLookup, int boardXMax, int boardYMax){
		this.state = new int[boardXMax * boardYMax];
		this.pieceLookup = pieceLookup;
		this.extremity = new Position(boardXMax, boardYMax);
		this.stats = createStat();
	}
	
	protected SimpleArrayBackedBoard(int[] state, PieceLookup<T> pieceLookup, int boardXMax, int boardYMax){
		this.state = state;
		this.extremity = new Position(boardXMax, boardYMax);
		this.pieceLookup = pieceLookup;
		this.stats = generateStats();
	}
	
	private S generateStats() {
		
		S stats = createStat();
		
		for(int i = 0; i < state.length; i++){
			if (state[i] != NO_PIECE_ID){
				addPieceStat(stats, pieceLookup.getPiece(state[i]));
			}
		}
		return stats;
	}
	
	protected abstract S createStat();
	
	protected abstract S createStat(S stat);

	protected void addPieceStat(S stat, T pieceType){
		stat.addPiece(pieceType);
	}
	
	protected void removePieceStat(S stats, T stat){
		stats.removePiece(stat);
	}

	private Position getPositionFromIndex(int i) {
		int x = (int)Math.floor(i / extremity.getYCoord());
		int y = i - (x * extremity.getYCoord());
		
		return new Position(x, y);
	}

	public Iterable<Piece<T>> getPieces() {
		Iterable<Piece<T>> pieces = new Iterable<Piece<T>>(){

			public Iterator<Piece<T>> iterator() {
				
				return new Iterator<Piece<T>>(){
					
					private int i = 0;
					
					public boolean hasNext() {
						while(i < state.length && state[i] == NO_PIECE_ID){
							i++;
						}
						return i<state.length;
					}

					public Piece<T> next() {
						return getPieceAtIdx(i++);
					}

					public void remove() {
						// no op
					}
					
				};
			}
			
		};
		
		return pieces;
	}
	
	public boolean holdsPieceAtPoint(Player<T> player, Position pos){
		int idx = getIdxFromPosition(pos);
		if (state[idx] == NO_PIECE_ID){
			return false;
		} else{
			return pieceLookup.getPiece(state[idx]).getPlayer().equals(player);
		}
		
	}
	
	private Piece<T> getPieceAtIdx(int idx){
		return new Piece<T>(getPositionFromIndex(idx), pieceLookup.getPiece(state[idx]));
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Piece<T>> getPiecesAtPlacement(Position placement) {
		int pieceId = state[getIdxFromPosition(placement)];
		if (pieceId == NO_PIECE_ID){ // there are no pieces here
			return Collections.emptyList();
		}
		else{
			return Arrays.asList(new Piece<T>(placement, pieceLookup.getPiece(pieceId)));
		}
			
	}

	private int getIdxFromPosition(Position placement) {

		int col = placement.getXCoord();
		int row = placement.getYCoord();
		return (col * extremity.getXCoord()) + row;
	}

	public Iterable<Piece<T>> getPieces(final Player<T> player) {
		return Iterables.filter(getPieces(), new Predicate<Piece<T>>(){

			public boolean apply(Piece<T> piece) {
				return piece.getPlayer().equals(player);
			}
			
		});
	}

	public Position getBoardExtremity() {
		return extremity;
	}

	public void removePiece(Piece<T> piece) {
		int idx = getIdxFromPosition(piece.getPlacement());
		state[idx] = NO_PIECE_ID;
		removePieceStat(stats, piece.getPiece());
	}

	public void addPiece(Piece<T> piece) {
		int idx = getIdxFromPosition(piece.getPlacement());
		if (idx < 0){
			throw new ArrayIndexOutOfBoundsException(idx);
		}
		if (state[idx] != NO_PIECE_ID){
			throw new IllegalStateException("State already contains piece at this position: "+piece);
		}
		T pieceDescription = piece.getPiece();
		state[idx] = getIdFromPiece(pieceDescription);
		newMove = piece.getPlacement();
		addPieceStat(stats, pieceDescription);
	}
	
	protected int getIdFromPiece(T piece){
		return piece.getPlayer().getPlayerId().getId();
	}

	public Position getNewMove() {
		return newMove;
	}
	
	public String toString(){
		return BoardFormater.toString(this, extremity.getXCoord() * 7, newMove);
	}
	
	@Override
	public int hashCode(){
		return state.hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof SimpleArrayBackedBoard){
			@SuppressWarnings("unchecked")
			SimpleArrayBackedBoard<T, S> other = (SimpleArrayBackedBoard<T, S>)obj;
			return Arrays.equals(this.state, other.state);
		}
		
		return false;
	}

	public boolean isValidBoardPosition(Position pos) {
		return pos.getXCoord() >= 0 && pos.getXCoord() < getBoardExtremity().getXCoord()
				&& pos.getYCoord() >= 0 && pos.getYCoord() < getBoardExtremity().getYCoord();
	}

	public S getBoardStats() {
		return stats;
	}
}
