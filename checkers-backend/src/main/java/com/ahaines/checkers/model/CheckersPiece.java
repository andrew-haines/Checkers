package com.ahaines.checkers.model;

import com.ahaines.boardgame.model.Piece;
import com.ahaines.boardgame.model.PieceDescription;
import com.ahaines.boardgame.model.Player;
import com.ahaines.boardgame.model.Position;

public class CheckersPiece extends Piece<CheckersPiece.CheckersPieceDescription>{

	public CheckersPiece(Position placement, CheckersPieceDescription piece) {
		super(placement, piece);
	}
	
	public CheckersPiece(Piece<CheckersPieceDescription> piece){
		super(new Position(piece.getPlacement()), piece.getPiece());
	}
	
	public static class CheckersPieceDescription extends PieceDescription<CheckersPieceDescription>{

		public static enum Type {
			STANDARD,
			KING
		}
		private final Type type;
		
		private final int id;
		
		public CheckersPieceDescription(int id, Player<CheckersPieceDescription> player, Type type) {
			super(player);
			this.id = id;
			this.type = type;
		}

		public Type getType() {
			return type;
		}

		public int getId() {
			return id;
		}
		
		public String toString(){
			String value = super.toString();//+"("+getId()+")";
			if (type == Type.KING){
				value+="^";
			}
			return value;
		}
	}
}