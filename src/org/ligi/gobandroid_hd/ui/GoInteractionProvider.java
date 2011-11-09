package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_hd.logic.GoGameProvider;

public class GoInteractionProvider {
	public static int touch_position=-1; // negative numbers -> no recent touch
	
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
}
