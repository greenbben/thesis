package tarble;

import java.util.Arrays;

public class Gamestate {
	Board board;
	// Stores the scores of the players in the game. Player x's score is indexed at location x - 1.
	int[] scores;

	public Gamestate(Board board, int players) {
		this.board = board;
		scores = new int[players];
	}
	
	// Reads notation of a move in the game. Organized as roll followed by 
	// from:toxtaken for each piece moved and seperated by spaces
	public void readNotation(String input) {
		String[] inputArray = input.split(" ");
		int roll = Integer.parseInt(inputArray[0]);
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
}
