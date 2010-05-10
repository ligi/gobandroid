package org.ligi.gobandroid.logic;

public class GoGameProvider {

	private static GoGame game;
	
	public static void setGame(GoGame p_game) {
		game=p_game;
	}
	
	public static GoGame getGame() {
		return game;
	}
	
}
