package com.ahaines.ai.search.service.heurstic.service;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ahaines.ai.search.model.Identifiable;
import com.ahaines.ai.search.service.SuccessorService;

/**
 * Successor function that only returns successors if the current state is not a goal state.
 * @author andrewhaines
 *
 * @param <T>
 */
public class GoalFilteringSuccessorService<T extends Identifiable> implements SuccessorService<T>{

	private static final Logger LOG = LoggerFactory.getLogger(GoalFilteringSuccessorService.class);
	
	private final GoalService<T> goalService;
	private final SuccessorService<T> successorService;
	
	public GoalFilteringSuccessorService(GoalService<T> goalService, SuccessorService<T> successorService){
		this.goalService = goalService;
		this.successorService = successorService;
	}
	public Iterable<T> getSuccessors(T state) {
		
		if (goalService.isStateWon(state)){
			//LOG.debug("goal found for state: "+state.getId());
			return Collections.emptyList();
		} else{
			return successorService.getSuccessors(state);
		}
	}

}
