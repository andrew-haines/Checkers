package com.ahaines.checker.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.ahaines.boardgame.model.Player;
import com.ahaines.boardgame.model.PlayerLookup;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription;

public class CheckersPlayerLookup implements PlayerLookup<CheckersPieceDescription>{

	private final Map<Integer, Player<CheckersPieceDescription>> players;
	
	public CheckersPlayerLookup(Player<CheckersPieceDescription> player1, Player<CheckersPieceDescription> player2){
		Map<Integer, Player<CheckersPieceDescription>> players = new HashMap<Integer, Player<CheckersPieceDescription>>();
		
		players.put(player1.getPlayerId().getId(), player1);
		players.put(player2.getPlayerId().getId(), player2);
		
		this.players = Collections.unmodifiableMap(players);
	}
	
	public Player<CheckersPieceDescription> getPlayer(int id) {
		Player<CheckersPieceDescription> player = players.get(id);
		
		if (player == null){
			throw new IllegalArgumentException("player id: "+id+" is not a valid player in the game");
		}
		
		return player;
	}

	public Collection<Player<CheckersPieceDescription>> getAllPlayers() {
		return players.values();
	}

}
