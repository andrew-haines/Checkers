package com.ahaines.ai.search.service;

import com.ahaines.ai.search.model.Identifiable;
import com.ahaines.ai.search.model.Node;

/**
 * Interface that defines the successor function in determining new nodes.
 * @author andrewhaines
 *
 * @param <T>
 */
public interface SuccessorNodeService<T extends Identifiable>{
	
	/**
	 * Similar to {@link #getSuccessors(Identifiable), except that this also provides
	 * a node type to determine what sort of node this provided state is in the context
	 * of the current search.
	 * @param state
	 * @param nodeType
	 * @return
	 */
	public Iterable<Node<T>> getSuccessors(Node<T> state, NodeType nodeType);
	
	public Iterable<NodeVisitor<T>> getRequestedVisitors();
}
