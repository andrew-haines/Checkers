package com.ahaines.ai.search.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ahaines.ai.search.model.Identifiable;
import com.ahaines.ai.search.model.Node;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Provides an implementation that caches already calculated results. This prevents nodes from being
 * re-evaluated if they have already been calculated at a different point in the search tree and or in
 * a different turn. Note that this cache tries to store as much of the search tree as possible due
 * to the fact that we limit the depth of the search. As a storage optimization it is expected that
 * clients call the {@link #clearParentsAndNonConsideredSubTrees(Node)} at the beginning of each turn
 * to ensure that nodes that can never be considered again are removed. 
 * 
 * It is also worth mentioning that this cache is used as a search cache, not a node cache. This means
 * that each node is unique if it appears in the search tree despite the fact that it's contents might
 * be equal. This cache is only used to optimize duplication of searches that have already been
 * performed.
 * @author andrewhaines
 *
 */
public final class CachedSuccessorService<T extends Identifiable> implements SuccessorNodeService<T>, NodeVisitor<T>{

	private static final Logger LOG = LoggerFactory.getLogger(CachedSuccessorService.class);
	
	private final SuccessorNodeService<T> workerSuccessorService;
	private final Map<Node<T>, NodeItem<T>> cache;
	
	public CachedSuccessorService(SuccessorNodeService<T> workerSuccessorService){
		this.workerSuccessorService = workerSuccessorService;
		this.cache = new HashMap<Node<T>, NodeItem<T>>();
	}

	public Iterable<Node<T>> getSuccessors(Node<T> node, NodeType type) {
		// check cache first
		
		NodeItem<T> cachedSuccessorNodes = cache.get(node);
		
		if (cachedSuccessorNodes == null || cachedSuccessorNodes.getNodeType() != type){
			// compute new results.
			//LOG.debug("cache miss with node: "+node.getId());
			Iterable<Node<T>> computedSuccessors = workerSuccessorService.getSuccessors(node, type);
			// add to cache
			
			cachedSuccessorNodes = new NodeItem<T>(type, computedSuccessors, node);
			cache.put(node, cachedSuccessorNodes);
		} else{
			LOG.debug("cache hit with node: "+node.getId());
		}
		
		return cachedSuccessorNodes.getSuccessors();
	}
	
	/**
	 * This method should be called as an optimization to clear down nodes that we know will not
	 * be considered any more. You should invoke this method for each new search from an initial node,
	 * as all parents of this node and sub trees from those parent (except for the supplied node) will
	 * no longer be required and can be removed from memory. To illustrate this, consider the following
	 * tree:
	 * 
	 *                    G
	 *               /          \
	 *       E                         F
	 *    /     \                   /    \
	 * A           B             C          D
	 * 
	 * If we are now running from a new turn based on a choice by the user to use node F, we need no longer
	 * consider nodes G (parent) and E's subtree (A and B). this method if called with F will remove from
	 * the cache nodes A,B,E, and G. For GC reasons, we also remove the parent links from F so that all
	 * redundant nodes can be considered for Garbage Collection
	 * 
	 * @param usedNode
	 */
	public void clearParentsAndNonConsideredSubTrees(Node<?> usedNode){
		Node<?> parent = usedNode.getParent();
		
		if (parent != null){ // we have items to remove
			/* 
			 * recursively consider parent states but in theory, as long as this is called on each on
			 * turn, this will only have 1 parent (the last initial state).
			*/
			clearParentsAndNonConsideredSubTrees(parent);
			
			for (Node<?> child: parent.getChildren()){
				if (!child.equals(usedNode)){ // this is a subtree that we no longer care about.
					clearSubTree(child);
				}
			}
			cache.remove(parent);
		}
		usedNode.setParent(null); // food for GC.
	}

	private void clearSubTree(Node<?> node) {
		cache.remove(node);
		for (Node<?> child: node.getChildren()){
			clearSubTree(child);
		}
	}

	public void preNodeVisited(Node<T> node, int depth, NodeType type) {
		// perform the caching here before the node starts
		if (type == NodeType.START){
			
			/* fetch node from cache rather then the node passed in as it may not be the
			* exact same node cached. If it's not it will have a different parent so to
			* clean the cache of the original values, we will need to use the parent that the cache
			*/ 
			NodeItem<T> cacheNode = cache.get(node);
			
			if (cacheNode != null){
			
				clearParentsAndNonConsideredSubTrees(cacheNode.getCachedParent());
			}
		}
	}

	public void postNodeVisited(Node<T> node, int depth, NodeType type) {
		//NOOP
	}
	
	private static class NodeItem<T extends Identifiable>{
		
		private final NodeType nodeType;
		private final Iterable<Node<T>> successors;
		private final Node<T> cachedParentOfSuccessors;
		
		public NodeItem(NodeType nodeType, Iterable<Node<T>> successors, Node<T> cachedParentOfSuccessors){
			this.nodeType = nodeType;
			this.successors = successors;
			this.cachedParentOfSuccessors = cachedParentOfSuccessors;
		}

		public Node<T> getCachedParent() {
			return cachedParentOfSuccessors;
		}

		public NodeType getNodeType() {
			return nodeType;
		}

		public Iterable<Node<T>> getSuccessors() {
			return successors;
		}
	}

	public Iterable<NodeVisitor<T>> getRequestedVisitors() {
		return Iterables.concat(Lists.<NodeVisitor<T>>newArrayList(this), workerSuccessorService.getRequestedVisitors());
	}
}
