package com.haines.ist.checkers.ui.swing;

public interface GameWonListener {

	/**
	 * Callback for when a game is won by the user
	 * @param pieces
	 */
	public void gameWonForUser();

	/**
	 * Callback for when a game is won by the computer
	 */
	public void gameWonForComp();
}
