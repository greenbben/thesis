package tarble2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import tarble.Pair;

class MonteCarloPlayer implements Player {
	static Random r = new Random();
	static double epsilon = 1e-6;
	
	Map<String, Pair<Integer, Double>> gamestateToNVisitsAndTotValue = 
			new HashMap<String, Pair<Integer, Double>>();
	
	Map<String, Collection<Gamestate>> gamestateToChildren = 
			new HashMap<String, Collection<Gamestate>>();

	@Override
	public Gamestate nextMove(Gamestate state) {
		System.out.println(state);
		int count = 0;
		long startTime =  System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < Constants.MOVE_MILLIS) {
			count++;
			Collection<Gamestate> visited = new ArrayList<Gamestate>();
			Gamestate cur = state;
			visited.add(state);
			while (!isLeaf(cur)) {
				Gamestate next = select(cur);
				cur = next;
				visited.add(cur);
			}
			while (GamestateUtils.getWinner(cur) == 0) {
				expand(cur);
				cur = select(cur);
				visited.add(cur);
			}
			double value = 0;
			int winner = GamestateUtils.getWinner(cur);
			if ((state.isPlayer1Turn() && winner == 1) || (!state.isPlayer1Turn() && winner == 2)) {
				value = 1000;
			} else if (winner == -1) {
				value = 500;
			} else {
				value = 0;
			}
			for (Gamestate visitedState : visited) {
				updateStats(visitedState, value);
			}
		}
		// Store all possible moves in new next moves.
		Set<Gamestate> nextMoves = new HashSet<>();
		nextMoves.add(state);
		for (int i = 0; i < state.getRoll(); i++) {
			Set<Gamestate> newNextMoves = new HashSet<>();
			for (Gamestate nextState : nextMoves) {
				for (Gamestate newMove : gamestateToChildren.get(nextState.hash())) {
					if (!isLeaf(newMove)) {
						newNextMoves.add(newMove);
					}
				}
			}
			nextMoves = newNextMoves;
		}
		
		Gamestate selected = null;
		double bestValue = Double.NEGATIVE_INFINITY;
		for (Gamestate c : nextMoves) {
            double uctValue = getUctValue(c, state.isPlayer1Turn());
            // small random number to break ties randomly in unexpanded nodes.
            if (uctValue > bestValue) {
            	selected = c;
            	bestValue = uctValue;
            }
		}
		System.out.println(count);
		System.out.println(selected);
		return selected;
	}
	
	private void expand(Gamestate state) {
		String hash = state.hash();
		gamestateToChildren.put(
				hash, GamestateUtils.generateNextMove(state));
		gamestateToNVisitsAndTotValue.put(hash, new Pair<Integer, Double>(0, 0.0));
	}
	
	private Gamestate select(Gamestate state) {
		Gamestate selected = null;
		double bestValue = Double.NEGATIVE_INFINITY;
		String stateHash = state.hash();
		for (Gamestate c : gamestateToChildren.get(stateHash)) {
			if (isLeaf(c)) {
				expand(c);
			}
			double uctValue = getUctValue(c, state.isPlayer1Turn());
            // small random number to break ties randomly in unexpanded nodes.
            if (uctValue > bestValue) {
            	selected = c;
            	bestValue = uctValue;
            }
		}
		Pair<Integer, Double> pair = gamestateToNVisitsAndTotValue.get(selected.hash());
		pair.setFirst(pair.getFirst() + 1);
		return selected;
	}
	
	private double getUctValue(Gamestate state, boolean player1) {
		Pair<Integer, Double> NVisitsAndTotValue = 
				gamestateToNVisitsAndTotValue.get(state.hash());
		double nVisits = NVisitsAndTotValue.getFirst();
		double totValue = NVisitsAndTotValue.getSecond();
		double naiveScore = (totValue / (nVisits + epsilon) 
				+ Math.sqrt(Math.log(nVisits+1) / (nVisits + epsilon)) +
                r.nextDouble() * epsilon);
		if (player1) {
			return (.5 + state.getScore1()) / (1 + state.getScore1() + state.getScore2()) * 1000 + naiveScore;
		}
		return (.5 + state.getScore2()) / (1 + state.getScore1() + state.getScore2()) * 1000 + naiveScore;
	}

	private boolean isLeaf(Gamestate state) {
		if (GamestateUtils.getWinner(state) == 0) {
			return !gamestateToChildren.containsKey(state.hash());
		}
		return true;
	}
	
	private void updateStats(Gamestate state, double value) {
		Pair<Integer, Double> pair = gamestateToNVisitsAndTotValue.get(state.hash());
		pair.setFirst(pair.getFirst() + 1);
		pair.setSecond(pair.getSecond() + value);
	}
	
	private int arity(Gamestate state) {
		return isLeaf(state) ? 0 : gamestateToChildren.get(state.hash()).size();
	}
}
