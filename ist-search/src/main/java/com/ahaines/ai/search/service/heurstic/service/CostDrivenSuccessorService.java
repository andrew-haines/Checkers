package com.ahaines.ai.search.service.heurstic.service;

import java.util.Collection;
import java.util.LinkedList;

import com.ahaines.ai.search.model.Identifiable;
import com.ahaines.ai.search.service.SuccessorService;
import com.ahaines.ai.search.service.heuristic.model.CostState;
import com.ahaines.ai.search.service.heuristic.model.DefaultCostState;

/**
 * On obtaining a new state, this service will run each of the generated states through the cost
 * function to obtain it's heuristic evaluation. The state is then wrapped in a {@link CostState}
 * to encapsulate it's cost. See notes in {@link CostState} regarding this design choice.
 * @author andrewhaines
 *
 * @param <T>
 */
public class CostDrivenSuccessorService<T extends Identifiable> implements SuccessorService<CostState<T>>{

	private final SuccessorService<T> stateSuccessorService;
	
	public CostDrivenSuccessorService(SuccessorService<T> stateSuccessorService){
		this.stateSuccessorService = stateSuccessorService;
	}
	
	protected Iterable<T> getNewStates(CostState<T> state){
		return stateSuccessorService.getSuccessors(state.getActualState());
	}
	
	public Iterable<CostState<T>> getSuccessors(CostState<T> state) {
		Iterable<T> newStates = getNewStates(state);
		
		return calculateCostState(newStates);
	}
	
	protected Collection<CostState<T>> calculateCostState(Iterable<T> newStates){
		Collection<CostState<T>> costNodes = new LinkedList<CostState<T>>();
		
		for (T newSuccessorState: newStates){
			CostState<T> newSuccessorCostState = new DefaultCostState<T>(newSuccessorState);
			costNodes.add(newSuccessorCostState);
		}
		
		return costNodes;
	}

}
