package com.ahaines.ai.search.service.heurstic.service;

import com.ahaines.ai.search.model.Identifiable;
import com.ahaines.ai.search.model.Node;
import com.ahaines.ai.search.service.NodeType;
import com.ahaines.ai.search.service.NodeVisitor;
import com.ahaines.ai.search.service.heuristic.model.CostState;

public class CostUpdateVisitor<T extends Identifiable> implements NodeVisitor<CostState<T>> {

	private final CostFunctionService<T> costFunctionService;
	
	public CostUpdateVisitor(CostFunctionService<T> costFunctionService){
		this.costFunctionService = costFunctionService;
	}
	
	public void preNodeVisited(Node<CostState<T>> node, int depth, NodeType type) {
		// NO OP
	}

	public void postNodeVisited(Node<CostState<T>> node, int depth, NodeType type) {
		int cost = costFunctionService.calculateCost(node.getState().getActualState());
		node.getState().setCost(cost);
	}
}
