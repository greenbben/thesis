package tarble;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Board {
	// Map to represent the locations of pieces on the board. 
	// 0 represents an empty hex, 1 represents a neutral
	// piece, and higher integers represent different teams pieces.
	Map<Pair<Integer, Integer>, Integer> pieces;
	
	// Board size constants
	final int HEIGHT = 11;
	final int WIDTH = 11;
	
	public Board(Map<Pair<Integer, Integer>, Integer> pieces) {
		this.pieces = pieces;
	}
	
	// Check if a given coordinate is located on the board.
	public boolean isOnBoard(int x, int y) {
		return Math.abs(x) <= HEIGHT / 2 
				&& Math.abs(y) <= WIDTH / 2 
				&& Math.abs(x + y) <= (((HEIGHT / 2) + (WIDTH / 2)) / 2);
	}
	
	// Prints a human readable version of the board for testing purposes.
	public void printBoard() {
		for (int i = HEIGHT / 2; i >= -1 * HEIGHT / 2; --i) {
			for (int k = 0; k < Math.abs(i); ++k) {
				System.out.print(" ");
			}
			for (int j = -1 * WIDTH / 2; j <= WIDTH / 2; ++j) {
				if (isOnBoard(j, i)) {
					if (pieces.containsKey(new Pair<Integer, Integer>(j, i))) {
						int piece = pieces.get(new Pair<Integer, Integer>(j, i));
						System.out.print("-" + piece);
					} else {
						System.out.print("--");
					}
				}
			}
			System.out.println();
		}
	}
	
	public void move(Pair<Integer, Integer> start, Pair<Integer, Integer> end) {
		// Check if end spot is taken and on board
		if (pieces.containsKey(end) || !isOnBoard(end.getFirst(), end.getSecond())) {
			throw new IllegalArgumentException("The piece can not be moved to this location");
		}
		int piece = pieces.get(start);
		pieces.remove(start);
		pieces.put(end, piece);
	}
	
	public void take(Pair<Integer, Integer> location) {
		pieces.remove(location);
	}
	
	public void placeNeutralPiece() {
		this.pieces.put(new Pair<Integer, Integer>(0, 0), 0);
	}
	
	// returns the pieces at a given location or -1 if the location is empty.
	public int getPiece(Pair<Integer, Integer> location) {
		if (this.pieces.containsKey(location)) {
			return this.pieces.get(location);
		}
		return -1;
	}
	
	public List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getLegalMoves(int roll, int team) {
		return getLegalMoves(roll, team, null);
	}
	
	private List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getLegalMoves(int roll, int team, Pair<Integer, Integer> origin) {
		Board copyBoard = new Board(new HashMap<Pair<Integer, Integer>, Integer>(pieces));
		if (roll == 0) {
			return new ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>();
		}
		List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> legalMoves = 
				new ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>();
		for (Entry<Pair<Integer, Integer>, Integer> position : pieces.entrySet()) {
			if (position.getValue() == team) {
				for (int i = -1; i <= 1; ++i) {
					for (int j = -1; j <= 1; ++j) {
						if (i == 0 && j == 0 || Math.abs(i+j) == 2) {
							continue;
						}
						int x = position.getKey().getFirst() + i;
						int y = position.getKey().getSecond() + j;
						// location taken
						if (copyBoard.getPiece(new Pair<Integer, Integer>(x, y)) == team) {
							continue;
						} else if (copyBoard.getPiece(new Pair<Integer, Integer>(x, y)) >= 0) {
							x += i;
							y += j;
							if (copyBoard.getPiece(new Pair<Integer, Integer>(x, y)) >= 0) {
								continue;
							}
						}
						if (copyBoard.isOnBoard(x, y) && roll > 1) {
							copyBoard.move(position.getKey(), new Pair<Integer, Integer>(x, y));
							if (origin == null) {
								for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move : 
										copyBoard.getLegalMoves(roll - 1, team, position.getKey())) {
									if (!legalMoves.contains(move)) {
										legalMoves.add(move);
									}
								}
							} else {
								for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move : 
										copyBoard.getLegalMoves(roll - 1, team, position.getKey())) {
									if (!legalMoves.contains(move)) {
										legalMoves.add(move);
									}
								}
							}
							copyBoard.move(new Pair<Integer, Integer>(x, y), position.getKey());
						} else if (copyBoard.isOnBoard(x, y)) {
							if (origin == null) {
								legalMoves.add(
										new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(
												position.getKey(), new Pair<Integer, Integer>(x, y)));
							} else {
								legalMoves.add(
										new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(
												origin, new Pair<Integer, Integer>(x, y)));
							}
						}
					}
				}
			}
		}
		return legalMoves;
	}
}
