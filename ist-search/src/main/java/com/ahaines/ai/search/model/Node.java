package com.ahaines.ai.search.model;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

public class Node<T extends Identifiable> implements Identifiable{

	private Node<T> parent;
	private final Collection<Node<T>> children;
	private final T state;
	private int workToBeCompleted;
	private boolean isEvaluated;
	private final Comparator<Node<T>> comparator;
	private final int depthFromStart;
	
	public Node(Node<T> parent, T state, Comparator<Node<T>> heuristicSorter){
		this.parent = parent;
		if (parent != null){
			depthFromStart = parent.getDepthFromStart()+1;
		} else{
			depthFromStart = 0;
		}
		this.comparator = heuristicSorter;
		if (heuristicSorter != null){
			this.children = new TreeSet<Node<T>>(heuristicSorter); // tree set will sort items if they implement comparable meaning that if these are heuristic nodes, we can have children[0] being the biggest cost value
		} else{
			this.children = new ArrayDeque<Node<T>>();
		}
		this.state = state;
		this.workToBeCompleted = 0;
		this.isEvaluated = false;
	}
	
	public int getDepthFromStart() {
		return depthFromStart;
	}

	public Node(Node<T> parent, T state){
		this(parent, state, null);
	}
	
	public void addChild(Node<T> node){
		this.children.add(node);
		isEvaluated = false; // always reset isEvaluated to false as we now have extra children
		workToBeCompleted++;
	}

	public T getState() {
		return state;
	}

	public Node<T> getParent() {
		return parent;
	}

	@Override
	public int hashCode() {
		return getState().getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Node)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		Node<T> other = (Node<T>) obj;
		if (getState() != null) {
			if (other.getState() != null){
				return getState().getId() == other.getState().getId();
			}
		}
			
		return false;
	}

	public Iterable<Node<T>> getChildren() {
		return children;
	}

	public boolean isEvaluated() {
		return isEvaluated; // only evaluated when all work is finished
	}

	public void evaluatedSomeWork() {
		
		if (this.getId() == 20){
			System.out.println("hello");
		}
		this.workToBeCompleted--;
		if (workToBeCompleted == 0){
			isEvaluated = true;
		}
		
		if (workToBeCompleted < 0){
			throw new IllegalStateException("work to be completed is negative???");
		}
	}
	
	public void setEvaluatedComplete(){
		isEvaluated = true;
	}

	public int getId() {
		return state.getId();
	}

	public void setParent(Node<T> parent) {
		this.parent = parent;
	}
	
	@Override
	public String toString(){
		return "node ("+getId()+"): "+getState();
	}

	public Comparator<Node<T>> getComparator() {
		return comparator;
	}
}
