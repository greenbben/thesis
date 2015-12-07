package tarble2;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * A class to represent the state of the game at any point in time. Team 1 is a set of integers
 * representing the locations of all of the team 1 pieces, and team 2 is a set of integers representing
 * all of the team 2 pieces. The integers are determined by multiplying the column number of a location
 * by 100 and adding the row number of a location with all locations taking a value between 0 and 10. Scores
 * 1 and 2 represent the scores of the two respective teams. The boolean neutral variable represents whether
 * or not the neutral center piece is still located in the center of the board.
 */
public final class Gamestate {
	final Set<Integer> team1;
	final Set<Integer> team2;
	final boolean neutral;
	final int score1;
	final int score2;
	final int roll;
	final boolean player1Turn;
	
	private final static Random rand = new Random();
	
	Gamestate(Set<Integer> team1, Set<Integer> team2, 
			boolean neutral, int score1, int score2, boolean player1Turn, int roll) {
		this.team1 = team1;
		this.team2 = team2;
		this.neutral = neutral;
		this.score1 = score1;
		this.score2 = score2;
		this.player1Turn = player1Turn;
		this.roll = roll;
	}
	
	static Gamestate of(
			Set<Integer> player, 
			Set<Integer> opp, 
			boolean neutral, 
			int score, 
			int oppScore, 
			boolean player1Turn,
			int roll) {
		if (player1Turn) {
			if (roll == 0) {
				player.removeAll(Constants.GREY_LOCATIONS);
				return new Gamestate(
						player, opp, true, score, oppScore, false, rand.nextInt(Constants.MAX_ROLL) + 1);
			}
			return new Gamestate(player, opp, neutral, score, oppScore, true, roll);
		}
		if (roll == 0) {
			player.removeAll(Constants.GREY_LOCATIONS);
			return new Gamestate(
					opp, player, true, oppScore, score, true, rand.nextInt(Constants.MAX_ROLL) + 1);
		}
		return new Gamestate(opp, player, neutral, oppScore, score, false, roll);
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
	
	int getRoll() {
		return roll;
	}
	
	boolean isPlayer1Turn() {
		return player1Turn;
	}
	
	String hash() {
		return "team1:" + team1.toString() + score1 + ",team2:" + team2.toString() + score2 + ",neutral:" + neutral + "," + roll + "," + player1Turn;
	}
	
	static Gamestate deserialize(String given) {
		Set<Integer> team1 = new HashSet<Integer>();
		Set<Integer> team2 = new HashSet<Integer>();
		int score1;
		int score2;
		boolean neutral;
		int roll;
		boolean player1Turn;
		
		for (String num : given.substring(given.indexOf('[') + 1, given.indexOf(']')).split(",")) {
			if (num.length() > 0) {
				team1.add(Integer.parseInt(num.trim()));
			}
		}
		given = given.substring(given.indexOf(']') + 1);
		score1 = Integer.parseInt(given.substring(0, given.indexOf(",")));
		
		for (String num : given.substring(given.indexOf('[') + 1, given.indexOf(']')).split(",")) {
			if (num.length() > 0) {
				team2.add(Integer.parseInt(num.trim()));
			}
		}
		given = given.substring(given.indexOf(']') + 1);
		String[] givenArray = given.split(",");
		score2 = Integer.parseInt(givenArray[0]);
		neutral = Boolean.parseBoolean(givenArray[1].substring(8));
		roll = Integer.parseInt(givenArray[2]);
		player1Turn = Boolean.parseBoolean(givenArray[3]);
		
		return new Gamestate(team1, team2, neutral, score1, score2, player1Turn, roll);
	}
	
	@Override 
	public String toString() {
		return hash();
	}
	
	@Override
	public int hashCode() {
		return this.hash().hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Gamestate) {
			return ((Gamestate) other).hash().equals(hash());
		}
		return false;
	}
	
	int getPlayerMultiplier() {
		return player1Turn ? 1 : -1;
	}
}
