package tarble2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jblas.DoubleMatrix;

import neural.ForwardNet;
import tarble.Pair;

public class NeuralTarblePlayer implements Player {
	ForwardNet net;
	
	public NeuralTarblePlayer(ForwardNet net) {
		this.net = net;
	}
	
	public ForwardNet updateNet(List<DoubleMatrix> history, double winner, double learningRate, double lambda) {
		net.TDSingleUpdate(history, winner, learningRate, lambda);
		return net;
	}

	@Override
	public Gamestate nextMove(Gamestate state) {
		int playerMultiplier = state.getPlayerMultiplier();
		Gamestate choice = null;
		double bestScore = Double.NEGATIVE_INFINITY;
		// Store all possible moves in new next moves.
		Set<Gamestate> nextMoves = new HashSet<>();
		nextMoves.add(state);
		for (int i = 0; i < state.getRoll(); i++) {
			Set<Gamestate> newNextMoves = new HashSet<>();
			for (Gamestate nextState : nextMoves) {
				int winner = GamestateUtils.getWinner(nextState);
				if (winner != 0) {
					if ((winner == 1 && playerMultiplier == 1) || (winner == 2 && playerMultiplier == -1)) {
						return nextState;
					}
				}
				for (Gamestate newMove : GamestateUtils.generateNextMove(nextState)) {
					newNextMoves.add(newMove);
				}
			}
			nextMoves = newNextMoves;
		}
		
		for (Gamestate next : nextMoves) {
			DoubleMatrix[] outputs = net.forwardProp(GamestateUtils.boardToInputs(next));
			double score = outputs[outputs.length - 1].sum() * playerMultiplier;
			//System.out.println(score + " " + TicTacToe.boardToInputs(next, playerMultiplier));
			if (bestScore < score) {
				bestScore = score;
				choice = next;
			}
		}
		return choice;
	}
		
	public Pair<Gamestate, DoubleMatrix> trainingNextMove(Gamestate state) {
		int playerMultiplier = state.getPlayerMultiplier();
		Gamestate choice = null;
		DoubleMatrix inputChoice = null;
		double bestScore = Double.NEGATIVE_INFINITY;
		
		// Store all possible moves in new next moves.
		Set<Gamestate> nextMoves = new HashSet<>();
		nextMoves.add(state);
		for (int i = 0; i < state.getRoll(); i++) {
			Set<Gamestate> newNextMoves = new HashSet<>();
			for (Gamestate nextState : nextMoves) {
				int winner = GamestateUtils.getWinner(nextState);
				if (winner != 0) {
					if ((winner == 1 && playerMultiplier == 1) || (winner == 2 && playerMultiplier == -1)) {
						return new Pair<Gamestate, DoubleMatrix>(nextState, GamestateUtils.boardToInputs(nextState));
					}
				}
				for (Gamestate newMove : GamestateUtils.generateNextMove(nextState)) {
					newNextMoves.add(newMove);
				}
			}
			nextMoves = newNextMoves;
		}
		for (Gamestate next : GamestateUtils.generateNextMove(state)) {
			int winner = GamestateUtils.getWinner(next);
			DoubleMatrix inputNext = GamestateUtils.boardToInputs(next);
			if (winner != 0) {
				if ((winner == 1 && playerMultiplier == 1) || (winner == 2 && playerMultiplier == -1)) {
					return new Pair<Gamestate, DoubleMatrix>(next, inputNext);
				}
			}
			DoubleMatrix[] outputs = net.forwardProp(inputNext);
			double score = outputs[outputs.length - 1].sum() * playerMultiplier;
			if (bestScore < score) {
				bestScore = score;
				choice = next;
				inputChoice = inputNext;
			}
		}
		return new Pair<Gamestate, DoubleMatrix>(choice, inputChoice);
	}
}
