package tarble;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Gamestate {
	Board board;
	// Stores the scores of the players in the game. Player x's score is indexed at location x - 1.
	int[] scores;
	
	public Gamestate() {
		this.board = setUpBoard();
		this.scores = new int[2];
	}

	public Gamestate(Board board, int players) {
		this.board = board;
		this.scores = new int[players];
	}
	
	public static Board setUpBoard() {
		Map<Pair<Integer, Integer>, Integer> pieces = new HashMap<Pair<Integer, Integer>, Integer>();
		pieces.put(new Pair<Integer, Integer>(-4, 5), 1);
		pieces.put(new Pair<Integer, Integer>(-3, 5), 1);
		pieces.put(new Pair<Integer, Integer>(-2, 5), 1);
		pieces.put(new Pair<Integer, Integer>(-1, 5), 1);
		pieces.put(new Pair<Integer, Integer>(-3, 4), 1);
		pieces.put(new Pair<Integer, Integer>(-2, 4), 1);
		pieces.put(new Pair<Integer, Integer>(-1, 4), 1);
		pieces.put(new Pair<Integer, Integer>(1, -5), 2);
		pieces.put(new Pair<Integer, Integer>(2, -5), 2);
		pieces.put(new Pair<Integer, Integer>(3, -5), 2);
		pieces.put(new Pair<Integer, Integer>(4, -5), 2);
		pieces.put(new Pair<Integer, Integer>(1, -4), 2);
		pieces.put(new Pair<Integer, Integer>(2, -4), 2);
		pieces.put(new Pair<Integer, Integer>(3, -4), 2);
		pieces.put(new Pair<Integer, Integer>(0, 0), 0);
		return new Board(pieces);
	}
	
	// Reads notation of a move in the game. Organized as roll followed by 
	// from:toxtaken for each piece moved and seperated by spaces
	public void readNotation(String input) {
		String[] inputArray = input.split(" ");
		String[] moves = Arrays.copyOfRange(inputArray, 1, inputArray.length);
		for (String move : moves) {
			String from = move.split(":")[0];
			String too = move.split(":")[1].split("x")[0];
			String taken = "";
			int player = board.getPiece(new Pair<Integer, Integer>(
					Integer.parseInt(from.split(",")[0]), 
					Integer.parseInt(from.split(",")[1])));
			if (move.split(":")[1].split("x").length > 1) {
				taken = move.split(":")[1].split("x")[1];
			}
			if (taken.length() > 0) {
				board.take(new Pair<Integer, Integer>(Integer.parseInt(taken.split(",")[0]), Integer.parseInt(taken.split(",")[1])));
				scores[player - 1]++;
			}
			board.move(
					new Pair<Integer, Integer>(
							Integer.parseInt(from.split(",")[0]), 
							Integer.parseInt(from.split(",")[1])), 
					new Pair<Integer, Integer>(
							Integer.parseInt(too.split(",")[0]), 
							Integer.parseInt(too.split(",")[1])));
		}
	}
	
	public int getScore(int player) {
		return scores[player - 1];
	}
	
	public Board getBoard() {
		return this.board;
	}
	
	public void move(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move) {
		this.board.move(move.getFirst(), move.getSecond());
	}
	
	public void take(Pair<Integer, Integer> taken, int teamTook) {
		this.board.take(taken);
		scores[teamTook - 1] += 1;
	}
}
