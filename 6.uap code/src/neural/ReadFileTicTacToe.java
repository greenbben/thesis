package neural;

import java.util.ArrayList;
import java.util.Collection;

import org.jblas.DoubleMatrix;

public class ReadFileTicTacToe {
	public static void main(String[] args) {
		NeuralTicTacToePlayer player = new NeuralTicTacToePlayer(ForwardNet.deserializeWeights("tictactoe300000weightsVar.05SecondTry"));
		TicTacToe state = new TicTacToe(new int[3][3], true);
		Collection<DoubleMatrix> history = new ArrayList<>();
		while (state.winner() == -10) {
			state = player.nextMove(state);
			history.add(TicTacToe.boardToInputs(state.state, state.getPlayerMultiplier()));
		}
		System.out.println(history);
		System.out.println(state.winner());
	}

}
