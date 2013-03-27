package com.ahaines.boardgame.model;

public class SimplePlayerId implements PlayerId{

	private final int id;
	private final String name;
	
	public SimplePlayerId(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}

	public String getPlayerName() {
		return name;
	}
	
	
}
