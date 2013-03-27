package com.haines.ist.checkers.ui;

import com.haines.ist.checkers.ui.swing.BoardAssetFailureException;
import com.haines.ist.checkers.ui.swing.CheckersFrame;

public class SwingCheckersApplication {

	private static final String ALPHA_PROP = "useAlphaBetaPrunning";
	private static final String CACHE_PROP = "useCaching";

	public static void main(String[] args) throws BoardAssetFailureException{
		
		boolean useAlphaBetaPrunning = true;
		boolean useCaching = true;
		
		for (String arg: args){
			String[] prop = arg.split("=");
			if (prop[0].equalsIgnoreCase(ALPHA_PROP) ){
				useAlphaBetaPrunning = Boolean.parseBoolean(prop[1]);
			} else if (prop[0].equalsIgnoreCase(CACHE_PROP)){
				useCaching = Boolean.parseBoolean(prop[1]);
			}
		}
		
		CheckersFrame frame = new CheckersFrame(useAlphaBetaPrunning, useCaching);
		
		frame.setVisible(true);
	}
}
