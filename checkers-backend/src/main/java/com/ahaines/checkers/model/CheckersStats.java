package com.ahaines.checkers.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.ahaines.boardgame.model.Board.Stats;
import com.ahaines.boardgame.model.Player;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription.Type;

public class CheckersStats implements Stats<CheckersPieceDescription>{

	
	private final Map<Player<CheckersPieceDescription>, Counts> pieceCounts;
	
	public CheckersStats(){
		this.pieceCounts = new HashMap<Player<CheckersPieceDescription>, Counts>();
	}
	
	/**
	 * Copy constructor that does a deep copy...
	 * 
	 * @param stats
	 */
	public CheckersStats(CheckersStats stats) {
		this.pieceCounts = new HashMap<Player<CheckersPieceDescription>, Counts>();
		
		for (Entry<Player<CheckersPieceDescription>, Counts> stat: stats.pieceCounts.entrySet()){
			pieceCounts.put(stat.getKey(), new Counts(stat.getValue()));
		}
	}

	public void removePiece(CheckersPieceDescription piece){
		Player<CheckersPieceDescription> player = piece.getPlayer();
		Counts currentCounts = this.pieceCounts.get(player);
		if (currentCounts == null){
			currentCounts = new Counts();
		} 
		currentCounts.removePiece(piece);
		pieceCounts.put(player, currentCounts);
	}
	
	public void addPiece(CheckersPieceDescription piece){
		Player<CheckersPieceDescription> player = piece.getPlayer();
		Counts currentCounts = this.pieceCounts.get(player);
		if (currentCounts == null){
			currentCounts = new Counts();	
		}
		currentCounts.addPiece(piece);
		pieceCounts.put(player, currentCounts);
	}
	
	public int getPieceCountForPlayer(Player<?> player){
		return pieceCounts.get(player).getTotalPieceCount();
	}
	
	public Counts getCounts(Player<CheckersPieceDescription> player){
		return pieceCounts.get(player);
	}
	
	public static class Counts {
		
		private final AtomicInteger pieceCount;
		private final AtomicInteger kingPieceCount;

		public Counts(Counts value) {
			this.pieceCount = new AtomicInteger(value.pieceCount.get());
			this.kingPieceCount = new AtomicInteger(value.getKingPieceCount());
		}

		public int getTotalPieceCount() {
			return pieceCount.get();
		}

		public void addPiece(CheckersPieceDescription piece) {
			pieceCount.incrementAndGet();
			if (piece.getType() == Type.KING){
				kingPieceCount.incrementAndGet();
			}
		}

		public void removePiece(CheckersPieceDescription piece) {
			pieceCount.decrementAndGet();
			if (piece.getType() == Type.KING){
				kingPieceCount.decrementAndGet();
			}
		}

		public Counts() {
			this.pieceCount = new AtomicInteger();
			this.kingPieceCount = new AtomicInteger();
		}

		public int getKingPieceCount() {
			return kingPieceCount.get();
		}
	}

}
