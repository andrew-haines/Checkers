package com.ahaines.ai.search.service.heuristic.model;

import com.ahaines.ai.search.model.Identifiable;

/**
 * A state that represents a node that can have a cost associated with it. This can be used in directed
 * heuristic searches.
 * 
 * Note that the implied implementation here uses wrapped composition rather then inheritance for maximum extensibility
 * and so that the state can be decoupled from its heuristic evaluation.
 * @author andrewhaines
 *
 */
public interface CostState<T extends Identifiable> extends Identifiable {

	/**
	 * Returns the heuristic cost of this state
	 * @return
	 */
	int getCost();
	
	void setCost(int cost);

	/**
	 * Returns the underlying state this heuristic applies to
	 * @return
	 */
	T getActualState();

}
