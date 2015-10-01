package tarble2;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableList;

class GamestateUtils {
	private static final Collection<Integer> movements = 
			ImmutableList.of(1, -1, 100, -100, 99, -99);
	
	static Collection<Gamestate> generateNextMove(Gamestate state, boolean player1) {
		Set<Integer> player;
		Set<Integer> opp;
		int score;
		int oppScore;
		if (player1) {
			player = state.getTeam1();
			opp = state.getTeam2();
			score = state.getScore1();
			oppScore = state.getScore2();
		} else {
			player = state.getTeam2();
			opp = state.getTeam1();
			score = state.getScore2();
			oppScore = state.getScore1();
		}
		
		for (int piece : player) {
			for (int move : movements) {
				
			}
		}
	}
}
