package com.ahaines.ai.search.service.heurstic.service;

import com.ahaines.ai.search.model.Identifiable;

public interface GoalService<T extends Identifiable> {

	/**
	 * Returns true if state is won for the provided player
	 * @param state
	 * @return
	 */
	boolean isStateWon(T state);
}
