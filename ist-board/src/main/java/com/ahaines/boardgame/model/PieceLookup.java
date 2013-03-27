package com.ahaines.boardgame.model;

import java.util.Collection;

public interface PieceLookup<T extends PieceDescription<T>> {

	T getPiece(int id);
	
	Collection<T> getAllPieces();
}
