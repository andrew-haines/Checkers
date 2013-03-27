package com.haines.ist.checkers.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ahaines.ai.search.game.GameFinishedException;
import com.ahaines.ai.search.minmax.model.TurnDrivenState.SimpleTurn;
import com.ahaines.boardgame.model.Piece;
import com.ahaines.boardgame.model.Player;
import com.ahaines.boardgame.model.Player.PlayerType;
import com.ahaines.boardgame.model.SimplePlayerId;
import com.ahaines.checker.service.BoardIdService;
import com.ahaines.checker.service.CheckersGame;
import com.ahaines.checker.service.CheckersPlayerLookup;
import com.ahaines.checker.service.IncrementalBoardIdService;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription;

public class CheckersPanel extends JPanel implements GameWonListener{

	private static final String ENTER_NAME_TEXT = "Enter Your name";

	private static final String COMP_NAME = "Henry";

	private static final String START_NEW_GAME_TEXT = "Start new game";
	
	private JTextField playerName;
	private JTextField compName;
	private JSlider difficulty;
	private BoardPanel boardPanel;
	private CheckersGame game;
	private JButton startGameButton;
	
	public CheckersPanel(boolean useAlphaBetaPrunning, boolean useCaching) throws BoardAssetFailureException{
		
		
		setUpLayouts();
		setUpControls(useAlphaBetaPrunning, useCaching);
		setUpBoard();
	}

	private void setUpBoard() throws BoardAssetFailureException {
		boardPanel = new BoardPanel(this);
		
		this.add(boardPanel, BorderLayout.CENTER);
	}

	private void setUpLayouts() {
		LayoutManager manager = new BorderLayout();
		setLayout(manager);
	}

	private void setUpControls(final boolean useAlphaBetaPrunning, final boolean useCaching) {
		JPanel controls = new JPanel();
		
		controls.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		JLabel difficultyLabel = new JLabel("Difficulty", JLabel.CENTER);
		difficultyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		difficulty = new JSlider(JSlider.HORIZONTAL, 1, 9, 7);
		difficulty.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				if (game != null){
					game.setDepthLimit(difficulty.getValue());;
				}
			}
			
		});
		
		startGameButton = new JButton(START_NEW_GAME_TEXT);
		
		JLabel playerNameLabel = new JLabel("Player Name:");
		playerName = new JTextField(ENTER_NAME_TEXT, 10);
		playerName.addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent e) {
				if (playerName.getText().equals(ENTER_NAME_TEXT)){
					playerName.setText("");
				}
			}

			public void focusLost(FocusEvent e) {
				if (playerName.getText().equals("")){
					playerName.setText(ENTER_NAME_TEXT);
				}
			}
			
		});
		
		compName = new JTextField(COMP_NAME, 10);
		compName.setEditable(false);
		
		JLabel compNameLabel = new JLabel("Computer Name:");
		
		JPanel player1 = new JPanel();
		player1.setLayout(new GridLayout(2, 1));
		player1.add(compNameLabel, BorderLayout.CENTER);
		player1.add(compName);
		
		JPanel player2 = new JPanel();
		player2.setLayout(new GridLayout(2, 1));
		player2.add(playerNameLabel, BorderLayout.CENTER);
		player2.add(playerName);
		
		JPanel difficultyPanel = new JPanel();
		difficultyPanel.setLayout(new GridLayout(2, 1));
		difficultyPanel.add(difficultyLabel, BorderLayout.CENTER);
		difficultyPanel.add(difficulty);
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(2, 1));
		
		JButton howToPlay = new JButton("How to play");
		howToPlay.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				boardPanel.showRules();
			}
			
		});
		startGameButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				if (game == null){ // starting a new game
					playerName.setEnabled(false);
					startGameButton.setText("Withdraw Game");
					
					if (playerName.getText().equals(ENTER_NAME_TEXT)){
						playerName.setText("George");
					}
					
					Player<CheckersPieceDescription> compPlayer = new Player<CheckersPieceDescription>(new SimplePlayerId(SimpleTurn.MAX.getId(), compName.getText()), PlayerType.COMPUTER, new ArrayList<Piece<CheckersPieceDescription>>());
					Player<CheckersPieceDescription> player = new Player<CheckersPieceDescription>(new SimplePlayerId(SimpleTurn.MIN.getId(), playerName.getText()), PlayerType.HUMAN, new ArrayList<Piece<CheckersPieceDescription>>());

					BoardIdService boardIdService = new IncrementalBoardIdService();
					
					CheckersPlayerLookup playerLookup = new CheckersPlayerLookup(compPlayer, player);
					
					game = new CheckersGame.CheckersGameBuilder(playerLookup, boardIdService, SimpleTurn.class)
							.setStartingTurn(SimpleTurn.MAX)
							.useAlphaBetaPrunning(useAlphaBetaPrunning)
							.useCaching(false)
							.setDepthLimit(difficulty.getValue())
							.build();
					
					try {
						game.getNextMove();
					} catch (GameFinishedException e1) {
						playerName.setEditable(true);
						boardPanel.endGame();
						CheckersPanel.this.gameWon(playerLookup.getPlayer(game.getCurrentState().getTurn().getId()).getPlayerId().getPlayerName());
					}
					boardPanel.setNewGame(game);
					
					
				} else{ // withdraw game
					game = null;
					startGameButton.setText(START_NEW_GAME_TEXT);
					playerName.setEditable(true);
					boardPanel.endGame();
					CheckersPanel.this.gameWon(compName.getText());
				}
			}			
		});
		
		buttons.add(howToPlay);
		buttons.add(startGameButton);
		
		controls.add(player1);
		controls.add(new JLabel("    Vs    "));
		controls.add(player2);
		controls.add(difficultyPanel);
		controls.add(buttons);
		this.add(controls, BorderLayout.NORTH);
	}
	
	private void gameWon(String winner){
		JOptionPane.showMessageDialog(this,
			    winner+" has won the game!");
		
	}

	private void resetUI(){
		startGameButton.setText(START_NEW_GAME_TEXT);
		playerName.setEditable(true);
	}

	public void gameWonForUser() {
		JOptionPane.showMessageDialog(this,
			    "You Won! Have a biscuit");
		resetUI();
	}

	public void gameWonForComp() {
		JOptionPane.showMessageDialog(this,
				"You Lost! Give the computer a biscuit");
		resetUI();
	}
}
