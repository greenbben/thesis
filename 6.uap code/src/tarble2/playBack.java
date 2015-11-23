package tarble2;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class playBack extends Applet implements ActionListener {
	private Button forwardButton;
	private Button backButton;
	
	private List<Gamestate> history;
	private int boardSize, xCenter, yCenter;
	private Map<Integer, Color>  teamColors;
	private int historyIndex = 0;
	
	public void init() {
		forwardButton = new Button("Forward");
		this.add(forwardButton);
		forwardButton.addActionListener(this);
		backButton = new Button("Backward");
		this.add(backButton);
		backButton.addActionListener(this);
		
		boardSize = Math.min(this.getWidth(), this.getHeight());
		xCenter = this.getWidth() / 2;
		yCenter = this.getHeight() / 2;
		teamColors = new HashMap<Integer, Color>();
		teamColors.put(0, Color.BLACK);
		teamColors.put(1, Color.RED);
		teamColors.put(2, Color.BLUE);
		history = History.readHistory(Constants.GAME_DIR + "game.txt");
	}
	
	public void paint(Graphics g) {
		boardSize = Math.min(this.getWidth(), this.getHeight());
		xCenter = this.getWidth() / 2;
		yCenter = this.getHeight() / 2;
		Font font = new Font("Arial", 0, boardSize / 20);
		g.setFont(font);
		Gamestate state = history.get(historyIndex);
		g.drawString("Turn: " + (state.isPlayer1Turn() ? "Red" : "Blue"), 
				xCenter / 100, yCenter / 100 + boardSize / 20);
		g.drawString("Roll: " + state.getRoll(), xCenter / 100, yCenter / 100 + boardSize / 10);
		g.drawString("Red: " + state.getScore1(), xCenter / 100, yCenter / 100 + 3 * boardSize / 20);
		g.drawString("Blue: " + state.getScore2(), xCenter / 100, yCenter / 100 + boardSize / 5);
		drawBoard(g, boardSize, xCenter, yCenter, state);
	}
	
	/**
	 * Does not support even number of rows
	 * 
	 * @param pieces Pieces is a map from a row number to a map from a column number to the team.
	 */
	private void drawBoard(
			Graphics g, int size, int xcenter, int ycenter, Gamestate state) {
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
			if (state.getTeam1().contains(pieceLocation)) {
				drawHexagon(g, 
						xcenter + hexSize * i + hexSize * ((cols + 1) % 2) / 2, 
						ycenter, hexSize, 1);
			} else if (state.getTeam2().contains(pieceLocation)) {
				drawHexagon(g, 
						xcenter + hexSize * i + hexSize * ((cols + 1) % 2) / 2, 
						ycenter, hexSize, 2);
			} else if (state.isNeutralPlaced() && pieceLocation == Constants.NEUTRAL_LOCATION) {
				drawHexagon(g, 
						xcenter + hexSize * i + hexSize * ((cols + 1) % 2) / 2, 
						ycenter, hexSize, 0);
			} else {
				drawHexagon(g, 
						xcenter + hexSize * i + hexSize * ((cols + 1) % 2) / 2, 
						ycenter, hexSize);
			}
		}
	}
	
	private void drawHexagon(Graphics g, int xcenter, int ycenter, int size, int team) {
		drawHexagon(g, xcenter, ycenter, size);
		g.setColor(teamColors.get(team));
		g.fillOval(xcenter - size / 3, ycenter - size / 3, 2 * size / 3, 2 * size / 3);
		g.setColor(Color.BLACK);
	}
	
	private void drawHexagon(Graphics g, int xcenter, int ycenter, int size) {
		int[] ypoints = {ycenter - size / 4, ycenter + size / 4, ycenter + size / 2, 
				ycenter + size / 4, ycenter - size / 4, ycenter - size / 2};
		int[] xpoints = {xcenter - size / 2, xcenter - size / 2, xcenter, 
				xcenter + size / 2, xcenter + size / 2, xcenter};
		g.drawPolygon(xpoints, ypoints, 6);
	}
	
	private int adjustPieceLocation(int col, int row) {
		int ret = col + row / 2;
		if (row < 0) {
			ret += row % 2;
		}
		return (((ret + (((row - (row < 0 ? 1 : 0)) / 2) * -2)) + 5) * 100) + row + 5;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == forwardButton) {
			if (historyIndex < history.size() - 1) {
				historyIndex++;
			}
		} else if (e.getSource() == backButton) {
			if (historyIndex > 0) {
				historyIndex--;
			}
		}
		repaint();
	}
}