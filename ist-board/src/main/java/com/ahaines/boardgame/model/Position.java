package com.ahaines.boardgame.model;

public class Position implements Comparable<Position>{

	private int xCoord;
	private int yCoord;
	private Integer distanceFromOrigin = null;
	
	public Position(int xCoord, int yCoord){
		setCoords(xCoord, yCoord);
	}
	
	public Position(Position pos){
		setCoords(pos.getXCoord(), pos.getYCoord());
	}
	
	public void setCoords(int xCoord, int yCoord){
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.distanceFromOrigin = null;
	}
	
	public void move(int xshift, int yshift){
		setCoords(xCoord+xshift, yCoord+yshift);
	}

	private int calculateDistanceFromOrigin() {
		
		/*
		 * Ã(x^2)+(y^2). Note that as origin is assumed to be 0,0 we do not need to consider this here
		 */
		return (int)Math.round(Math.sqrt((double)((xCoord * xCoord) + (yCoord * yCoord))));
	}

	public int compareTo(Position o) {
		
		/*
		 *  use euclidean distance from coord (0,0) to determine comparison of
		 *  one piece from another. note that this is used only for quick binary searches of
		 *  particular peices and is not used in any actual game logic
		 */
		return getDistanceFromOrigin() - o.getDistanceFromOrigin();
	}

	private int getDistanceFromOrigin() {
		if (distanceFromOrigin == null){
			distanceFromOrigin = calculateDistanceFromOrigin();
		}
		return distanceFromOrigin;
	}
	
	public int getXCoord(){
		return xCoord;
	}
	
	public int getYCoord(){
		return yCoord;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + xCoord;
		result = prime * result + yCoord;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Position))
			return false;
		Position other = (Position) obj;
		if (xCoord != other.xCoord)
			return false;
		if (yCoord != other.yCoord)
			return false;
		return true;
	}

	public String toString(){
		return "("+getXCoord()+","+getYCoord()+")";
	}
}
