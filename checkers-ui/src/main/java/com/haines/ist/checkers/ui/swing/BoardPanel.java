package com.haines.ist.checkers.ui.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ahaines.ai.search.game.GameFinishedException;
import com.ahaines.ai.search.minmax.model.TurnDrivenState.SimpleTurn;
import com.ahaines.boardgame.model.Piece;
import com.ahaines.boardgame.model.Position;
import com.ahaines.checker.service.CheckersGame;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription;
import com.ahaines.checkers.model.CheckersPiece.CheckersPieceDescription.Type;
import com.ahaines.checkers.model.Move;

public class BoardPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener{

	private static final String CHECKERS_IMAGE_LOCATION = "/assets/mainBoard.jpg";
	private static final String PLAYER1_PIECE_IMAGE_LOCATION = "/assets/ply1_piece.png";
	private static final String PLAYER1_KING_PIECE_IMAGE_LOCATION = "/assets/ply1_piece_king.png";
	private static final String PLAYER2_PIECE_IMAGE_LOCATION = "/assets/ply2_piece.png";
	private static final String PLAYER2_KING_PIECE_IMAGE_LOCATION = "/assets/ply2_piece_king.png";
	private static final Logger LOG = LoggerFactory.getLogger(BoardPanel.class);
	private static final String RULES_TEXT = "1.) Each player starts out with 12 playing pieces. One player has light colored pieces (called “white”) and the other has \n    dark pieces (called “black” although the pieces may actually be red). \n    Each player starts with their pieces laid out on the 12 dark squares nearest him or her.\n\n" +
											 "2.) The player with the black pieces moves first.\n\n" +
											 "3.) There are two ways to move a piece:\n\n" +
											 "         - A simple move involves sliding a piece one space diagonally forwards to an adjacent unoccupied dark square.\n" +
											 "         - A jump is a move from a square diagonally adjacent to one of the opponent's pieces to an empty square immediately \n" +
											 "           and directly on the opposite side of the opponent's square thus jumping directly over the square containing the \n" +
											 "           opponent's piece\n" +
											 "         - An uncrowned piece may only jump diagonally forwards, kings may also jump diagonally backwards.\n" +
											 "         - A piece that is jumped is captured and removed from the board\n" +
											 "         - Multiple-jump moves are possible if when the jumping piece lands, there is another immediate piece that can be jumped\n" +
											 "           even if the jump is in a different direction. Hold 'Ctrl' key whilst dragging to define multiple moves\n" +
											 "         - Jumping is mandatory – whenever a player has the option to jump, that person must jump\n\n" +
											 "4.) If a player's piece moves into the kings row on the opposing player's side of the board, that piece is said to be crowned\n," +
											 "    becoming a king and gaining the ability to move both forwards and backwards. If a player's piece jumps into the kings row,\n" +
											 "    the current move terminates\n\n" +
											 "5.) A player wins by capturing all of the opposing player's pieces or by leaving the opposing player with no legal moves";
	
	private final Image checkersBoardAsset;
	private final Image player1PieceAsset;
	private final Image player1KingPieceAsset;
	private final Image player2PieceAsset;
	private final Image player2KingPieceAsset;
	private final GameWonListener listener;
	private CheckersGame game;
	private boolean dragging;
	private Position pieceBeingMoved;
	private Position lastPositionPressed;
	private Point draggedTo;
	private Deque<Move> moves = new ArrayDeque<Move>();
	
	public BoardPanel(GameWonListener listener) throws BoardAssetFailureException{
		try {
			this.listener = listener;
			checkersBoardAsset = ImageIO.read(BoardPanel.class.getResourceAsStream(CHECKERS_IMAGE_LOCATION));
			player1PieceAsset = ImageIO.read(BoardPanel.class.getResourceAsStream(PLAYER1_PIECE_IMAGE_LOCATION));
			player1KingPieceAsset = ImageIO.read(BoardPanel.class.getResourceAsStream(PLAYER1_KING_PIECE_IMAGE_LOCATION));
			player2PieceAsset = ImageIO.read(BoardPanel.class.getResourceAsStream(PLAYER2_PIECE_IMAGE_LOCATION));
			player2KingPieceAsset = ImageIO.read(BoardPanel.class.getResourceAsStream(PLAYER2_KING_PIECE_IMAGE_LOCATION));
			
		} catch (IOException e) {
			throw new BoardAssetFailureException("unable to load asserts for rendering board", e);
		}
		
		setUpDragAndDropListeners();
	}

	private void setUpDragAndDropListeners() {
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(checkersBoardAsset, 0, 0, getWidth(), getHeight(), null);
		
		if (game != null){
			int xborder = (int)(getWidth()*0.007);
			int yborder = (int)(getHeight()*0.0060);
			int xOffset = (int)(getWidth()*0.040);
			int yOffset = (int)(getHeight()*0.040); // remove border - border is 3% big on image
			int boardWidth = (int)(getWidth() - (xOffset * 2)); 
			int boardHeight = (int)(getHeight() - (yOffset * 2)); // remove border
			int pieceWidth = (int)(boardWidth / 8);
			int pieceHeight = (int)(boardHeight / 8);
			
			
			for (Piece<CheckersPieceDescription> piece: game.getCurrentState().getPieces()){
				
				Image pieceAsset;
				if (piece.getPlayer().getPlayerId().getId() == SimpleTurn.MAX.getId()){
					if (piece.getPiece().getType() == Type.STANDARD){
						pieceAsset = player1PieceAsset;
					} else{
						pieceAsset = player1KingPieceAsset;
					}
				} else{
					if (piece.getPiece().getType() == Type.STANDARD){
						pieceAsset = player2PieceAsset;
					} else{
						pieceAsset = player2KingPieceAsset;
					}
				}

				int x;
				int y;
				if (pieceBeingMoved != null && pieceBeingMoved.equals(piece.getPlacement())){

					if (dragging){ // position piece where the mouse currently is
						x = (int)draggedTo.getX() - (pieceWidth / 2);
						y = (int)draggedTo.getY() - (pieceHeight / 2);
					} else{ // otherwise pop last position off the deque, and draw lines for each move position
						Move newLocation = moves.peekFirst();
						
						x = (xOffset/2) + newLocation.getTo().getXCoord()*(pieceWidth + xborder);
						y = (yOffset/2) + newLocation.getTo().getYCoord()*(pieceHeight + yborder);
						
						// now iterator through all the moves and draw lines from and to the moves
						
						for (Move move: moves){
							int lineXFrom = (xOffset/2) + move.getFrom().getXCoord()*(pieceWidth + xborder) + (pieceWidth/2);
							int lineYFrom = (yOffset/2) + move.getFrom().getYCoord()*(pieceHeight + yborder) + (pieceHeight/2);
							int lineXTo = (xOffset/2) + move.getTo().getXCoord()*(pieceWidth + xborder) + (pieceWidth/2);
							int lineYTo = (yOffset/2) + move.getTo().getYCoord()*(pieceHeight + yborder) + (pieceHeight/2);
							Graphics2D g2 = (Graphics2D) g;
					        g2.setStroke(new BasicStroke(3));
					        g2.setColor(Color.RED);
							g.drawLine(lineXFrom, lineYFrom, lineXTo, lineYTo);
						}
					}
						
				} else{ // statically render it's location
					x = (xOffset/2) + piece.getPlacement().getXCoord()*(pieceWidth + xborder);
					y = (yOffset/2) + piece.getPlacement().getYCoord()*(pieceHeight + yborder);
				}
				g.drawImage(pieceAsset,x, y, pieceWidth, pieceHeight, null);
			}
		}
	}

	public void endGame() {
		this.game = null;
		this.repaint();
		
	}

	public void setNewGame(CheckersGame game) {
		this.game = game;
		this.repaint();
	}

	public void mouseClicked(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {
		
		// calculate the x/y position of this piece
		if (pieceBeingMoved == null){
			this.pieceBeingMoved = getPiecePositionFromMouseEvent(e);
		}
		
		this.dragging = true;
		
		lastPositionPressed = getPiecePositionFromMouseEvent(e);
	}
	
	private Position getPiecePositionFromMouseEvent(MouseEvent e){
		int xborder = (int)(getWidth()*0.007);
		int yborder = (int)(getHeight()*0.0060);
		int boardWidth = (int)(getWidth()); 
		int boardHeight = (int)(getHeight()); // remove border
		int pieceWidth = (int)(boardWidth / 8);
		int pieceHeight = (int)(boardHeight / 8);
		int x = (e.getX() - xborder) / pieceWidth;
		int y = (e.getY() - yborder) / pieceHeight;
		
		return new Position(x ,y);
	}

	public void mouseReleased(MouseEvent event) {
		if (game != null){

				this.dragging = false;
				
				Position draggedNewPosition = getPiecePositionFromMouseEvent(event);
				if (!pieceBeingMoved.equals(draggedNewPosition)){ // no change to move
					
					if (!lastPositionPressed.equals(draggedNewPosition)){
						Move newMove = new Move(lastPositionPressed, draggedNewPosition);
						moves.push(newMove);
						LOG.debug("Adding move: "+newMove);
					}
					if (!event.isControlDown()){
						LOG.debug("Releasing position to: "+draggedNewPosition);
						try {
							game.playMove(moves);
							if (game.isWon()){
								listener.gameWonForUser();
							} else{
								game.getNextMove();
								LOG.debug("computer moved to: "+game.getCurrentState().getNewMove());
								if (game.isWon()){
									listener.gameWonForComp();
								}
							}
						} catch (GameFinishedException e) {
							JOptionPane.showMessageDialog(this,
								    "The game has already been won. Please stop playing!");
						} catch (Exception e){
							Object[] options = {"Yes, please",
				                    "No, thanks"};
							
							int selection = JOptionPane.showOptionDialog(this,
							    e.getMessage()+".\nDo you want to see the rules?", "Illegal Move!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[1]);
							LOG.error("Error playing moves. "+moves, e);
							
							if (selection == 0){
								showRules();
							}
						}
						clearMove();
					}
				} else{
					clearMove();
				}
				repaint();
		}
		
	}

	public void showRules() {
		JOptionPane.showMessageDialog(this, RULES_TEXT, "Rules", JOptionPane.INFORMATION_MESSAGE);
	}

	private void clearMove() {
		this.pieceBeingMoved = null;
		moves.clear();
		draggedTo = null;
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mouseDragged(MouseEvent e) {
		draggedTo = e.getPoint();
		repaint();
	}

	public void mouseMoved(MouseEvent e) {}

	public void keyTyped(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE && !dragging){
			clearMove();
		}
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
