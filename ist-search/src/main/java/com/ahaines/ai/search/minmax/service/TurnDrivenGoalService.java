package com.ahaines.ai.search.minmax.service;

import com.ahaines.ai.search.minmax.model.TurnDrivenState;
import com.ahaines.ai.search.service.heurstic.service.GoalService;

public interface TurnDrivenGoalService<T extends TurnDrivenState> extends GoalService<T>{

	/**
	 * Returns true if state is won for the provided player
	 * @param state
	 * @return
	 */
	boolean isStateWon(T state, int playerId);
}
