package com.ahaines.checkers.model;

import com.ahaines.boardgame.model.Position;

public class Move {

	private final Position from;
	private final Position to;
	
	public Move(Position from, Position to){
		this.from = from;
		this.to = to;
	}

	public Position getFrom() {
		return from;
	}

	public Position getTo() {
		return to;
	}
	
	public String toString(){
		return "from: "+getFrom()+" to: "+getTo();
	}
}
