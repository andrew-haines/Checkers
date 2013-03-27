package com.ahaines.ai.search.minmax.model;

import com.ahaines.ai.search.model.Identifiable;

public interface TurnDrivenState extends Identifiable{

	/**
	 * Returns this states turn
	 * @return
	 */
	Turn getTurn();
	
	public static enum SimpleTurn implements Turn {
		MAX(1),
		MIN(-1);
		
		private final int multiplier;
		
		private SimpleTurn(int multiplier){
			this.multiplier = multiplier;
		}
		/**
		 * Returns the neg-max implementation to ensure max is calculated correctly
		 * @return
		 */
		public int getMultiplier(){
			return multiplier;
		}
		
		/**
		 * Returns the next turn of this turn. As there are only 2 players in min max this is trivial
		 * but implementation could be extended to consider multiple players.
		 * @return
		 */
		public Turn nextTurn() {
			return (this == SimpleTurn.MAX)?SimpleTurn.MIN:SimpleTurn.MAX;
		}
		public int getId() {
			return multiplier;
		}
	}
	
	public static interface Turn{
		int getMultiplier();
		
		int getId();
		
		Turn nextTurn();
	}
	
}
