package tarble2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class GamestateUtilsTest {
	
	private void assertStates(
			Collection<Gamestate> states,
			Collection<Set<Integer>> player1, 
			Collection<Set<Integer>> player2, 
			boolean player, 
			int neutrals) {
		// Correct number of moves found.
		assertEquals(player1.size(), states.size());
		assertEquals(player2.size(), states.size());
		
		// Correct number of boards with the neutral piece left found.
		int foundNeutrals = 0;
		for (Gamestate state : states) {
			if (state.isNeutralPlaced()) {
				foundNeutrals++;
			}
		}
		assertEquals(foundNeutrals, neutrals);
		
		// Correct player1 locations found.
		for (Set<Integer> p1 : player1) {
			boolean found = false;
			for (Gamestate state : states) {
				if (state.getTeam1().equals(p1)) {
					found = true;
					break;
				}
			}
			assertTrue(found);
		}
		
		// Correct player2 locations found.
		for (Set<Integer> p2 : player2) {
			boolean found = false;
			for (Gamestate state : states) {
				if (state.getTeam2().equals(p2)) {
					found = true;
					break;
				}
			}
			assertTrue(found);
		}
	}

	@Test
	public void testGenerateNextMoveBasicPlayer1Turn() {
		// (-3,3) and (0,3)
		Set<Integer> p1 = ImmutableSet.of(208, 508);
		// (0,-4) and (4,-4).
		Set<Integer> p2 = ImmutableSet.of(501, 901);
		Gamestate state = new Gamestate(p1, p2, true, 0, 0, true, 5);
		
		Collection<Gamestate> states = GamestateUtils.generateNextMove(state);
		
		assertEquals(12, states.size());
	}
}
