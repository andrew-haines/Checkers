package com.ahaines.ai.search.service.heurstic.service;

import java.util.Comparator;

import com.ahaines.ai.search.model.Node;
import com.ahaines.ai.search.service.heuristic.model.CostState;

public class CostStateComparator<T extends CostState<?>> implements Comparator<Node<T>>{

	public int compare(CostState<?> o1, CostState<?> o2) {
		
		int cost = o2.getCost() - o1.getCost();
		
		return cost;
	}

	public int compare(Node<T> o1, Node<T> o2) {
		return compare(o1.getState(), o2.getState());
	}

}
