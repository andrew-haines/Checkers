package com.ahaines.ai.search.minmax.service;

import java.util.Collection;
import java.util.LinkedList;

import com.ahaines.ai.search.minmax.model.MinMaxState;
import com.ahaines.ai.search.minmax.model.TurnDrivenState;
import com.ahaines.ai.search.model.Node;
import com.ahaines.ai.search.service.NodeType;
import com.ahaines.ai.search.service.NodeVisitor;
import com.ahaines.ai.search.service.SuccessorNodeService;
import com.ahaines.ai.search.service.SuccessorService;
import com.ahaines.ai.search.service.heurstic.service.CostFunctionService;
import com.ahaines.ai.search.service.heurstic.service.GoalFilteringSuccessorService;
import com.google.common.collect.Lists;

/**
 * Successor service used to construct the correct state representations for performing min max search.
 * Note that this just delegates to underlying state services that actually generate the successor
 * states.
 * @author andrewhaines
 *
 * @param <T>
 */
public class MinMaxSuccessorService<T extends TurnDrivenState> implements SuccessorNodeService<MinMaxState<T>>, NodeVisitor<MinMaxState<T>> {

	private final SuccessorService<T> successorService;
	private final CostFunctionService<T> costFunctionService;
	
	public MinMaxSuccessorService(SuccessorService<T> staticEvaluationWorkerSuccessorService, TurnDrivenGoalService<T> goalService, CostFunctionService<T> costFunctionService){
		this.successorService = new GoalFilteringSuccessorService<T>(goalService, staticEvaluationWorkerSuccessorService);
		this.costFunctionService = costFunctionService;
	}
	public Iterable<Node<MinMaxState<T>>> getSuccessors(Node<MinMaxState<T>> node, NodeType type) {

		return wrapCostStates(node, successorService.getSuccessors(node.getState().getActualState()));
	}
	
	private Iterable<Node<MinMaxState<T>>> wrapCostStates(Node<MinMaxState<T>> currentNode, Iterable<T> successorStates){
		Collection<Node<MinMaxState<T>>> minMaxSuccessors = new LinkedList<Node<MinMaxState<T>>>(); // TODO these conversions are resulting in a lot of wasted lists. We could consider CachedIterables to avoid them 
		// now we have the new states, iterate over them and construct new MinMaxStates and construct nodes for them
		
		for (T successor: successorStates){
			MinMaxState<T> minMaxSuccessor = new MinMaxState<T>(successor);
			minMaxSuccessors.add(new Node<MinMaxState<T>>(currentNode, minMaxSuccessor, currentNode.getComparator()));
		}
		
		return minMaxSuccessors;
	}
	
	public void preNodeVisited(Node<MinMaxState<T>> node, int depth, NodeType type) {
		// no op
	}

	public void postNodeVisited(Node<MinMaxState<T>> node, int depth, NodeType type) {
		
		if (type != NodeType.LEAF){
			setDynamicCostEvaluations(node);
		} else{
			setStaticCostEvaluation(node, type);
		}
	}
	
	private void setStaticCostEvaluation(Node<MinMaxState<T>> node, NodeType type) {
		int cost = costFunctionService.calculateCost(node.getState().getActualState());
		
		// adapt cost to a scaling factor based on how many transitions it took to calculate
		double scaledCost = cost;
		if (cost != 0 && node.getDepthFromStart() != 0){
			scaledCost = ((cost / node.getDepthFromStart()) * 10000);
		}
		
		if (scaledCost > 0){
			cost = (int)Math.round(scaledCost + 1); // add 1 to ensure we do not scale and truncate to zero.
		} else if (scaledCost < 0){
			cost = (int)Math.round(scaledCost - 1);
		}
		
		node.getState().setCost(cost);
	}
	private void setDynamicCostEvaluations(Node<MinMaxState<T>> node) {
		int childMax = Integer.MIN_VALUE;
		
		MinMaxState<T> minMaxState = node.getState();
		/**
		 * Loop over all child nodes and, using the negmax implementation,
		 */
		for (Node<MinMaxState<T>> child: node.getChildren()){
			
			childMax = Math.max(childMax, child.getState().getCost());
		}

		minMaxState.setCost(childMax * -1); // negmax
	}
	
	@SuppressWarnings("unchecked")
	public Iterable<NodeVisitor<MinMaxState<T>>> getRequestedVisitors() {
		return Lists.<NodeVisitor<MinMaxState<T>>>newArrayList(this);
	}
}
