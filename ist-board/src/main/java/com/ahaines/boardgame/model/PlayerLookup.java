package com.ahaines.boardgame.model;

import java.util.Collection;

/**
 * Lookup interface to locate a player based on a numerical id
 * @author andrewhaines
 *
 */
public interface PlayerLookup<T extends PieceDescription<T>> {

	Player<T> getPlayer(int id);
	
	Collection<Player<T>> getAllPlayers();
}
