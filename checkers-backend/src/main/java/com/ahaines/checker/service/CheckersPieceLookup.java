package com.ahaines.checker.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.ahaines.boardgame.model.PieceLookup;
import com.ahaines.boardgame.model.Player;
import com.ahaines.boardgame.model.PlayerLookup;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription.Type;

/**
 * Contains all the pieces of the game (12 for each player). The id of each piece is a composite id of the player id and then the actual piece id (0-11).
 * Note that king pieces are treated as additional pieces (ie these are treated as upgraded Standard pieces). The reason for this is that otherwise the pieces would need
 * to be defined as a member of the board. Requiring n x 24 each objects being created where n is the number of states in the search tree (alot of objects!). Using primitives
 * to reference these static piece definitions saves on this overhead.
 * @author andrewhaines
 *
 */
public class CheckersPieceLookup implements PieceLookup<CheckersPieceDescription>{

	private static final int NUMBER_OF_STARTING_PIECES_PER_PLAYER = 12;
	private final Map<Integer, CheckersPieceDescription> pieces;
	
	public CheckersPieceLookup(PlayerLookup<CheckersPieceDescription> playerLookup){
		Map<Integer, CheckersPieceDescription> pieces = new HashMap<Integer, CheckersPieceDescription>();
		
		for (int i = 0; i < NUMBER_OF_STARTING_PIECES_PER_PLAYER; i++){
			for (Player<CheckersPieceDescription> player: playerLookup.getAllPlayers()){
				CheckersPieceDescription newPiece = createPieceForPlayer(player, i, Type.STANDARD);
				if (pieces.containsKey(newPiece.getId())){
					throw new IllegalArgumentException("there is already a piece with id: "+newPiece.getId()+" assigned");
				}
				pieces.put(newPiece.getId(), newPiece);
				
				// add the king piece
				
				CheckersPieceDescription newKingPiece = createPieceForPlayer(player, i, Type.KING);
				if (pieces.containsKey(newKingPiece.getId())){
					throw new IllegalArgumentException("there is already a piece with id: "+newKingPiece.getId()+" assigned");
				}
				pieces.put(newKingPiece.getId(), newKingPiece);
			}
		}
		
		this.pieces = Collections.unmodifiableMap(pieces);
	}
	
	private CheckersPieceDescription createPieceForPlayer(Player<CheckersPieceDescription> player, int i, Type type) {
		
		
		
		return new CheckersPieceDescription(generateGuid(player, i, type), player, type);
	}
	
	/** 
	 * Generates a GUID that uniquely identifies a piece with the defined definitions. The guid is a composite key of the form: 
	 * 
	 * <XXXXXXXXXXXXXXXX><XX><XXXXXXXXXXXXXX>
	 * 
	 *     piece type  playerId  pieceId
	 **/
	private int generateGuid(Player<CheckersPieceDescription> player, int i, Type type) {
		
		int typePreFixId = ((type == Type.STANDARD)?0:1) << 16;
		int playerIdHOB = (player.getPlayerId().getId()+2) << 14;
		int guid = typePreFixId | playerIdHOB | i;
		
		return guid;
	}

	public CheckersPieceDescription getPiece(int id) {
		return pieces.get(id);
	}
	public Collection<CheckersPieceDescription> getAllPieces() {
		return pieces.values();
	}

	public CheckersPieceDescription getKingForPiece(CheckersPieceDescription piece) {
		return getPiece(generateGuid(piece.getPlayer(), piece.getId(), Type.KING));
	}
}