package tarble2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class History {

	// Writes a collection of gamestates representing the 
	// history of a game to a file for future use.
	static void writeHistory(Collection<Gamestate> states, String filename) {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
		              new FileOutputStream(filename), "utf-8"))) {
			for (Gamestate state : states) {
				writer.write(state.toString() + "\n");
			}
		} catch (Exception e) {
			throw new RuntimeException("Could not write history.");
		}
	}
	
	static List<Gamestate> readHistory(String filename) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(Constants.GAME_DIR), "utf-8"))) {
			String line;
			List<Gamestate> states = new ArrayList<Gamestate>();
		    while ((line = reader.readLine()) != null) {
		    	states.add(Gamestate.deserialize(line));
		    }
		    return states;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}