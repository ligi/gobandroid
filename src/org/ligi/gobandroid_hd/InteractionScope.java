package org.ligi.gobandroid_hd;

import org.ligi.gobandroid_hd.logic.GoGame;


public class InteractionScope {
	/**
	 * set the game instance
	 * @param p_game the game to set as current
	 * 
	 */
	public void setGame(GoGame p_game) {
		ask_variant_session=true;
		game=p_game;
	}
	
	/**
	 * @return the game instance
	 */
	public GoGame getGame() {
		return game;
	}
	

	public final static byte MODE_RECORD=0;
	public final static byte MODE_TSUMEGO=1;
	public final static byte MODE_REVIEW=2;
	public final static byte MODE_GNUGO=3;
	public final static byte MODE_TELEVIZE=4;
		
	public int touch_position=-1; // negative numbers -> no recent touch
	private byte mode;
	private boolean is_noif_mode=false;
	
	
	public boolean ask_variant_session=true;
	
	private GoGame game; // we will most likely interact with a game ;-)
	
	public void setTouchPosition(int pos) {
		touch_position=pos;
	}
	
	public int getTouchPosition() {
		return touch_position;
	}

	public int getTouchX() {
		return touch_position%game.getSize();
	}
	
	public int getTouchY() {
		return touch_position/game.getSize();
	}
	
	public boolean hasValidTouchCoord() {
		return ((touch_position>=0)&&(touch_position<game.getSize()*game.getSize()));
	}

	public byte getMode() {
		return mode;
	}

	public void setMode(byte mode) {
		this.mode = mode;
	}

	public boolean is_in_noif_mode() {
		return is_noif_mode;
	}

	public void setIs_in_noif_mode(boolean is_noif_mode) {
		this.is_noif_mode = is_noif_mode;
	}
	
}
