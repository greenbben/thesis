package neural;

import org.jblas.DoubleMatrix;

public class NeuralTicTacToePlayer implements TicTacToePlayer {
	ForwardNet net;
	
	public NeuralTicTacToePlayer(ForwardNet net) {
		if (net.weights[0].rows != 10) {
			throw new IllegalArgumentException("Net must have 10 inputs, 9 for pieces and 1 for threshold.");
		}
		this.net = net;
	}

	@Override
	public int[][] nextMove(TicTacToe state) {
		int playerMultiplier = state.player1Turn ? 1 : -1;
		int[][] choice = null;
		double bestScore = Double.NEGATIVE_INFINITY;
		for (int[][] next : state.getLegalMoves()) {
			double score = net.forwardProp(boardToInputs(next, playerMultiplier))[0].sum();
			if (bestScore < score) {
				bestScore = score;
				choice = next;
			}
		}
		return choice;
	}
	
	private DoubleMatrix boardToInputs(int[][] state, int playerMultiplier) {
		return new DoubleMatrix(1, 9, 
				state[0][0] * playerMultiplier, 
				state[1][0] * playerMultiplier, 
				state[2][0] * playerMultiplier,
				state[0][1] * playerMultiplier,
				state[1][1] * playerMultiplier,
				state[2][1] * playerMultiplier,
				state[0][2] * playerMultiplier,
				state[1][2] * playerMultiplier,
				state[2][2] * playerMultiplier);
	}

}
