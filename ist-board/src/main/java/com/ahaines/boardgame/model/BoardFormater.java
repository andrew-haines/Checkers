package com.ahaines.boardgame.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class BoardFormater {

	private static final int DEFAULT_IMAGE_MAX = 140;

	public static String toString(Board<?> board){
		return toString(board, DEFAULT_IMAGE_MAX, null);
	}
	
	public static String toString(Board<?> board, Position newMove){
		return toString(board, DEFAULT_IMAGE_MAX, newMove);
	}
	
	public static String toString(Board<?> board , int imageMax) {
		return toString(board, imageMax, null);
	}
	
	public static String toString(Board<?> board, int imageMax, Position newMove){
		StringBuilder builder = new StringBuilder();
		int dashesPerSquare = getDashesPerSquare(imageMax, board.getBoardExtremity().getXCoord());
		printXIndices(builder, board.getBoardExtremity().getYCoord(), dashesPerSquare);
		Map<Piece<?>, String> effectMap = new HashMap<Piece<?>, String>();
		for (int y = 0; y < board.getBoardExtremity().getYCoord(); y++){
			builder.append("    |");
			printRowBorder(builder, board.getBoardExtremity().getXCoord(), dashesPerSquare);
			int currentLine = dashesPerSquare /2;
			int midYOfSquare = (int)Math.ceil((double)currentLine / 2);
			for (int i = currentLine; i >0; i--){
				if (i == midYOfSquare){
					builder.append(String.format("%3d |", y));
					printEffectRow(board, builder, effectMap, y, dashesPerSquare, newMove);
				}else{
					builder.append("    |");
					printRowBorder(builder, board.getBoardExtremity().getXCoord(), dashesPerSquare, false);
				}
			}
		}
		builder.append("    |");
		printRowBorder(builder, board.getBoardExtremity().getXCoord(), dashesPerSquare, true);
		builder.append("\nKey:\n");
		for (Map.Entry<Piece<?>, String> piece: effectMap.entrySet()){
			
			builder.append(piece.getValue());
			builder.append(": {player: ");
			builder.append(piece.getKey().getPlayer().getPlayerId());
		}
		builder.append("\n");
		return builder.toString();
	}
	
	private static <T extends PieceDescription<T>> void printEffectRow(Board<T> board, StringBuilder builder, Map<Piece<?>, String> effectMap, int y, int dashesPerSquare, Position newMove) {
		for (int x = 0; x < board.getBoardExtremity().getXCoord(); x++){
			Iterable<Piece<T>> pieces = board.getPiecesAtPlacement(new Position(x, y));
			
			StringBuilder effectTags = new StringBuilder();
			for (Piece<T> piece: pieces){
				String effectTag;
				if (effectMap.containsKey(piece)){
					effectTag = effectMap.get(piece);
				} else{
					effectTag = piece.toString();
					effectMap.put(piece, effectTag);
				}
				effectTags.append(effectTag);
			}
			if (newMove != null && newMove.getXCoord() == x && newMove.getYCoord() == y){
				effectTags.append("*");
			}
			printTag(dashesPerSquare-1, effectTags.toString(), builder);
			builder.append("|");
		}
		builder.append("\n");
	}

	private static void printXIndices(StringBuilder builder, int xMax, int dashesPerSquare) {

		builder.append("    ");
		
		for (int x = 0; x < xMax; x++){
			printTag(dashesPerSquare, String.valueOf(x), builder);
		}
		builder.append("\n");
	}
	
	private static void printTag(int dashesPerSquare, String tag, StringBuilder builder){
			
			builder.append(StringUtils.center(tag, dashesPerSquare));
	}
	
	private static int getDashesPerSquare(int imageMax, int xMax){
		return imageMax / xMax;
	}
	
	private static StringBuilder printRowBorder(StringBuilder builder, int xMax, int dashesPerSquare){
		return printRowBorder(builder, xMax, dashesPerSquare, true);
	}

	private static StringBuilder printRowBorder(StringBuilder builder, int xMax, int dashesPerSquare, boolean printYBorder){
		for (int x = 0; x < xMax; x++){
			for (int i = 0; i < dashesPerSquare-1; i++){
				if (printYBorder){
					builder.append("-");
				} else{
					builder.append(" ");
				}
			}
			builder.append("|");
		}
		builder.append("\n");
		return builder;
	}
}
