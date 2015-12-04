package neural;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jblas.DoubleMatrix;

public class TicTacToe {
	// 2D array representing board state. 0 is empty, 1 is X 2 is O.
	int[][] state = new int[3][3];
	boolean player1Turn;
	
	public TicTacToe(int[][] state, boolean player1Turn) {
		this.state = state;
		this.player1Turn = player1Turn;
	}
	
	
	// Adds a piece of type team to row, col. Throws on illegal move.
	public int[][] move(int team, int row, int col) {
		if (state[row][col] == 0) {
			state[row][col] = team;
			player1Turn = !player1Turn;
		} else {
			throw new RuntimeException("Illegal Move.");
		}
		return state;
	}
	
	// Returns 0 if no winner yet, otherwise the number of the winning team. -1 if the game is a tie.
	public int winner() {
		// 3 in a row in a row.
		for (int i = 0; i < state.length; ++i) {
			if (state[i][0] == state[i][1] && state[i][1] == state[i][2]) {
				return state[i][0];
			}
		}
		
		// 3 in a row in a column.
		for (int i = 0; i < state[0].length; ++i) {
			if (state[0][i] == state[1][i] && state[1][i] == state[2][i]) {
				return state[0][i];
			}
		}
		
		// 3 in a row diagonally.
		if (state[0][0] == state[1][1] && state[1][1] == state[2][2]) {
			return state[0][0];
		} else if (state[0][2] == state[1][1] && state[1][1] == state[2][0]) {
			return state[0][2];
		}
		
		// Tie.
		if (this.getLegalMoves().size() == 0) {
			return -1;
		}
		
		return 0;
	}
	
	// Returns the next possible states.
	public Collection<int[][]> getLegalMoves() {
		Set<int[][]> moves = new HashSet<int[][]>();
		for (int i = 0; i < state.length; ++i) {
			for (int j = 0; j < state[0].length; ++j) {
				if (state[i][j] == 0) {
					int[][] newState = new int[3][3];
					for (int k = 0; k < state.length; ++k) {
						for (int l = 0; l < state[0].length; ++l) {
							newState[k][l] = state[k][l];
						}
					}
					if (player1Turn) {
						newState[i][j] = 1;
					} else {
						newState[i][j] = -1;
					}
					moves.add(newState);
				}
			}
		}
		return moves;	
	}
	
	public int getPlayerMultiplier() {
		return player1Turn ? 1 : -1;
	}
	
	static DoubleMatrix boardToInputs(int[][] state, int playerMultiplier) {
		return new DoubleMatrix(1, 10, 
				state[0][0], 
				state[1][0], 
				state[2][0],
				state[0][1],
				state[1][1],
				state[2][1],
				state[0][2],
				state[1][2],
				state[2][2],
				playerMultiplier);
	}
}
