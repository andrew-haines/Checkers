package com.ahaines.ai.search.service.heurstic.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ahaines.ai.search.model.Identifiable;
import com.ahaines.ai.search.model.Node;
import com.ahaines.ai.search.service.NodeType;
import com.ahaines.ai.search.service.NodeVisitor;

public class LoggingVisitor implements NodeVisitor<Identifiable>{

	private static final Logger LOG = LoggerFactory.getLogger(LoggingVisitor.class);
	
	public void preNodeVisited(Node<Identifiable> node, int depth, NodeType type) {
		LOG.debug("starting to visit node: "+node.getId()+" ("+((node.getParent() == null)?"-":node.getParent().getId())+")");
	}

	public void postNodeVisited(Node<Identifiable> node, int depth, NodeType type) {
		LOG.debug("finishing visit to node: "+node.getId()+"_state: . node type is "+type);
	}

}
