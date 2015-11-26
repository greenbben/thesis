package tarble2;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class JFramePVP extends JFrame implements MouseListener {
	// Set to true in order to record game.
	private final boolean collectGameData = true;
	
	private Gamestate state;
	private int filled;
	private int boardSize, xCenter, yCenter;
	private Map<Integer, Color>  teamColors;
	
	Collection<Gamestate> history = new ArrayList<Gamestate>();
	
	public JFramePVP() {
		init();
	}
	
	public static void main(String[] args) {
	    SwingUtilities.invokeLater(new Runnable(){
	        @Override
	        public void run() {
	            new JFramePVP();
	        }
	    });
	}
	
	public void init() {
		boardSize = Math.min(this.getWidth(), this.getHeight());
		xCenter = this.getWidth() / 2;
		yCenter = this.getHeight() / 2;
		state = GamestateUtils.getStartingGamestate();
		history.add(state);
		teamColors = new HashMap<Integer, Color>();
		teamColors.put(0, Color.BLACK);
		teamColors.put(1, Color.RED);
		teamColors.put(2, Color.BLUE);
		this.addMouseListener(this);
		filled = Integer.MAX_VALUE;
		
		 setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 pack();
		 setLocationRelativeTo(null);
		 setVisible(true);
		 this.setSize(new Dimension(500, 500));
	}
	
	public void paint(Graphics g) {
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		boardSize = Math.min(this.getWidth(), this.getHeight());
		xCenter = this.getWidth() / 2;
		yCenter = this.getHeight() / 2;
		Font font = new Font("Arial", 0, boardSize / 20);
		g.setFont(font);
		g.drawString("Turn: " + (state.isPlayer1Turn() ? "Red" : "Blue"), 
				xCenter / 100, yCenter / 100 + boardSize / 20);
		g.drawString("Roll: " + state.getRoll(), xCenter / 100, yCenter / 100 + boardSize / 10);
		g.drawString("Red: " + state.getScore1(), xCenter / 100, yCenter / 100 + 3 * boardSize / 20);
		g.drawString("Blue: " + state.getScore2(), xCenter / 100, yCenter / 100 + boardSize / 5);
		int winner = GamestateUtils.getWinner(state);
		if (winner != 0) {
			if (winner == -1) {
				JOptionPane.showMessageDialog(this, "The game has ended in a tie.");
			}
			System.out.println("winner");
			JOptionPane.showMessageDialog(this, (winner == 1 ? "Red" : "Blue") + " wins!!!");
			if (collectGameData) {
				History.writeHistory(history, Constants.GAME_DIR);
			}
			System.exit(202);
		}
		System.out.println(state);
		drawBoard(g, boardSize, xCenter, yCenter, filled, state);
	}
	
	/**
	 * Does not support even number of rows
	 * 
	 * @param pieces Pieces is a map from a row number to a map from a column number to the team.
	 */
	private void drawBoard(
			Graphics g, int size, int xcenter, int ycenter, int filled, Gamestate state) {
		int rowHeight = size / Constants.ROWS;
		for (int i = -Constants.ROWS / 2; i < Constants.ROWS / 2 + 1; ++i) {
			drawRow(g, Constants.COLS - Math.abs(i), size - rowHeight * Math.abs(i), 
					xcenter, ycenter - i * rowHeight * 3 / 4, i, state);
		}
	}
	
	private void drawRow(
			Graphics g, int cols, int size, int xcenter, int ycenter, int row, Gamestate state) {
		int hexSize = size / cols;
		for (int i = -cols / 2; i < cols / 2 + cols % 2; ++i) {
			int pieceLocation = adjustPieceLocation(i, row);
			boolean isFilled = filled == pieceLocation;
			if (state.getTeam1().contains(pieceLocation)) {
				drawHexagon(g, 
						xcenter + hexSize * i + hexSize * ((cols + 1) % 2) / 2, 
						ycenter, hexSize, 1, isFilled);
			} else if (state.getTeam2().contains(pieceLocation)) {
				drawHexagon(g, 
						xcenter + hexSize * i + hexSize * ((cols + 1) % 2) / 2, 
						ycenter, hexSize, 2, isFilled);
			} else if (state.isNeutralPlaced() && pieceLocation == Constants.NEUTRAL_LOCATION) {
				drawHexagon(g, 
						xcenter + hexSize * i + hexSize * ((cols + 1) % 2) / 2, 
						ycenter, hexSize, 0, isFilled);
			} else {
				drawHexagon(g, 
						xcenter + hexSize * i + hexSize * ((cols + 1) % 2) / 2, 
						ycenter, hexSize);
			}
		}
	}
	
	private void drawHexagon(Graphics g, int xcenter, int ycenter, int size) {
		int[] ypoints = {ycenter - size / 4, ycenter + size / 4, ycenter + size / 2, 
				ycenter + size / 4, ycenter - size / 4, ycenter - size / 2};
		int[] xpoints = {xcenter - size / 2, xcenter - size / 2, xcenter, 
				xcenter + size / 2, xcenter + size / 2, xcenter};
		g.drawPolygon(xpoints, ypoints, 6);
	}
	
	private void fillHexagon(Graphics g, int xcenter, int ycenter, int size) {
		int[] ypoints = {ycenter - size / 4, ycenter + size / 4, ycenter + size / 2, 
				ycenter + size / 4, ycenter - size / 4, ycenter - size / 2};
		int[] xpoints = {xcenter - size / 2, xcenter - size / 2, xcenter, 
				xcenter + size / 2, xcenter + size / 2, xcenter};
		g.fillPolygon(xpoints, ypoints, 6);
	}
	
	private void drawHexagon(Graphics g, int xcenter, int ycenter, int size, int team, boolean isFilled) {
		if (isFilled) {
			fillHexagon(g, xcenter, ycenter, size);
		} else {
			drawHexagon(g, xcenter, ycenter, size);
		}
		g.setColor(teamColors.get(team));
		g.fillOval(xcenter - size / 3, ycenter - size / 3, 2 * size / 3, 2 * size / 3);
		g.setColor(Color.BLACK);
	}
	
	private int adjustPieceLocation(int col, int row) {
		int ret = col + row / 2;
		if (row < 0) {
			ret += row % 2;
		}
		return (((ret + (((row - (row < 0 ? 1 : 0)) / 2) * -2)) + 5) * 100) + row + 5;
	}
	
	private void move(Gamestate move) {
		state = move;
		history.add(move);
		filled = Integer.MAX_VALUE;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// Find clicked location.
		int deltaY = 0;
		if (e.getY() < yCenter) {
			deltaY = -1;
		} else {
			deltaY = 1;
		}
		int y = -(e.getY() - yCenter + deltaY * boardSize / 
				(Constants.ROWS * 2)) / ((3 * boardSize) / (4 * Constants.ROWS));
		int deltaX = -1-y;
		int initialX = (int)(1.0 * e.getX() - xCenter + deltaX * boardSize / (Constants.COLS * 2));
		int x = initialX / (boardSize / Constants.COLS);
		if (initialX > 0) {
			x++;
		}
		int clicked = ((x + 5) * 100) + y + 5;
			
		if (filled == Integer.MAX_VALUE) {
			// Only set filled if the clicked location is a valid location for the moving player.
			if ((state.isPlayer1Turn() && state.getTeam1().contains(clicked)) 
					|| (!state.isPlayer1Turn() && state.getTeam2().contains(clicked))) {
				filled = clicked;
			} else {
				System.out.println((state.isPlayer1Turn() ? "player 1" : "player 2") + " does not have a piece at " + clicked);
			}
		} else {
			// Filled already set tryingto move.
			Gamestate move = GamestateUtils.move(state, filled, clicked);
			if (GamestateUtils.isLegalMove(state, move)) {
				// Successful move.
				move(move);
			} else {
				// Illegal move.
				System.out.println("not a legal move");
				filled = Integer.MAX_VALUE;
			}
		}
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}