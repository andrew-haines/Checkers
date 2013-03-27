package com.ahaines.ai.search.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ahaines.ai.search.model.Identifiable;
import com.ahaines.ai.search.model.Node;
import com.google.common.collect.Lists;

public class NodeFormater {

	public static <T extends Identifiable & Comparable<T>> String getTree(Node<T> node, FormatVisitor<T> nodeVisitor){
		Map<Integer, List<Node<T>>> depthNodes = new HashMap<Integer, List<Node<T>>>();
		
		Node<T> root = getRoot(node);
		
		StringBuilder builder = new StringBuilder();
		
		int maxDepth = NodeFormater.<T>populateNodeDepthMap(depthNodes, Arrays.<Node<T>>asList(root), 1);
		
		
		return builder.toString();
		
	}

	private static <T extends Identifiable & Comparable<T>> int populateNodeDepthMap(Map<Integer, List<Node<T>>> depthNodes, List<Node<T>> nodes, int depth) {
		List<Node<T>> nodesAtDepth = depthNodes.get(depth);
		
		if (nodesAtDepth == null){
			nodesAtDepth = new LinkedList<Node<T>>();
		}
		nodesAtDepth.addAll(nodes);
		
		depthNodes.put(depth, nodesAtDepth);
		int maxDepth = depth;
		for (Node<T> node: nodes){
			int potentialMax = populateNodeDepthMap(depthNodes, Lists.newArrayList(node.getChildren()), depth+1);
			maxDepth = Math.max(maxDepth, potentialMax);
		}
		
		return maxDepth;
		
	}

	private static <T extends Identifiable & Comparable<T>> Node<T> getRoot(Node<T> node) {
		Node<T> parent = node;
		
		while(parent != null){
			//parent = parent.getParent();
		}
		
		return parent;
	}
	
	public static interface FormatVisitor<T extends Identifiable & Comparable<T>>{
		
		String toString(Node<T> node);
	}
}
