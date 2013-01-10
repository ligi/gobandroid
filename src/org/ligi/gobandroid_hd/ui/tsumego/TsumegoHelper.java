package org.ligi.gobandroid_hd.ui.tsumego;

import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoMove;

public class TsumegoHelper {

	/**
	 * find how big the action is on the board - assuming it is top left corner
	 * 
	 * @param game
	 * @return
	 */
	public static int calcSpan(GoGame game,boolean with_moves) {
		int max = 0;
		for (int x = 0; x < game.getSize(); x++)
			for (int y = 0; y < game.getSize(); y++) {
				if ((x > max) && !game.getHandicapBoard().isCellFree(x, y))
					max = x;
				if ((y > max) && !game.getHandicapBoard().isCellFree(x, y))
					max = y;
			}
		
		if (with_moves) {
			max=calcMaxMove(game.getFirstMove(),max);
		}
		
		return max;
	}

	public static int calcMaxMove(GoMove move,int act_max) {
		if (move==null) 
			return act_max;
		
		for (GoMove nmove:move.getNextMoveVariations()) {
			int tmp_max=calcMaxMove(nmove,act_max);
			if (tmp_max>act_max)
				act_max=tmp_max;
		}
		
		if (move.getX()>act_max)
			act_max=move.getX();
		
		if (move.getY()>act_max)
			act_max=move.getY();
		
		return act_max;
	}
	
	
	/*
	 * 
	 * public static int calcSpan(GoGame game) { int min_x=game.getSize(); int
	 * min_y=game.getSize(); for (int x=0;x<game.getSize();x++) for (int
	 * y=0;y<game.getSize();y++) { if
	 * ((x<min_x)&&!game.getHandicapBoard().isCellFree(x, y)) min_x=x; if
	 * ((y<min_y)&&!game.getHandicapBoard().isCellFree(x, y)) min_y=y; }
	 * 
	 * return Math.max(game.getSize()-min_x, game.getSize()-min_y); }
	 */

	/**
	 * calculate a Zoom factor so that all stones in handicap fit on bottom
	 * right area
	 * 
	 * @return - the calculated Zoom factor
	 */
	public static float calcZoom(GoGame game,boolean with_moves) {

		int max_span_size = calcSpan(game,with_moves);

		if (max_span_size == 0) // no predefined stones -> no zoom
			return 1.0f;

		float calculated_zoom = (float) game.getSize() / (max_span_size + 2);

		if (calculated_zoom < 1.0f)
			return 1.0f;
		else
			return calculated_zoom;
	}
	

	public static int calcPOI(GoGame game,boolean with_moves) {
		int poi = (int) (game.getSize() / 2f / calcZoom(game,with_moves));
		return poi + poi * game.getSize();
	}

	public static int calcTransform(GoGame game) {
		// we count 4 quadrants to find the hot spot
		int[] count_h = new int[2];
		int[] count_v = new int[2];

		for (int x = 0; x < game.getSize(); x++)
			for (int y = 0; y < game.getSize(); y++) {
				if (!game.getVisualBoard().isCellFree(x, y)) {
					count_h[(x > (game.getSize() / 2)) ? 1 : 0]++;
					count_v[(y > (game.getSize() / 2)) ? 1 : 0]++;
				}
			}

		return ((count_v[0] > count_v[1]) ? 0 : 1) + ((count_h[0] > count_h[1]) ? 0 : 2);
	}
}
