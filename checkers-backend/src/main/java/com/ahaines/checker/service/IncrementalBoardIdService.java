package com.ahaines.checker.service;

public class IncrementalBoardIdService implements BoardIdService{

	private int nextId = 0;
	
	public int nextId() {
		return nextId++;
	}

}
