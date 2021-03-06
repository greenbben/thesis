package tarble;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

public class main extends Applet implements MouseListener {

	private static final int MAX = Integer.MAX_VALUE;
	private final int cols = 11;
	private final int rows = 11;
	private int boardSize, xCenter, yCenter;
	private Pair<Integer, Integer> filled;
	private Map<Integer, Color>  teamColors;
	private Board board;
	
	public void init() {
		boardSize = Math.min(this.getWidth(), this.getHeight());
		xCenter = this.getWidth() / 2;
		yCenter = this.getHeight() / 2;
		this.addMouseListener(this);
		filled = new Pair<Integer, Integer>(MAX, MAX);
		teamColors = new HashMap<Integer, Color>();
		teamColors.put(0, Color.BLACK);
		teamColors.put(1, Color.RED);
		teamColors.put(2, Color.BLUE);
		
		board = Gamestate.setUpBoard();
	}
	
	public void paint(Graphics g) {
		boardSize = Math.min(this.getWidth(), this.getHeight());
		xCenter = this.getWidth() / 2;
		yCenter = this.getHeight() / 2;
//		Map<Integer, Map<Integer, Integer>> pieces = new HashMap<Integer, Map<Integer, Integer>>();
//		Map<Integer, Integer> row0 = new HashMap<Integer, Integer>();
//		row0.put(1, 1);
//		pieces.put(3, row0);
		drawBoard(g, rows, cols, boardSize, xCenter, yCenter, filled, convertBoard(board.getPieces()));
	}
	
	/**
	 * Does not support even number of rows
	 * 
	 * @param pieces Pieces is a map from a row number to a map from a column number to the team.
	 */
	private void drawBoard(Graphics g, int rows, int cols, int size, 
			int xcenter, int ycenter, Pair<Integer, Integer> filled, 
			Map<Integer, Map<Integer, Integer>> pieces) {
		int rowHeight = size / rows;
		for (int i = -rows / 2; i < rows / 2 + 1; ++i) {
			int fillCol = MAX;
			if (filled.getSecond() == i) {
				fillCol = adjustFillCol(filled.getFirst(), i);
			}
			if (pieces.containsKey(i)) {
				drawRow(g, cols - Math.abs(i), size - rowHeight * Math.abs(i), 
						xcenter, ycenter - i * rowHeight * 3 / 4, fillCol, pieces.get(i), i);
			} else {
				drawRow(g, cols - Math.abs(i), size - rowHeight * Math.abs(i), 
						xcenter, ycenter - i * rowHeight * 3 / 4, fillCol);
			}
		}
	}
	
	private int adjustCol(int col, int row) {
		int ret = col + row / 2;
		if (row < 0) {
			ret += row % 2;
		}
		return ret + (((row - (row < 0 ? 1 : 0)) / 2) * -2);
	}
	
	private int adjustFillCol(int col, int row) {
		int ret = col + row / 2;
		if (row < 0) {
			ret += row % 2;
		}
		return ret;
	}
	
	private void drawRow(Graphics g, int cols, int size, int xcenter, int ycenter, 
			int fillCol, Map<Integer, Integer> pieces, int row) {
		int hexSize = size / cols;
		for (int i = -cols / 2; i < cols / 2 + cols % 2; ++i) {
			int adjustedCol = adjustCol(i, row);
			if (pieces.containsKey(adjustedCol)) {
				if (i == fillCol) {
					fillHexagon(g, 
							xcenter + hexSize * i + hexSize * ((cols + 1) % 2) / 2, 
							ycenter, hexSize, pieces.get(adjustedCol));
				} else {
					drawHexagon(g, 
							xcenter + hexSize * i + hexSize * ((cols + 1) % 2) / 2, 
							ycenter, hexSize, pieces.get(adjustedCol));
				}
			} else {
				if (i == fillCol) {
					fillHexagon(g, 
							xcenter + hexSize * i + hexSize * ((cols + 1) % 2) / 2, 
							ycenter, hexSize);
				} else {
					drawHexagon(g, 
							xcenter + hexSize * i + hexSize * ((cols + 1) % 2) / 2, 
							ycenter, hexSize);
				}
			}
		}
	}
	
	private void drawRow(Graphics g, int cols, int size, int xcenter, int ycenter, 
			int fillCol) {
		int hexSize = size / cols;
		for (int i = -cols / 2; i < cols / 2 + cols % 2; ++i) {
			if (i == fillCol) {
				fillHexagon(g, 
						xcenter + hexSize * i + hexSize * ((cols + 1) % 2) / 2, 
						ycenter, hexSize);
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

	private void drawHexagon(Graphics g, int xcenter, int ycenter, int size, int piece) {
		drawHexagon(g, xcenter, ycenter, size);
		g.setColor(teamColors.get(piece));
		g.drawOval(xcenter - size / 3, ycenter - size / 3, 2 * size / 3, 2 * size / 3);
		g.setColor(Color.BLACK);
	}

	private void fillHexagon(Graphics g, int xcenter, int ycenter, int size, int piece) {
		fillHexagon(g, xcenter, ycenter, size);
		g.setColor(teamColors.get(piece));
		g.drawOval(xcenter - size / 3, ycenter - size / 3, 2 * size / 3, 2 * size / 3);
		g.setColor(Color.BLACK);
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {
		int deltaY = 0;
		if (event.getY() < yCenter) {
			deltaY = -1;
		} else {
			deltaY = 1;
		}
		int y = -(event.getY() - yCenter + deltaY * boardSize / (rows * 2)) / ((3 * boardSize) / (4 * rows));
		int deltaX = -1-y;
		int initialX = (int)(1.0 * event.getX() - xCenter + deltaX * boardSize / (cols * 2));
		int x = initialX / (boardSize / cols);
		if (initialX > 0) {
			x++;
		}

		filled = new Pair<Integer, Integer>(x, y);
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private Map<Integer, Map<Integer, Integer>> convertBoard(
			Map<Pair<Integer, Integer>, Integer> oldBoard) {
		System.out.println(oldBoard);
		Map<Integer, Map<Integer, Integer>> ret = new HashMap<Integer, Map<Integer, Integer>>();
		for (int i = -(rows / 2); i <= rows / 2; ++i) {
			ret.put(i, new HashMap<Integer, Integer>());
		}
		for (Pair<Integer, Integer> place : oldBoard.keySet()) {
			ret.get(place.getSecond()).put(place.getFirst(), oldBoard.get(place));
		}
		System.out.println(ret);
		return ret;
	}
}
