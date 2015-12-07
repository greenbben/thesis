package neural;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import tarble2.Gamestate;

public class ForwardNet {
	DoubleMatrix[] weights;
	
	private final DoubleMatrix negOne = new DoubleMatrix(1,1,-1);
	private DoubleMatrix[] weightRowRemovers;
	
	/**
	 * Initial Weights is a float matrix representing the weights to
	 * be assigned to each neural connection. The length of the array
	 * is the number of neural layers in the net. Each layer has the 
	 * element i,j (ith row, jth column) which represents the ith 
	 * weight going into the jth nueron. Each layer must have the 
	 * same number of columns as the next layer has rows - 1 due to
	 * the constant input. The first layer has a number of rows equal 
	 * to the number of inputs + 1 for the same reason.
	 * 
	 * @param initialWeights
	 */
	public ForwardNet(DoubleMatrix[] initialWeights) {
		weights = initialWeights;
		weightRowRemovers = new DoubleMatrix[weights.length];
		for (int i = 0; i < weightRowRemovers.length; ++i) {
			weightRowRemovers[i] = DoubleMatrix.concatHorizontally(
					DoubleMatrix.zeros(weights[i].rows - 1), DoubleMatrix.eye(weights[i].rows - 1));
		}
	}
	
	/**
	 * A function to forward propagate through the neural network.
	 * 
	 * @param inputs A vector of the inputs to the neural network.
	 * Must be dimensions 1 row by n-1 columns where n = weights[0].rows
	 * @return An array of 1 row by n columns float matrices where
	 * ret[i - 1].columns = weights[i].columns. And the first element
	 * is equal to inputs.
	 */
	public DoubleMatrix[] forwardProp(DoubleMatrix inputs) {
		DoubleMatrix[] outputs = new DoubleMatrix [weights.length + 1];
		outputs[0] = inputs;
		int currentLevel = 1;
		for (DoubleMatrix layer : weights) {
			outputs[currentLevel] = sigmoid(DoubleMatrix.concatHorizontally(negOne, outputs[currentLevel - 1]).mmul(layer));
			currentLevel++;
		}
		return outputs;
	}
	
	/**
	 * A function to back propagate through the neural network.
	 * 
	 * @param inputs A matrix of inputs to the neural network. Must be
	 * dimensions k rows by n-1 columns where n = weights[0].rows and k
	 * is the number of inputs that you are given.
	 * @param expectedOutput A matrix of outputs from the neural network.
	 * Must be dimensions k rows by n columns where 
	 * n = weights[weights.length - 1].columns and k is the same as above.
	 * @return An array of weight matrices for the neural network after back propagation
	 * has completed.
	 */
	public DoubleMatrix[] backProp(DoubleMatrix inputs, DoubleMatrix expectedOutput, double rate, double threshold) {
		double change = Double.POSITIVE_INFINITY;
		double loss = Double.POSITIVE_INFINITY;
		while (threshold < change) {
			DoubleMatrix[] outputs = forwardProp(inputs);
			DoubleMatrix[] gradients = weightGradients(outputs, deltas(outputs[outputs.length - 1], expectedOutput));
			for (int i = 0; i < weights.length; ++i) {
				weights[i].sub(gradients[i].mul(rate));
			}
//			double newLoss = loss(inputs)
		}
		return weights;
	}
	
	/**
	 * Updates the weigths after he conclusion of a game. 
	 * 
	 * @param history An array of the input matrix to forward propagation
	 * at each point of the game. The first element is after the first move made by the first player, and the last element
	 * is the final state of the game in which one of the players has been declared the winner.
	 * @param winner The expected output by the final input in which one of the players has been declared the winner.
	 * @param learningRate A number between 0 and 1 representing how much the weights are updated.
	 * @param lambda A number between 0 and 1 representing how much weight early moves in the game are given towards a
	 * winning strategy. 0 means none and 1 means they are as important as the last move. Numbers in between 0 and 1 represent
	 * a decay of importance with time.
	 * @return The new updated weights array.
	 */
	public DoubleMatrix[] TDUpdate(Collection<DoubleMatrix> history, double winner, double learningRate, double lambda) {
		DoubleMatrix[] gradientSum = new DoubleMatrix[weights.length];
		for (int i = 0; i < gradientSum.length; ++i) {
			gradientSum[i] = DoubleMatrix.zeros(weights[i].rows, weights[i].columns);
		}
		for (DoubleMatrix inputs : history) {
			DoubleMatrix[] outputs = forwardProp(inputs);
			DoubleMatrix[] gradients = nonLossWeightGradients(outputs);
			for (int i = 0; i < weights.length; ++i) {
				gradientSum[i] = gradientSum[i].mul(lambda).add(gradients[i]);
				weights[i] = weights[i].add(gradientSum[i].mul(learningRate * (winner - outputs[outputs.length - 1].sum())));
			}
		}
		return weights;
	}
	
	public DoubleMatrix[] TDUpdateStep(List<DoubleMatrix> history, double winner, double learningRate, double lambda) {
		DoubleMatrix[][] gradientSum = new DoubleMatrix[history.size()][weights.length];
		DoubleMatrix[][] outputs = new DoubleMatrix[history.size()][weights.length + 1];
		int finalOutput = weights.length;
		int finalStep = history.size() - 1;
		// First update.
		outputs[0] = forwardProp(history.get(0));
		gradientSum[0] = nonLossWeightGradients(outputs[0]);
		
		// Middle updates.
		for (int i = 0; i < history.size() - 1; ++i) {
			outputs[i + 1] = forwardProp(history.get(i + 1));
			DoubleMatrix[] gradients = nonLossWeightGradients(outputs[i + 1]);
			for (int j = 0; j < weights.length; ++j) {
				gradientSum[i + 1][j] = gradientSum[i][j].mul(lambda).add(gradients[j]);
				weights[j] = weights[j].add(gradientSum[i][j].mul(learningRate * (outputs[i + 1][finalOutput].sum() - outputs[i][finalOutput].sum())));
			}
		}
		
		// Final update.
		for (int j = 0; j < weights.length; ++j) {
			weights[j] = weights[j].add(gradientSum[finalStep][j].mul(learningRate * (winner - outputs[finalStep][finalOutput].sum())));
		}
		
		return weights;
	}
	
	public DoubleMatrix[] TDSingleUpdate(List<DoubleMatrix> history, double winner, double learningRate, double lambda) {
		// Find the temporal difference sum of the gradients.
		DoubleMatrix[] gradientSum = new DoubleMatrix[weights.length];
		for (int i = 0; i < gradientSum.length; ++i) {
			gradientSum[i] = DoubleMatrix.zeros(weights[i].rows, weights[i].columns);
		}
		for (DoubleMatrix inputs : history) {
			DoubleMatrix[] outputs = forwardProp(inputs);
			DoubleMatrix[] gradients = nonLossWeightGradients(outputs);
			for (int i = 0; i < weights.length; ++i) {
				gradientSum[i] = gradientSum[i].mul(lambda).add(gradients[i].mul(learningRate * (winner - outputs[outputs.length - 1].sum())));
			}
		}
		
		// Update the weights.
		for (int i = 0; i < weights.length; ++i) {
			weights[i] = weights[i].add(gradientSum[i]);
		}
		return weights;
	}
	
	private DoubleMatrix sigmoid(DoubleMatrix z) {
		return MatrixFunctions.pow(MatrixFunctions.exp(z.neg()).add(1), -1);
	}
	
	/**
	 * Calculates the delta value for each layer in the neural network.
	 * 
	 * @param outputs A vector of outputs from the network. The returned value from forwardProp
	 * @param expectedOutput A 1 row by n columns float matrix where 
	 * expectedOutput.columns = weights[weights.length - 1].columns.
	 * @return An array of delta column vectors where the size of the array is the number of layers
	 * in the net and the size of the vector is equal to the number of neurons in the layer.
	 */
	private DoubleMatrix[] deltas(DoubleMatrix finalOutput, DoubleMatrix expectedOutput) {
		DoubleMatrix[] deltas = new DoubleMatrix[weights.length];
		deltas[deltas.length - 1] = finalOutput.sub(expectedOutput).transpose();
		for (int i = weights.length - 2; i >= 0; --i) {
			deltas[i] = weights[i + 1].mmul(deltas[i + 1]);
		}
		return deltas;
	}
	
	/**
	 * Calculates the gradient of the weights in the neural network.
	 * 
	 * @param outputs The result of calling forwardProp with the current weights.
	 * @param deltas The result of calling deltas with outputs.
	 * @return An array of DoubleMatrices representing the gradient of each of the weights.
	 */
	private DoubleMatrix[] weightGradients(DoubleMatrix[] outputs, DoubleMatrix[] deltas) {
		DoubleMatrix[] gradients = new DoubleMatrix[weights.length];
		for (int i = 0; i < gradients.length; ++i) {
			gradients[i] = deltas[i].mmul(DoubleMatrix.concatHorizontally(negOne, outputs[i]))
					.transpose();
		}
		return gradients;
	}
	
//	/**
//	 * Returns the non-Loss delta values for weight gradients.
//	 * 
//	 * @param outputs The result of calling forwardProp with the current weights.
//	 * @return An array of delta column vectors where the size of the array is the number of layers in
//	 * the net and the size of the vector is equal to the number of neurons in the layer.
//	 */
//	private DoubleMatrix[] nonLossDeltas(DoubleMatrix[] outputs) {
//		DoubleMatrix[] deltas = new DoubleMatrix[weights.length];
//		deltas[deltas.length - 1] = outputs[outputs.length - 1].mul(outputs[outputs.length - 1].sub(1).neg());
//		
//	}
	
	public DoubleMatrix[] nonLossWeightGradients(DoubleMatrix[] outputs) {
		DoubleMatrix[] gradients = new DoubleMatrix[weights.length];
		
		// First iteration is hard coded in because it is different than future iterations.
		DoubleMatrix delta = outputs[outputs.length - 1].mul(outputs[outputs.length - 1].sub(1).neg()).transpose();
		gradients[gradients.length - 1] = DoubleMatrix.concatHorizontally(negOne, outputs[gradients.length - 1]).transpose().mmul(delta.transpose());
		for (int i = gradients.length - 2; i >= 0; --i) {
			delta = weightRowRemovers[i + 1].mmul(weights[i + 1]).mmul(delta).mul(outputs[i + 1].mul(outputs[i + 1].sub(1).neg()));
			gradients[i] = DoubleMatrix.concatHorizontally(negOne, outputs[i]).transpose().mmul(delta.transpose());
		}
		return gradients;
	}
	
	/**
	 * Finds the log likelihood of a given classification.
	 * 
	 * @param finalOutput The result of running forward propagation with a given input.
	 * @param expectedOutput The expected result of running forward propagation with that input.
	 * @return A double representing the loss of the output.
	 */
	private double loss(DoubleMatrix finalOutput, DoubleMatrix expectedOutput) {
		return expectedOutput.neg().mmul(MatrixFunctions.log(finalOutput.transpose())).sum() 
				+ expectedOutput.sub(1).mmul(MatrixFunctions.log(finalOutput.transpose().sub(1).neg())).sum();
	}
	
	/**
	 * Finds the regularized log likelihood of a given classification.
	 * 
	 * @param finalOutput The result of running forward propagation with a given input.
	 * @param expectedOutput The expected result of running forward propagation with that input.
	 * @return A double representing the loss of the output with an added regularization term.
	 */
	private double regularlizedLoss(DoubleMatrix finalOutput, DoubleMatrix expectedOutput, double lambda) {
		double sum = 0;
		for (DoubleMatrix i : weights) {
			sum += MatrixFunctions.pow(2, i).sum();
		}
		return loss(finalOutput, expectedOutput) + lambda * sum;
	}
	
	public void serializeWeights(String fileName) {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
	              new FileOutputStream("/home/greenbben/Documents/thesis/6.Uap extra/weights/" + fileName + ".txt"), "utf-8"))) {
			for (DoubleMatrix weight : weights) {
				writer.write(weight.rows + "|" + weight.columns + "|" + weight + "\n");
			}
		} catch (Exception e) {
			throw new RuntimeException("Could not write history.");
		}
	}
	
	public static ForwardNet deserializeWeights(String fileName) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(
				"/home/greenbben/Documents/thesis/6.Uap extra/weights/" + fileName + ".txt"), "utf-8"))) {
			String line;
			Collection<DoubleMatrix> weights = new ArrayList<>();
		    while ((line = reader.readLine()) != null) {
		    	String[] firstSplit = line.split(Pattern.quote("|"));
		    	String[] rowSplit = firstSplit[2]
		    			.replaceAll(Pattern.quote("[") + "|" + Pattern.quote(" ") + "|" + Pattern.quote("]"), "")
		    			.split(";");
		    	double[][] weightArray = new double[rowSplit.length][rowSplit[0].split(",").length];
		    	for (int i = 0; i < rowSplit.length; ++i) {
		    		String[] row = rowSplit[i].split(",");
		    		for (int j = 0; j < row.length; ++j) {
		    			weightArray[i][j] = Double.parseDouble(row[j]);
		    		}
		    	}
		    	weights.add(new DoubleMatrix(weightArray));
		    }
		    return new ForwardNet(weights.toArray(new DoubleMatrix[weights.size()]));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
