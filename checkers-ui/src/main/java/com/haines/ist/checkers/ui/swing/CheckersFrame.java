package com.haines.ist.checkers.ui.swing;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

public class CheckersFrame extends JFrame implements WindowListener{

	private CheckersPanel panel;
	
	public CheckersFrame(boolean useAlphaBetaPrunning, boolean useCaching) throws BoardAssetFailureException{
		panel = new CheckersPanel(useAlphaBetaPrunning, useCaching);
		this.setLayout(new GridLayout(1, 1));
		this.addWindowListener(this);
		this.add(panel);
		setupFrameSize();
	}
	
	public void windowOpened(WindowEvent e) {
		
	}

	public void windowClosing(WindowEvent e) {
		System.exit(1);
	}

	public void windowClosed(WindowEvent e) {
		System.exit(1);
	}

	public void windowIconified(WindowEvent e) {
		
	}

	public void windowDeiconified(WindowEvent e) {
		
	}

	public void windowActivated(WindowEvent e) {
		
	}

	public void windowDeactivated(WindowEvent e) {
		
	}
	
	private void setupFrameSize() {
		 Toolkit kit = this.getToolkit();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        Insets in = kit.getScreenInsets(gs[0].getDefaultConfiguration());

        Dimension d = kit.getScreenSize();
        int max_width = (d.width - in.left - in.right);
        int max_height = (d.height - in.top - in.bottom);
        
        this.setSize(Math.min(max_width, 770), Math.min(max_height, 800));//whatever size you want but smaller the insets
        this.setLocation((int) (max_width - this.getWidth()) / 2, (int) (max_height - this.getHeight() ) / 2);
	}
}
