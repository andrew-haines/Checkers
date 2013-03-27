package com.ahaines.boardgame.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Defines the concept of a board. A board can have a number of {@link Placements} that are valid for a
 * {@link Piece} to be positioned at. At this abstraction of the model, no logic is held, only state and
 * functionality to allow to quick O(nlogn) lookup is represented. Implementors are responsible for determining
 * how this state is represented. For sparsely occupied grid, a virtual representation is more appropriate whereas
 * a more spatial and explicit representation is more appropriate for densely populated grids.
 * 
 * Note that this board is meant to be completely agnostic of the actual game being played on it
 * @author andrewhaines
 *
 */
public interface Board<T extends PieceDescription<T>> {

	/**
	 * Given a player, return their peices on the board.
	 * @param player
	 * @return
	 */
	Iterable<Piece<T>> getPieces(Player<T> player);
	
	/**
	 * Return all pieces of the board
	 * @return
	 */
	Iterable<Piece<T>> getPieces();
	
	/**
	 * Assuming that (0,0) is the origin, this method will return the other extremity coordinate
	 * 
	 * @return
	 */
	Position getBoardExtremity();
	
	/**
	 * Returns all pieces at a particular placement on the board. Note that this board
	 * allows multiple pieces to occupy a position at one time to allow for more broad usages
	 * @param placement
	 * @return
	 */
	Iterable<Piece<T>> getPiecesAtPlacement(Position pos);
	
	void removePiece(Piece<T> piece);
	
	void addPiece(Piece<T> piece);
	
	/**
	 * Determines if this position is valid within the confines of the board (ie between the origin (0,0) and the extremity point)
	 * @param pos
	 * @return
	 */
	boolean isValidBoardPosition(Position pos);
	
	/**
	 * Use this method to obtain stats about the board state. This is used to cut down on the amount of {@link Piece} objects that get created using other methods
	 * @return
	 */
	public Stats<T> getBoardStats();
	
	public static interface Stats<T extends PieceDescription<T>> {
		
		public void removePiece(T piece);
		
		public void addPiece(T piece);
		
		public int getPieceCountForPlayer(Player<?> player);
	}
	
	public static class SimpleStats<T extends PieceDescription<T>> implements Stats<T>{

		private final Map<Player<?>, AtomicInteger> pieceCounts;
		
		public SimpleStats(){
			this.pieceCounts = new HashMap<Player<?>, AtomicInteger>();
		}
		
		/**
		 * Copy constructor that does a deep copy...
		 * 
		 * @param stats
		 */
		public SimpleStats(SimpleStats<T> stats) {
			this.pieceCounts = new HashMap<Player<?>, AtomicInteger>();
			
			for (Entry<Player<?>, AtomicInteger> stat: stats.pieceCounts.entrySet()){
				pieceCounts.put(stat.getKey(), new AtomicInteger(stat.getValue().get()));
			}
		}

		public void removePiece(T piece){
			Player<T> player = piece.getPlayer();
			AtomicInteger currentCounts = this.pieceCounts.get(player);
			if (currentCounts == null){
				currentCounts = new AtomicInteger(0);
			} else{
				currentCounts.decrementAndGet();
			}
			pieceCounts.put(player, currentCounts);
		}
		
		public void addPiece(T piece){
			Player<T> player = piece.getPlayer();
			AtomicInteger currentCounts = this.pieceCounts.get(player);
			if (currentCounts == null){
				currentCounts = new AtomicInteger(1);	
			} else{
				currentCounts.incrementAndGet();
			}
			pieceCounts.put(player, currentCounts);
		}
		
		public int getPieceCountForPlayer(Player<?> player){
			return pieceCounts.get(player).get();
		}
		
	}
	
	
}
