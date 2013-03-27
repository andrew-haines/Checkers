package com.ahaines.ai.search.service;

import com.ahaines.ai.search.model.Identifiable;
import com.ahaines.ai.search.model.Node;

/**
 * Callback interface that gets called whenever a node is visited in a search. Implementors
 * can alter the node based on the search providing cost based analysis if required. 
 * @author andrewhaines
 *
 * @param <T>
 */
public interface NodeVisitor<T extends Identifiable> {

	/**
	 * Gets called just before a node is visited
	 * @param node
	 * @param depth Tells you at what depth in the tree this was visited. If depth = 0 then this is a leaf node
	 */
	public void preNodeVisited(Node<T> node, int depth, NodeType type);
	
	/**
	 * Gets called after a node has been expanded in the search. Any children can be expected to
	 * be assigned at this point.
	 * @param node
	 * @param depth Tells you at what depth in the tree this was visited. If depth = 0 then this is a leaf node
	 */
	public void postNodeVisited(Node<T> node, int depth, NodeType type);
}
