package tarble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public abstract class Player {
	Gamestate gamestate;
	Board board;
	int team;
	
	public Player(Gamestate gamestate, int team) {
		this.gamestate = gamestate;
		this.team = team;
		this.board = gamestate.getBoard();
	}
	
	public abstract Pair<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>, List<Pair<Integer, Integer>>>
		chooseMove(int roll);
}