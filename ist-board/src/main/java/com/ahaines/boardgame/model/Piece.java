package com.ahaines.boardgame.model;

/**
 * 
 * @author andrewhaines
 *
 * @param <T>
 */
public class Piece<T extends PieceDescription<T>> implements Comparable<Piece<T>>{

	private final Position placement;
	private final T piece;
	
	public Piece(Position placement, T piece){
		
		if (piece == null){
			throw new NullPointerException();
		}
		this.placement = placement;
		this.piece = piece;
	}

	public int compareTo(Piece<T> otherPiece) {
		return this.getPlacement().compareTo(otherPiece.getPlacement());
	}

	public Player<T> getPlayer() {
		return getPiece().getPlayer();
	}
	
	@Override
	public String toString(){
		return getPiece().toString();
	}

	public Position getPlacement() {
		return placement;
	}

	public T getPiece() {
		return piece;
	}
	
	
}
