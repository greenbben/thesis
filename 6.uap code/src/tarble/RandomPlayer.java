package tarble;

import java.util.List;
import java.util.Random;

public class RandomPlayer extends Player {
	Random rand;
	
	public RandomPlayer(Gamestate gamestate, int team) {
		super(gamestate, team);
		this.rand = new Random();
	}

	public Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, List<Pair<Integer, Integer>>>
		chooseMove(int roll) {
		List<Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, List<Pair<Integer, Integer>>>> 
			legalMoves = this.board.getLegalMoves(roll, this.team);
		return legalMoves.get(rand.nextInt(legalMoves.size()));
	}

}
