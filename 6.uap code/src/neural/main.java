package neural;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

public class main {
	
	public static void main(String[] args) {
//		DoubleMatrix[] weights = {new DoubleMatrix(3,2,-15,5,-10,10,-10,10), new DoubleMatrix(new double[][] {{15}, {10}, {10}})};
//		System.out.println(weights[1].mul(new DoubleMatrix(new double[][] {{10, 10, 10}})).rows);
//		for (DoubleMatrix i : weights) {
//			System.out.println(i.rows + "|" + i.columns + "|" + i);
//		}
//		
//		ForwardNet net = ForwardNet.deserializeWeights("testWeights");
//		for (DoubleMatrix i : net.weights) {
//			System.out.println(i.rows + "|" + i.columns + "|" + i);
//			i = new DoubleMatrix(new double[][] {{-1,3},{4,5}});
//		}
//		net.serializeWeights("testWeights");
//		ForwardNet net = new ForwardNet(weights);
//		System.out.println(net.forwardProp(new DoubleMatrix(1,2,1,0))[2]);
//		DoubleMatrix a = new DoubleMatrix(1,1,-1);
//		DoubleMatrix b = new DoubleMatrix(1,3,2,2,0);
//		System.out.println(MatrixFunctions.pow(b.neg(), -1));
//		System.out.println(b);
//		System.out.println(DoubleMatrix.concatHorizontally(a,b)
//				);
//		System.out.println(a);
//		System.out.println(b);
		
//		String a = "hello|you|are|fine";
//		String[] ar = a.split(Pattern.quote("|"));
//		for (String i : ar) {
//			System.out.println(i);
//		}
		
		
//		DoubleMatrix[] weights = {new DoubleMatrix(new double[][] {{3, -8}, {5, -5}, {5, -5}}), new DoubleMatrix(new double[][] {{15}, {10}, {10}})};
//		ForwardNet net = new ForwardNet(weights);
//		DoubleMatrix[] outs = net.forwardProp(new DoubleMatrix(new double [][] {{.5, 1.0}}));
//		System.out.println(outs[0]);
//		System.out.println(outs[1]);
//		System.out.println(outs[2]);
//		DoubleMatrix[] gradients = net.nonLossWeightGradients(outs);
//		System.out.println(gradients[0]);
//		System.out.println(gradients[1]);
		
		DoubleMatrix singleton = new DoubleMatrix(new double [][] {{3}});
		System.out.println(Double.NEGATIVE_INFINITY * -1);
	}
}
