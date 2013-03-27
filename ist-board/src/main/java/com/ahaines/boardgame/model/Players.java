package com.ahaines.boardgame.model;

public enum Players implements PlayerId{
	PLAYER_ONE("Player One", 1),
	PLAYER_TWO("Player Two", 2);

	private final String playerName;
	private final int id;
	
	private Players(String playerName, int id){
		this.playerName = playerName;
		this.id = id;
	}
	public String getPlayerName() {
		return playerName;
	}
	public int getId() {
		return id;
	}

}
