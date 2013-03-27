package com.ahaines.ai.search.service.heuristic.model;

import com.ahaines.ai.search.model.Identifiable;

/**
 * A state that represents a node that can have a cost associated with it. This can be used in directed
 * heuristic searches.
 * 
 * Note that the implementation here uses wrapped composition rather then inheritance for maximum extensibility
 * and so that the state can be decoupled from its heuristic evaluation.
 * @author andrewhaines
 *
 */
public final class DefaultCostState<T extends Identifiable> implements CostState<T>, Identifiable{

	private int cost;
	private final T state;
	
	public DefaultCostState(T state){
		this.setCost(0);// no cost to start with
		this.state = state;
	}
	
	/**
	 * Returns the cost of this state. This cost could be the static cost or a derived cost.
	 * @return
	 */
	public int getCost(){
		return cost;
	}

	public int compareTo(CostState<T> o) {
		return this.getCost() - o.getCost();
	}

	public int getId() {
		return state.getId();
	}
	
	/**
	 * Returns the underlying state for this object
	 * @return
	 */
	public T getActualState(){
		return state;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
}
