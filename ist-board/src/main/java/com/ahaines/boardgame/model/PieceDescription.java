package com.ahaines.boardgame.model;

public abstract class PieceDescription<T extends PieceDescription<T>> {
	private Player<T> player;
	
	protected PieceDescription(Player<T> player){
		this.player = player;
	}
	
	public Player<T> getPlayer(){
		return player;
	}
	
	public String toString(){
		return player.toString();
	}
}
