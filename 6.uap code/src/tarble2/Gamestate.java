package tarble2;

import java.util.Set;

/**
 * A class to represent the state of the game at any point in time. Team 1 is a set of integers
 * representing the locations of all of the team 1 pieces, and team 2 is a set of integers representing
 * all of the team 2 pieces. The integers are determined by multiplying the column number of a location
 * by 100 and adding the row number of a location with all locations taking a value between 0 and 10. Scores
 * 1 and 2 represent the scores of the two respective teams. The boolean neutral variable represents whether
 * or not the neutral center piece is still located in the center of the board.
 */
final class Gamestate {
	final Set<Integer> team1;
	final Set<Integer> team2;
	final boolean neutral;
	final int score1;
	final int score2;
	
	Gamestate(Set<Integer> team1, Set<Integer> team2, boolean neutral, int score1, int score2) {
		this.team1 = team1;
		this.team2 = team2;
		this.neutral = neutral;
		this.score1 = score1;
		this.score2 = score2;
	}
	
	Set<Integer> getTeam1() {
		return team1;
	}	
	
	Set<Integer> getTeam2() {
		return team2;
	}
	
	boolean isNeutralPlaced() {
		return neutral;
	}
	
	int getScore1() {
		return score1;
	}
	
	int getScore2() {
		return score2;
	}
	
	String hash() {
		return "team1:" + team1.toString() + score1 + ",team2:" + team2.toString() + score2 + ",neutral:" + neutral;
	}
}
