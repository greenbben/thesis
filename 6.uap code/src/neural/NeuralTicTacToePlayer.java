package neural;

public class NeuralTicTacToePlayer implements TicTacToePlayer {
	ForwardNet net;
	
	public NeuralTicTacToePlayer(ForwardNet net) {
		if (net.weights[0].rows != 11) {
			throw new IllegalArgumentException("Net must have 11 inputs, 9 for pieces, 1 for player turn, and 1 for threshold.");
		}
		this.net = net;
	}

	@Override
	public TicTacToe nextMove(TicTacToe state) {
		int playerMultiplier = state.getPlayerMultiplier();
		int[][] choice = null;
		double bestScore = Double.NEGATIVE_INFINITY;
		for (int[][] next : state.getLegalMoves()) {
			double score = net.forwardProp(TicTacToe.boardToInputs(next, playerMultiplier))[0].sum() * playerMultiplier;
			if (bestScore < score) {
				bestScore = score;
				choice = next;
			}
		}
		return new TicTacToe(choice, !state.player1Turn);
	}

}
