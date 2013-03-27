package com.ahaines.ai.search.game;

/**An exception that is thrown if you try and progress an already won game
 * 
 * @author andrewhaines
 *
 */
public class GameFinishedException extends Exception {

	private final int winnerId;
	
	public GameFinishedException(int winnerId){
		super("Game already finished");
		
		this.winnerId = winnerId;
	}
	
	public int getWinner(){
		return winnerId;
	}
}
