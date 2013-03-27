package com.haines.ist.checkers.ui.swing;

import java.io.IOException;

public class BoardAssetFailureException extends Exception {

	public BoardAssetFailureException(String message, IOException cause) {
		super(message, cause);
	}

}
