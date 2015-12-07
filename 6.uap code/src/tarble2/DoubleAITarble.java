package tarble2;

import java.util.ArrayList;
import java.util.List;

import org.jblas.DoubleMatrix;

import neural.ForwardNet;
import tarble.Pair;

public class DoubleAITarble {
	public static void main(String[] args) {
		final int numGames = 300000;
		final int hiddenNodes = 50;
		final double alpha = .001;
		final double lambda = .9;
		int games = 0;
		// Start with random weights and not looking more than 1 gamestate ahead to explore space.
		//DoubleMatrix[] initialWeights = {DoubleMatrix.rand(Constants.NEURAL_NET_INPUTS + 1, hiddenNodes).mul(2).sub(1), DoubleMatrix.rand(hiddenNodes + 1, 1).mul(2).sub(1)};
		//NeuralTarblePlayer player = new NeuralTarblePlayer(new ForwardNet(initialWeights));
		
		// Once space is explored look a few moves ahead.
		NeuralTarblePlayer player = new NeuralTarblePlayer(ForwardNet.deserializeWeights("tarble_180000_1_layer_50_hiddenNodes_alpha=0.001_lambda=0.9"));
	
		long startTime = System.currentTimeMillis();
		while (true) {
			System.out.println("time: " + (System.currentTimeMillis() - startTime));
			Gamestate state = GamestateUtils.getStartingGamestate();
			List<DoubleMatrix> history = new ArrayList<>();
			int winner = 0;
			while (winner == 0) {
				Pair<Gamestate, DoubleMatrix> nextMove = player.trainingNextMove(state);
				state = nextMove.getFirst();
				history.add(nextMove.getSecond());
				winner = GamestateUtils.getWinner(state);
			}
			if (winner == -1) {
				player.updateNet(history, 0.5 , .001, .9);
			} else {
				player.updateNet(history, winner * -2 + 3 , .001, .9);
			}
			if (games % 1 == 0) {
				//System.out.println(history);
				System.out.println(games);
			}
			if (++games % 1 == 0) {
				player.net.serializeWeights("tarble_1_layer_" + hiddenNodes + "_hiddenNodes_alpha=" + alpha + "_lambda=" + lambda + "_full_move");
				System.out.println("written");
			}
		}
	}
}
