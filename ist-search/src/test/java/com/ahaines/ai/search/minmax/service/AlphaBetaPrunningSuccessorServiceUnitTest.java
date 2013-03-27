package com.ahaines.ai.search.minmax.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ahaines.ai.search.minmax.model.MinMaxState;
import com.ahaines.ai.search.minmax.model.TurnDrivenState;
import com.ahaines.ai.search.model.Node;
import com.ahaines.ai.search.service.NodeType;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class AlphaBetaPrunningSuccessorServiceUnitTest {

	private AlphaBetaPrunningSuccessorService<TurnDrivenState> candidate;
	
	@Mock
	private MinMaxSuccessorService<TurnDrivenState> workerSuccessorMock;
	
	private Node<MinMaxState<TurnDrivenState>> testNode;
	private Node<MinMaxState<TurnDrivenState>> uncle;
	private Node<MinMaxState<TurnDrivenState>> sibling;
	
	@Before
	public void before(){
		this.candidate = new AlphaBetaPrunningSuccessorService<TurnDrivenState>(workerSuccessorMock);
		
		Node<MinMaxState<TurnDrivenState>> grandparent = new Node<MinMaxState<TurnDrivenState>>(null, createNewMinMaxState());
		uncle = new Node<MinMaxState<TurnDrivenState>>(grandparent, createNewMinMaxState());
		
		Node<MinMaxState<TurnDrivenState>> parentNode = new Node<MinMaxState<TurnDrivenState>>(grandparent, createNewMinMaxState());
		sibling = new Node<MinMaxState<TurnDrivenState>>(parentNode, createNewMinMaxState());
		
		grandparent.addChild(parentNode);
		grandparent.addChild(uncle);
		testNode = new Node<MinMaxState<TurnDrivenState>>(parentNode, createNewMinMaxState());
		
		parentNode.addChild(sibling);
		parentNode.addChild(testNode);
	}
	
	private MinMaxState<TurnDrivenState> createNewMinMaxState() {
		TurnDrivenState state = new TestTurnDrivenState();
		
		MinMaxState<TurnDrivenState> minMaxState = new MinMaxState<TurnDrivenState>(state);
		
		return minMaxState;
	}

	@Test
	public void givenNonPrunnedState_whenCallingGetSuccessors_thenUnderlyingSuccessorInvoked(){
		uncle.getState().setCost(9);
		candidate.postNodeVisited(uncle, 1, NodeType.TRANSITION);
		
		sibling.getState().setCost(10);
		candidate.postNodeVisited(sibling, 2, NodeType.TRANSITION);
		
		candidate.getSuccessors(testNode, NodeType.TRANSITION);
		
		verify(workerSuccessorMock, times(1)).getSuccessors(testNode, NodeType.TRANSITION);
	}
	
	@Test
	public void givenAlphaPrunningState_whenCallingGetSuccessor_thenUnderlyingSuccessorsNotInvoked(){
		/**
		 * This is from the example in the lecture notes
		 */
		
		uncle.getState().setCost(15);
		candidate.postNodeVisited(uncle, 1, NodeType.TRANSITION);
		
		sibling.getState().setCost(10);
		candidate.postNodeVisited(sibling, 2, NodeType.TRANSITION);
		
		candidate.getSuccessors(testNode, NodeType.TRANSITION);
		
		verify(workerSuccessorMock, never()).getSuccessors(testNode, NodeType.TRANSITION);
	}
	
	@Test
	public void givenBetaPrunningState_whenCallingGetSuccessor_thenUnderlyingSuccessorsNotInvoked(){
		/**
		 * This is from the example in the lecture notes
		 */
		
		uncle.getState().setCost(15);
		candidate.postNodeVisited(uncle, 1, NodeType.TRANSITION);
		
		sibling.getState().setCost(10);
		candidate.postNodeVisited(sibling, 2, NodeType.TRANSITION);
		
		candidate.getSuccessors(testNode, NodeType.TRANSITION);
		
		verify(workerSuccessorMock, never()).getSuccessors(testNode, NodeType.TRANSITION);
	}
	
	private static class TestTurnDrivenState implements TurnDrivenState{

		public int getId() {
			// TODO Auto-generated method stub
			return 0;
		}

		public Turn getTurn() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
