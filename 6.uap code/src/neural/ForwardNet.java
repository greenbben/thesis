package neural;

import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

public class ForwardNet {
	DoubleMatrix[] weights;
	
	private final DoubleMatrix negOne = new DoubleMatrix(1,1,-1);
	
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
}
