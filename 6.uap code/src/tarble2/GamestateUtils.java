package tarble2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

class GamestateUtils {
	
	static Collection<Gamestate> generateNextMove(Gamestate state) {
		Set<Integer> player;
		Set<Integer> opp;
		int score;
		int oppScore;
		boolean player1 = state.isPlayer1Turn();
		int roll = state.getRoll();
		boolean neutral = state.isNeutralPlaced();
		Collection<Gamestate> nextMoves = new ArrayList<Gamestate>();
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
			for (int move : Constants.MOVEMENTS) {
				int next = piece + move;
				if (!isOnBoard(next) || player.contains(next)) {
					// Can't move off board or jump own piece.
					continue;
				} else if (opp.contains(next)) {
					// Try to jump opponent.
					next += move;
					if (!player.contains(next) 
							&& !opp.contains(next) 
							&& !(neutral && next == Constants.NEUTRAL_LOCATION) 
							&& isOnBoard(next)) {
						// Can Jump.
						Set<Integer> newOpp = new HashSet<Integer>(opp);
						newOpp.remove(piece + move);
						Set<Integer> newPlayer = new HashSet<Integer>(player);
						newPlayer.remove(piece);
						newPlayer.add(next);
						nextMoves.add(Gamestate.of(
								newPlayer, newOpp, neutral, score + 1, oppScore, player1, roll - 1));
					}
				} else if (neutral && next == Constants.NEUTRAL_LOCATION) {
					// Try to jump neutral.
					next += move;
					if (!player.contains(next) && !opp.contains(next)) {
						// Can Jump.
						Set<Integer> newPlayer = new HashSet<Integer>(player);
						newPlayer.remove(piece);
						newPlayer.add(next);
						nextMoves.add(Gamestate.of(
								newPlayer, opp, false, score + 1, oppScore, player1, roll - 1));
					}
				} else {
					Set<Integer> newPlayer = new HashSet<Integer>(player);
					newPlayer.remove(piece);
					newPlayer.add(next);
					nextMoves.add(Gamestate.of(
							newPlayer, opp, neutral, score, oppScore, player1, roll - 1));
				}
			}
		}
		return nextMoves;
	}
	
	static boolean isLegalMove(Gamestate state1, Gamestate state2) {
		Collection<Gamestate> newStates = generateNextMove(state1);
		if (state1.getRoll() > 1) {
			return newStates.contains(state2);
		}
		if (state1.isPlayer1Turn() == state2.isPlayer1Turn() || !state2.isNeutralPlaced()) {
			return false;
		}
		for (Gamestate possible : newStates) {
			if (possible.getTeam1().equals(state2.getTeam1()) 
					&& possible.getTeam2().equals(state2.getTeam2())
					&& possible.getScore1() == state2.getScore1()
					&& possible.getScore2() == state2.getScore2()) {
				return true;
			}
		}
		return false;
	}
	
	static Gamestate move(Gamestate state, int start, int end) {
		if (state.isPlayer1Turn()) {
			Set<Integer> newPlayer = new HashSet<Integer>(state.getTeam1());
			newPlayer.remove(start);
			newPlayer.add(end);
			// Basic Move.
			if (Constants.MOVEMENTS.contains(end - start)) {
				return Gamestate.of(
						newPlayer, 
						state.getTeam2(), 
						state.isNeutralPlaced(), 
						state.getScore1(),
						state.getScore2(), 
						true,
						state.getRoll() - 1);
			}
			// Taking Move.
			int singleMovement = (end - start) / 2;
			if (start + singleMovement == Constants.NEUTRAL_LOCATION) {
				// Took Neutral
				return Gamestate.of(
						newPlayer, 
						state.getTeam2(), 
						false, 
						state.getScore1() + 1,
						state.getScore2(), 
						true,
						state.getRoll() - 1);
			}
			Set<Integer> newOpp = new HashSet<Integer>(state.getTeam2());
			newOpp.remove(start + singleMovement);
			return Gamestate.of(
					newPlayer, 
					newOpp, 
					state.isNeutralPlaced(), 
					state.getScore1() + 1,
					state.getScore2(), 
					true,
					state.getRoll() - 1);
		} else {
			Set<Integer> newPlayer = new HashSet<Integer>(state.getTeam2());
			newPlayer.remove(start);
			newPlayer.add(end);
			// Basic Move.
			if (Constants.MOVEMENTS.contains(end - start)) {
				return Gamestate.of(
						newPlayer, 
						state.getTeam1(), 
						state.isNeutralPlaced(), 
						state.getScore2(),
						state.getScore1(), 
						false,
						state.getRoll() - 1);
			}
			// Taking Move.
			int singleMovement = (end - start) / 2;
			if (start + singleMovement == Constants.NEUTRAL_LOCATION) {
				// Took Neutral
				return Gamestate.of(
						newPlayer, 
						state.getTeam1(), 
						false, 
						state.getScore2() + 1,
						state.getScore1(), 
						false,
						state.getRoll() - 1);
			}
			Set<Integer> newOpp = new HashSet<Integer>(state.getTeam1());
			newOpp.remove(start + singleMovement);
			return Gamestate.of(
					newPlayer, 
					newOpp, 
					state.isNeutralPlaced(), 
					state.getScore2() + 1,
					state.getScore1(), 
					false,
					state.getRoll() - 1);
		}
	}
	
	static boolean isOnBoard(int move) {
		return Math.abs((move / 100) - 5) <= 5 
				&& Math.abs((move % 100) - 5) <= 5 
				&& Math.abs((move / 100) + (move % 100) - 10) <= 5;
	}
	
	// Returns team number of the winning team or 0 if no team has won yet or -1 if the game has ended in a tie.
	static int getWinner(Gamestate state) {
		if (state.getTeam1().size() == 0) {
			if (state.getScore1() > state.getScore2()) {
				return 1;
			} 
			if (state.getTeam2().size() == 0) {
				return -1;
			}
			return 2;
		}
		if (state.getTeam2().size() == 0) {
			if (state.getScore2() > state.getScore1()) {
				return 2;
			}
			return 1;
		}
		return 0;
	}
	
	static Gamestate getStartingGamestate() {
		return new Gamestate(
				ImmutableSet.of(600, 700, 800, 900, 601, 701, 801),
				ImmutableSet.of(110, 210, 310, 410, 209, 309, 409),
				true,
				0,
				0,
				false,
				new Random().nextInt(Constants.MAX_ROLL) + 1);
	}
}
