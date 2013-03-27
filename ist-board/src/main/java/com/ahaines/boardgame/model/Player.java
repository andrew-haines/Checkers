package com.ahaines.boardgame.model;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

public class Player<T extends PieceDescription<T>> {
	
	public enum PlayerType {
		HUMAN,
		COMPUTER;
	}

	private final SortedSet<Piece<T>> piecesHeld;
	private int piecesCaptured;
	private final PlayerId id;
	private final PlayerType type;
	
	public Player(PlayerId id, PlayerType type, Collection<Piece<T>> piecesHeld){
		this.piecesCaptured = 0;
		this.piecesHeld = new TreeSet<Piece<T>>(piecesHeld);
		this.id = id;
		this.type = type;
	}
	
	public void opponentPieceCaptured(){
		piecesCaptured++;
	}
	
	public void playerPieceLost(Piece<T> piece){
		piecesHeld.remove(piece);
	}
	
	public Iterable<Piece<T>> getPiecesHeld(){
		return piecesHeld;
	}

	public PlayerId getPlayerId() {
		return id;
	}

	public int getPiecesCaptured() {
		return piecesCaptured;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object otherObj) {
		if (otherObj instanceof Player){
			@SuppressWarnings("unchecked")
			Player<T> otherPlayer = (Player<T>)otherObj;
			
			return this.id.equals(otherPlayer.getPlayerId());
		}
		
		return false;
	}

	public PlayerType getType() {
		return type;
	}
	
	@Override
	public String toString(){
		return id.getPlayerName();
	}
}
