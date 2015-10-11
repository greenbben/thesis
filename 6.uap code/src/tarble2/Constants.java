package tarble2;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

class Constants {
	static final int NEUTRAL_LOCATION = 505;
	
	static final Collection<Integer> MOVEMENTS = 
			ImmutableList.of(1, -1, 100, -100, 99, -99);
	
	static final Collection<Integer> GREY_LOCATIONS =
			ImmutableList.of(500, 1000, 5, 10, 510, 1005, 505);

	static final int MAX_ROLL = 6;
	
	static final int ROWS = 11;
	
	static final int COLS = 11;
	
	static final String GAME_DIR = "/home/greenbben/Documents/thesis/6.Uap extra/games/game.txt";
}
