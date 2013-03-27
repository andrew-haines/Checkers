package com.ahaines.ai.search.game;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ahaines.ai.search.minmax.model.MinMaxState;
import com.ahaines.ai.search.minmax.model.TurnDrivenState;
import com.ahaines.ai.search.minmax.service.TurnDrivenGoalService;
import com.ahaines.ai.search.model.Node;
import com.ahaines.ai.search.service.heurstic.service.CostStateComparator;
import com.ahaines.ai.search.service.heurstic.service.HeuristicSearchService;
import com.google.common.collect.Lists;

public class TurnDrivenGameService<T extends TurnDrivenState> {

	private static final Logger LOG = LoggerFactory.getLogger(TurnDrivenGameService.class);
	private final Comparator<Node<MinMaxState<T>>> comparator;
	private final HeuristicSearchService<MinMaxState<T>> searchService;
	private final TurnDrivenGoalService<T> goalService;
	private T currentState;
	private int searchDepthLimit;
	
	public TurnDrivenGameService(HeuristicSearchService<MinMaxState<T>> searchService, T startingState, TurnDrivenGoalService<T> goalService, int searchDepthLimit){
		this.searchService = searchService;
		this.currentState = startingState;
		this.goalService = goalService;
		this.comparator = new CostStateComparator<MinMaxState<T>>();
		this.searchDepthLimit = searchDepthLimit;
	}
	
	public boolean playNextMove() throws GameFinishedException{
		Node<MinMaxState<T>> startNode = getNodeForState(currentState, searchDepthLimit);
		
		checkForGameOver(startNode);
		
		List<Node<MinMaxState<T>>> children = Lists.newArrayList(startNode.getChildren());
		
		if (!children.isEmpty()){
			Collections.sort(children, comparator);
			this.currentState = children.get(0).getState().getActualState();
		}
		
		return isStateWon();
	}
	
	public T getCurrentState(){
		return currentState;
	}
	
	private Node<MinMaxState<T>> getNodeForState(T state, int depth){
		Node<MinMaxState<T>> startNode = new Node<MinMaxState<T>>(null, new MinMaxState<T>(state));
		searchService.depthFirstSearch(startNode, depth);
		
		return startNode;
	}
	
	public boolean playNextMove(T proposedState) throws GameFinishedException{
		if (proposedState.getTurn() != currentState.getTurn().nextTurn()){
			throw new IllegalArgumentException("it is not your turn to make a move");
		}
		
		Node<MinMaxState<T>> startNode = getNodeForState(currentState, 1); // depth of 1 to work out if this is a valid move
		checkForGameOver(startNode);
		
		boolean foundProposedState = false;
		for (Node<MinMaxState<T>> validMoves: startNode.getChildren()){
			if (validMoves.getState().getActualState().equals(proposedState)){
				currentState = validMoves.getState().getActualState(); // use the one we have already calculated so as to use the cache if it is used
				foundProposedState = true;
				break;
			}
		}
		if (!foundProposedState){
			throw new IllegalArgumentException("state proposed is not permitted by the game rules");
		}
		
		return isStateWon();
	}
	
	private void checkForGameOver(Node<MinMaxState<T>> startNode) throws GameFinishedException{
		if (isGameOver(startNode)){
			// we have one so there is no next move.
			
			throw new GameFinishedException(currentState.getTurn().getId());
		} 
	}
	
	public boolean isGameOver(Node<MinMaxState<T>> startNode){
		return isStateWon() || !startNode.getChildren().iterator().hasNext();
	}
	
	public boolean isStateWon(){
		if (goalService.isStateWon(currentState, currentState.getTurn().getId())){
			return true;
		} else{ // check to see if there is actually a move the current player can make
			
			Node<MinMaxState<T>> startNode = getNodeForState(currentState, 1); // depth of 1 to work out if this is a valid move
			return !startNode.getChildren().iterator().hasNext();
		}
	}

	public void setDepthLimit(int newDepthLimit) {
		LOG.info("changing depth limit to: "+newDepthLimit);
		this.searchDepthLimit = newDepthLimit;
	}
	
}
