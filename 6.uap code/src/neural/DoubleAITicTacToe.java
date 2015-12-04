package neural;

import java.util.ArrayList;
import java.util.Collection;

import org.jblas.DoubleMatrix;

public class DoubleAITicTacToe {

	public static void main(String[] args) {
		final int hiddenNodes = 20;
		DoubleMatrix[] initialWeights = {DoubleMatrix.rand(11, hiddenNodes).mul(2).sub(1), DoubleMatrix.rand(hiddenNodes + 1, 1)};
		
		TicTacToe state = new TicTacToe(new int[3][3], true);
		TicTacToePlayer player = new NeuralTicTacToePlayer(new ForwardNet(initialWeights));
		
		Collection<DoubleMatrix> history = new ArrayList<>();
		while (state.winner() == 0) {
			state = player.nextMove(state);
			history.add(TicTacToe.boardToInputs(state.state, state.getPlayerMultiplier()));
		}
		System.out.println(history);
	}

}
