package com.ahaines.ai.search.minmax.service;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ahaines.ai.search.minmax.model.MinMaxState;
import com.ahaines.ai.search.minmax.model.TurnDrivenState;
import com.ahaines.ai.search.model.Node;
import com.ahaines.ai.search.service.NodeType;
import com.ahaines.ai.search.service.NodeVisitor;
import com.ahaines.ai.search.service.SuccessorNodeService;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * This successor service will only consider expanding a node if it knows that alpha beta pruning
 * cant be applied. This basically acts as a filter around calls to the underlying successor service
 * when we can determine that we do not need to evaluate them. 
 * 
 * 
 *                                             N1
 *                                         /        \
 *  Max                                  N2          N3     
 *                                                 /  |  \
 *  Min                                          N4   N5*  N6*
 *                                               
 *                                                    Filtering is actual performed on evaulation
 *                                                    of N5's and N6's successors rather then at N3.
 *                                                    This is done to induce clear API design
 * 
 * Costs:
 * 
 * N2 = 23
 * N4 = 13
 * 
 * As costs for a given state can not be computed until all successors have been evaluated, doing the filtering 
 * at this level rather then at the node above and filtering this node from even being created, 
 * induces negligible overhead (extra jump statement for each successor and extra Node object creation but this is it).
 * in comparison to the simplicity in design by proxing existing implementations. The decision to
 * Evaluate further successors from N3 is cached via setting the parent (N3 in the above case) to have finished
 * evaluating.
 * 
 * @author andrewhaines
 *
 */
public class AlphaBetaPrunningSuccessorService<T extends TurnDrivenState> implements SuccessorNodeService<MinMaxState<T>>, NodeVisitor<MinMaxState<T>>{
	
	private static final Logger LOG = LoggerFactory.getLogger(AlphaBetaPrunningSuccessorService.class);
	private final MinMaxSuccessorService<T> workerSuccessor;
	
	public AlphaBetaPrunningSuccessorService(MinMaxSuccessorService<T> workerSuccessor){
		this.workerSuccessor = workerSuccessor;
	}

	public Iterable<Node<MinMaxState<T>>> getSuccessors(Node<MinMaxState<T>> node, NodeType type) {
		if (node.getParent() != null && node.getParent().getParent() != null){
			MinMaxState<T> parentState = node.getParent().getState();
			MinMaxState<T> grandParentState = node.getParent().getParent().getState();
			if(isNodeEligableForPruning(parentState, grandParentState)){ // we are filtering so return no successors back.
				node.getParent().setEvaluatedComplete(); // ensures that other children of it's parent are not considered either.
				//LOG.debug("pruning node: "+node.getId()+" type: "+type+" and turn: "+node.getState().getTurn());
				return Collections.emptyList();
			} 
		}
		// if we get here then we were unable to prune
		return workerSuccessor.getSuccessors(node, type);
	}

	private boolean isNodeEligableForPruning(MinMaxState<T> parentState, MinMaxState<T> grandParentState) {
		Integer upperBound = parentState.getLowerBound(); // this will be inverted due to neg max so is actually the upperbound here!
		Integer lowerBound = grandParentState.getLowerBound();
		// TODO rename above method names so they are more consistent???
		
		if (lowerBound != null && upperBound != null && lowerBound > upperBound ){ // the direct parent will never be chosen so ignore this nodes successors
			return true;
		}
		return false;
	}

	public void preNodeVisited(Node<MinMaxState<T>> node, int depth, NodeType type) {
		// NO OP
		
	}

	public void postNodeVisited(Node<MinMaxState<T>> node, int depth, NodeType type) {
		if (type != NodeType.START){ // we have no parent on the start node.
			updateBounds(node);
		}
		
	}
	
	private void updateBounds(Node<MinMaxState<T>> node) {
		
		/* 
		 * use this to update the parents lower bound (The Turn implementation uses neg max
		 * so this will only ever need to work out the lower bound (always maximises))
		 */
		int currentCost = node.getState().getCost();
		
		if (node.getParent() == null){
			LOG.debug("detected null node at: "+node);
		}
		MinMaxState<?> parentState = node.getParent().getState();
		parentState.compareAndSetLowerBound(currentCost);
	}

	@SuppressWarnings("unchecked")
	public Iterable<NodeVisitor<MinMaxState<T>>> getRequestedVisitors() {
		return Iterables.concat(workerSuccessor.getRequestedVisitors(), Lists.<NodeVisitor<MinMaxState<T>>>newArrayList(this));
	}

}
