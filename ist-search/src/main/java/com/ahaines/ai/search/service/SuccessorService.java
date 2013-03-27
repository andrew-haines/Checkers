package com.ahaines.ai.search.service;

import com.ahaines.ai.search.model.Identifiable;

public interface SuccessorService<T extends Identifiable> {

	/**
	 * Returns the successors of the specified state
	 * @param node
	 * @return
	 */ 
	public Iterable<T> getSuccessors(T state);//TODO do we want to add a NodeType here so that cost functions can determine if they should be static or dynamic
}
