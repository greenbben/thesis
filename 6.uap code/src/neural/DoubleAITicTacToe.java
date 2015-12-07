package neural;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jblas.DoubleMatrix;

public class DoubleAITicTacToe {

	public static void main(String[] args) {
		final int numGames = 300000;
		final int hiddenNodes = 40;
		int games = 0;
		DoubleMatrix[] initialWeights = {DoubleMatrix.rand(11, hiddenNodes).mul(2).sub(1), DoubleMatrix.rand(hiddenNodes + 1, 1).mul(2).sub(1)};
		
		NeuralTicTacToePlayer player = new NeuralTicTacToePlayer(new ForwardNet(initialWeights));
	
		while (games < numGames) {
			TicTacToe state = new TicTacToe(new int[3][3], true);
			List<DoubleMatrix> history = new ArrayList<>();
			while (state.winner() == -10) {
				state = player.nextRandomMove(state);
				history.add(TicTacToe.boardToInputs(state.state, state.getPlayerMultiplier()));
			}
			player.updateNet(history, (state.winner() + 1.0) / 2 , .001, .9);
			if (games % 10000 == 0) {
				System.out.println(history);
				System.out.println(state.winner());
			}
			if (++games == numGames) {
				player.net.serializeWeights("tictactoe" + games + "weightsVar.05SecondTry");
				System.out.println("written");
			}
		}
//		TicTacToe state = new TicTacToe(new int[3][3], true);
//		Collection<DoubleMatrix> history = new ArrayList<>();
//		while (state.winner() == -10) {
//			state = player.nextMove(state);
//			history.add(TicTacToe.boardToInputs(state.state, state.getPlayerMultiplier()));
//		}
//		System.out.println(history);
//		System.out.println(state.winner());
	}

}
