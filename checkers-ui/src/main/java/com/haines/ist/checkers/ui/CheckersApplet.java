package com.haines.ist.checkers.ui;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

import com.haines.ist.checkers.ui.swing.BoardAssetFailureException;
import com.haines.ist.checkers.ui.swing.CheckersFrame;
import com.haines.ist.checkers.ui.swing.CheckersPanel;

public class CheckersApplet extends JApplet{

	@Override
    public void init() {
        //Execute a job on the event-dispatching thread; creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    try {
						createCheckersBoard();
					} catch (BoardAssetFailureException e) {
						throw new RuntimeException(e);
					}
                }
            });
        } catch (Exception e) { 
        	e.printStackTrace();
            System.err.println("Unable to load checkers board.");
        }
    }

	protected void createCheckersBoard() throws BoardAssetFailureException {
		CheckersPanel frame = new CheckersPanel(true, false);
		
		setContentPane(frame);
		
	}
}
