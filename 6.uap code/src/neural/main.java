package neural;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

public class main {
	
	public static void main(String[] args) {
		DoubleMatrix[] weights = {new DoubleMatrix(3,2,-15,5,-10,10,-10,10), new DoubleMatrix(new double[][] {{15}, {10}, {10}})};
		for (DoubleMatrix i : weights) {
			System.out.println(i.rows + "|" + i.columns + "|" + i);
		}
		
		ForwardNet net = ForwardNet.deserializeWeights("testWeights");
		for (DoubleMatrix i : net.weights) {
			System.out.println(i.rows + "|" + i.columns + "|" + i);
			i = new DoubleMatrix(new double[][] {{-1,3},{4,5}});
		}
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
	}
}
