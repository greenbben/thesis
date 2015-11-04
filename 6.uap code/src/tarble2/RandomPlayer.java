package tarble2;

import java.util.List;
import java.util.Random;

class RandomPlayer implements Player {
	Random rand = new Random();

	/**
	 * Chooses a random move.
	 */
	@Override
	public Gamestate nextMove(Gamestate state) {
		List<Gamestate> states = (List<Gamestate>) GamestateUtils.generateNextMove(state);
		return states.get(rand.nextInt(states.size()));
	}

}
