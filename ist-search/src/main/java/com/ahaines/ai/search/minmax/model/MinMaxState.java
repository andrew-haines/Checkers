package com.ahaines.ai.search.minmax.model;

import com.ahaines.ai.search.model.Identifiable;
import com.ahaines.ai.search.service.heuristic.model.CostState;

public class MinMaxState<T extends TurnDrivenState> implements Identifiable, CostState<T>, TurnDrivenState, Comparable<MinMaxState<T>>{
	
	private final T costState;
	private Integer lowerBound;
	private int cost;
	
	public MinMaxState(T actualState){
		this.costState = actualState;
		this.cost = 0;
	}	

	/**
	 * sets the lower bound with the supplied value only if the suppiled value is less then
	 * it's current cost.
	 * @param currentCost
	 */
	public void compareAndSetLowerBound(int currentCost){
		if (lowerBound == null || lowerBound > currentCost){
			lowerBound = currentCost;
		}
	}
	
	/**
	 * Returns the lower bound
	 * @return
	 */
	public Integer getLowerBound(){
		return lowerBound;
	}

	public int getId() {
		return getActualState().getId();
	}
	
	public int getCost(){
		return cost;
	}
	
	public T getActualState(){
		return costState;
	}

	public Turn getTurn() {
		return getActualState().getTurn();
	}

	public int compareTo(MinMaxState<T> otherCostState) {
		return getCost() - otherCostState.getCost();
	}
	
	@Override
	public String toString(){
		return "minmax: "+getActualState().toString()+"\ncost:("+getCost()+")\n";
	}
	
	@Override
	public int hashCode(){
		return getActualState().hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof MinMaxState){
			@SuppressWarnings("unchecked")
			MinMaxState<T> other = (MinMaxState<T>)obj;
			
			return getActualState().equals(other.getActualState());
		}
		
		return false;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
}
