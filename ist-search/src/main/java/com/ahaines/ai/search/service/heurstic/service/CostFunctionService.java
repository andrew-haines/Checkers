package com.ahaines.ai.search.service.heurstic.service;

import com.ahaines.ai.search.model.Identifiable;

public interface CostFunctionService<T extends Identifiable> {

	/**
	 * Returns a cost value based on the supplied state.
	 * @param state
	 * @return
	 */
	int calculateCost(T state);
}
