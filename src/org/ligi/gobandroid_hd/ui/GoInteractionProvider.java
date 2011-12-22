package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_hd.logic.GoGameProvider;

public class GoInteractionProvider {
	
	public final static byte MODE_RECORD=0;
	public final static byte MODE_TSUMEGO=1;
	public final static byte MODE_REVIEW=2;
	public final static byte MODE_GNUGO=3;
		
	public static int touch_position=-1; // negative numbers -> no recent touch
	private static byte mode;
	
	public static void setTouchPosition(int pos) {
		touch_position=pos;
	}
	
	public static int getTouchPosition() {
		return touch_position;
	}

	public static int getTouchX() {
		return touch_position%GoGameProvider.getGame().getSize();
	}
	
	public static int getTouchY() {
		return touch_position/GoGameProvider.getGame().getSize();
	}
	
	public static boolean hasValidTouchCoord() {
		return ((touch_position>=0)&&(touch_position<GoGameProvider.getGame().getSize()*GoGameProvider.getGame().getSize()));
	}

	public static byte getMode() {
		return mode;
	}

	public static void setMode(byte mode) {
		GoInteractionProvider.mode = mode;
	}
}
