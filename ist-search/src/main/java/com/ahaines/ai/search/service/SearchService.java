package com.ahaines.ai.search.service;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ahaines.ai.search.model.Identifiable;
import com.ahaines.ai.search.model.Node;

/**
 * Service that performs a search providing relevant callbacks via {@link NodeVisitor} implementations
 * as nodes are expanded in the search. 
 * @author andrewhaines
 *
 * @param <T> The type of the state representation.
 */
public class SearchService<T extends Identifiable> {

	private final SuccessorNodeService<T> successorService;
	private final Iterable<NodeVisitor<T>> visitors;
	private static final Logger LOG = LoggerFactory.getLogger(SearchService.class);
	
	private final static int LEAF_NODE_DEPTH = 0;
	
	/**
	 * We want to ensure the visitors are immutable so create a SearchService via the Builder
	 * @param successorService
	 * @param visitors
	 */
	protected SearchService(SuccessorNodeService<T> successorService, Iterable<NodeVisitor<T>> visitors){
		this.successorService = successorService;
		this.visitors = visitors;
	}
	
	public void depthFirstSearch(Node<T> startNode, int depthLimit){
		this.recursiveDepthFirstSearch(startNode, depthLimit, NodeType.START);
	}
	
	private void recursiveDepthFirstSearch(Node<T> currentNode, int depthLimit, NodeType type){
		notifyPreNodeVisited(currentNode, depthLimit, type);
		Iterable<Node<T>> successors = Collections.emptyList();
		if (type != NodeType.LEAF){ // do not consider successors if we are at the limit of search
			
			successors = successorService.getSuccessors(currentNode, type);
		}
		
		int successorDepth = depthLimit -1; // depth is the same for all successors
		NodeType successorType = getNodeTypeFromDepth(successorDepth);
		int successorCount = 0;
		for (Node<T> successor: successors){
			if (!currentNode.isEvaluated()){
				recursiveDepthFirstSearch(successor, successorDepth, successorType);
				currentNode.addChild(successor);
				successorCount++;
			}
		}
		notifyPostNodeVisited(currentNode, depthLimit, successorCount==0?NodeType.LEAF:type);
	}
	
	private void stackdepthFirstSearch(Node<T> startNode, int depthLimit, NodeType type){
		
		Deque<SearchNode<T>> open = new ArrayDeque<SearchService.SearchNode<T>>();
		open.add(new SearchNode<T>(startNode, depthLimit, type));
		while(!open.isEmpty()){
			SearchNode<T> currentSearchNode = open.pop();
			
			Node<T> currentNode = currentSearchNode.getNode();
			NodeType currentType = currentSearchNode.getType();
			
			notifyPreNodeVisited(currentNode, depthLimit, currentType);
			
			Iterable<Node<T>> successors = Collections.emptyList();
			if (currentType != NodeType.LEAF){ // do not consider successors if we are at the limit of search
				
				successors = successorService.getSuccessors(currentNode, currentType);
			}
			
			int successorDepth = currentSearchNode.getDepth() -1; // depth is the same for all successors
			NodeType successorType = getNodeTypeFromDepth(successorDepth);
			int successorCount = 0;
			
			for (Node<T> successor: successors){
				
				/*
				 * Some search strategies can prune certain parts of the subtree for efficiencies. 
				 * This ensures that if this has happened, we do not continue to consider it's successors
				 */
				//if (!currentNode.isEvaluated()){
					
					// check we are not already considering this node.
					
					SearchNode<T> potentialSuccessor = new SearchNode<T>(successor,successorDepth, successorType);
					if (!open.contains(potentialSuccessor)){
						currentNode.addChild(successor);
						open.push(potentialSuccessor);
						successorCount++;
					}
					
				//} else{
					//LOG.debug("Not considering node: "+currentNode.getId());
				//}
			}
			if (successorCount == 0 || currentType == NodeType.LEAF){
				// no successor so this must be finished and a leaf node
				currentNode.setEvaluatedComplete();
				notifyPostNodeVisited(currentNode, depthLimit, NodeType.LEAF);
				// now consider the parent
				evaluateSomeWork(currentNode.getParent(), depthLimit);
			}
		}
	}

	private void evaluateSomeWork(Node<T> node, int depthLimit){
		Node<T> currentNode = node;
		int parentDepth = depthLimit;
		while (currentNode != null){
			currentNode.evaluatedSomeWork();

			//LOG.debug("evaluating work on node: "+currentNode.getId());
			if (currentNode.isEvaluated()){
				notifyPostNodeVisited(currentNode, parentDepth, (currentNode.getParent() == null)?NodeType.START:NodeType.TRANSITION); // has to be a transition or start node
				int previousNodeId = currentNode.getId();
				currentNode = currentNode.getParent();
				//LOG.debug("checking parent of node: {} ({})", previousNodeId, currentNode.getId());
			} else{
				currentNode = null; // do not consider any other parents... yet!
			}
		}
	}

	private NodeType getNodeTypeFromDepth(int successorDepth) {
		NodeType type = NodeType.TRANSITION;
		
		if (successorDepth == LEAF_NODE_DEPTH){
			type = NodeType.LEAF;
			//LOG.debug("limiting search as it has reached specified depth.");
		}
		return type;
	}

	private void notifyPostNodeVisited(Node<T> startNode, int depthLimit, NodeType type) {
		for(NodeVisitor<T> visitor: visitors){
			visitor.postNodeVisited(startNode, depthLimit, type);
		}
	}

	private void notifyPreNodeVisited(Node<T> startNode, int depthLimit, NodeType type) {
		for(NodeVisitor<T> visitor: visitors){
			visitor.preNodeVisited(startNode, depthLimit, type);
		}
	}
	
	/**
	 * Constructs new instances of {@link SearchService} with known setup states
	 * @author andrewhaines
	 *
	 * @param <T>
	 */
	public static class SearchServiceBuilder<T extends Identifiable>{
		
		protected final Collection<NodeVisitor<? super T>> visitors;
		protected final SuccessorNodeService<T> successorService;
		
		public SearchServiceBuilder(SuccessorNodeService<T> successorService){
			if (successorService == null){
				throw new NullPointerException("successorService can never be null");
			}
			this.visitors = new LinkedList<NodeVisitor<? super T>>();
			this.successorService = successorService;
			
			for (NodeVisitor<? super T> visitor: successorService.getRequestedVisitors()){
				registerVisitor(visitor);
			}
		}
		
		public SearchServiceBuilder<T> registerVisitor(NodeVisitor<? super T> visitor){
			visitors.add(visitor);
			return this;
		}
		
		public SearchService<T> build(){
			return new SearchService<T>(successorService, (Collection)visitors);
		}
	}
	
	private static class SearchNode<T extends Identifiable>{
		private final Node<T> node;
		private final int depth;
		private final NodeType type;
		
		private SearchNode(Node<T> node, int depth, NodeType type){
			this.node = node;
			this.depth = depth;
			this.type = type;
		}

		public Node<T> getNode() {
			return node;
		}

		public int getDepth() {
			return depth;
		}

		public NodeType getType() {
			return type;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((node == null) ? 0 : node.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof SearchNode){
				@SuppressWarnings("unchecked")
				SearchNode<T> other = (SearchNode<T>)obj;
				
				return getNode().getState().equals(other.getNode().getState());
			}
			return false;
		}
		
		@Override
		public String toString(){
			return node.toString()+" depth: "+depth+", type: "+type;
		}
	}
}
