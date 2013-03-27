package com.ahaines.ai.search.minmax.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ahaines.ai.search.minmax.model.MinMaxState;
import com.ahaines.ai.search.minmax.model.TurnDrivenState;
import com.ahaines.ai.search.minmax.model.TurnDrivenState.SimpleTurn;
import com.ahaines.ai.search.model.Node;
import com.ahaines.ai.search.service.NodeType;
import com.ahaines.ai.search.service.heurstic.service.CostFunctionService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class MinMaxNodeVisitorUnitTest {

	private MinMaxSuccessorService<TurnDrivenState> candidate;
	
	@Mock
	private CostFunctionService<TurnDrivenState> costFunctionMock;
	
	@Mock
	private MinMaxState<TurnDrivenState> testStateMock;
	
	@Mock
	private Node<MinMaxState<TurnDrivenState>> testNodeMock;
	
	@Before
	public void before(){
		when(testNodeMock.getState()).thenReturn(testStateMock);
		when(testStateMock.getTurn()).thenReturn(SimpleTurn.MAX);
		candidate = new MinMaxSuccessorService<TurnDrivenState>(null, null, costFunctionMock);
	}
	
	@Test
	public void given5ChildrenAndTransitionNode_whenCallingPostNodeVisited_thenMaxCostAssignedToNode(){
		
		List<Node<MinMaxState<TurnDrivenState>>> children = new ArrayList<Node<MinMaxState<TurnDrivenState>>>(5);
		
		for (int i = 1; i < 6; i++){
			TurnDrivenState actualChildState = mock(TurnDrivenState.class);
			when(actualChildState.getTurn()).thenReturn(SimpleTurn.MIN);
			when(actualChildState.getId()).thenReturn(i);
			MinMaxState<TurnDrivenState> childState = new MinMaxState<TurnDrivenState>(actualChildState);
			childState.setCost(i);
			children.add(new Node<MinMaxState<TurnDrivenState>>(testNodeMock, childState));
		}
		
		when(testNodeMock.getChildren()).thenReturn(children);
		
		candidate.postNodeVisited(testNodeMock, 0, NodeType.TRANSITION);
		
		verify(testStateMock).setCost(-5);
	}
	
	@Test
	public void given5ChildrenAndNotYourTurn_whenCallingPostNodeVisited_thenMinCostAssignedToNode(){
		when(testStateMock.getTurn()).thenReturn(SimpleTurn.MIN);
		List<Node<MinMaxState<TurnDrivenState>>> children = new ArrayList<Node<MinMaxState<TurnDrivenState>>>(5);
		
		for (int i = 1; i < 6; i++){
			TurnDrivenState actualChildState = mock(TurnDrivenState.class);
			when(actualChildState.getTurn()).thenReturn(SimpleTurn.MAX);
			when(actualChildState.getId()).thenReturn(i);
			MinMaxState<TurnDrivenState> childState = new MinMaxState<TurnDrivenState>(actualChildState);
			childState.setCost(i);
			children.add(new Node<MinMaxState<TurnDrivenState>>(testNodeMock, childState));
		}
		
		when(testNodeMock.getChildren()).thenReturn(children);
		
		candidate.postNodeVisited(testNodeMock, 0, NodeType.TRANSITION);
		
		verify(testStateMock).setCost(-5);
	}
}
