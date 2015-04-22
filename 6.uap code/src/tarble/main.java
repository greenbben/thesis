package tarble;

import java.util.HashMap;
import java.util.Map;

public class main {

	public static void main(String[] args) {
		Map<Pair<Integer, Integer>, Integer> pieces = new HashMap<Pair<Integer, Integer>, Integer>();
		pieces.put(new Pair<Integer, Integer>(-4, 5), 1);
		pieces.put(new Pair<Integer, Integer>(0, 2), 1);
		pieces.put(new Pair<Integer, Integer>(4, -4), 1);
		pieces.put(new Pair<Integer, Integer>(4, -3), 2);
		pieces.put(new Pair<Integer, Integer>(4, -5), 2);
		pieces.put(new Pair<Integer, Integer>(0, -2), 2);
		pieces.put(new Pair<Integer, Integer>(0, 0), 0);
		Board board = new Board(pieces);
		Gamestate gamestate = new Gamestate(board, 2);
		System.out.println(board.getLegalMoves(2, 2));
		System.out.println(board.getLegalMoves(2, 2).size());
		/*board.printBoard();
		gamestate.readNotation("4 -4,5:1,3 0,2:0,-1x0,0");
		board.printBoard();
		System.out.println(gamestate.getScore(1));
		System.out.println(gamestate.getScore(2));*/
	}

}
