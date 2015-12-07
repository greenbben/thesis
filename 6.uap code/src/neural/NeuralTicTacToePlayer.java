package neural;

import java.util.List;
import java.util.Random;

import org.jblas.DoubleMatrix;

public class NeuralTicTacToePlayer implements TicTacToePlayer {
	ForwardNet net;
	Random r = new Random();
	
	public NeuralTicTacToePlayer(ForwardNet net) {
		if (net.weights[0].rows != 11) {
			throw new IllegalArgumentException("Net must have 11 inputs, 9 for pieces, 1 for player turn, and 1 for threshold.");
		}
		this.net = net;
	}

	public TicTacToe nextRandomMove(TicTacToe state) {
		int playerMultiplier = state.getPlayerMultiplier();
		int[][] choice = null;
		double bestScore = Double.NEGATIVE_INFINITY;
		for (int[][] next : state.getLegalMoves()) {
			DoubleMatrix[] outputs = net.forwardProp(TicTacToe.boardToInputs(next, -1 * playerMultiplier));
			double score = outputs[outputs.length - 1].sum() * playerMultiplier * ((r.nextGaussian() / 20.0) + 1);
			if (bestScore < score) {
				bestScore = score;
				choice = next;
			}
		}
		return new TicTacToe(choice, !state.player1Turn);
	}

	public ForwardNet updateNet(List<DoubleMatrix> history, double winner, double learningRate, double lambda) {
		net.TDSingleUpdate(history, winner, learningRate, lambda);
		return net;
	}

	@Override
	public TicTacToe nextMove(TicTacToe state) {
		int playerMultiplier = state.getPlayerMultiplier();
		int[][] choice = null;
		double bestScore = Double.NEGATIVE_INFINITY;
		for (int[][] next : state.getLegalMoves()) {
			if (TicTacToe.winner(next) == playerMultiplier) {
				choice = next;
				break;
			}
			DoubleMatrix[] outputs = net.forwardProp(TicTacToe.boardToInputs(next, -1 * playerMultiplier));
			double score = outputs[outputs.length - 1].sum() * playerMultiplier;
			//System.out.println(score + " " + TicTacToe.boardToInputs(next, playerMultiplier));
			if (bestScore < score) {
				bestScore = score;
				choice = next;
			}
		}
		return new TicTacToe(choice, !state.player1Turn);
	}
}
