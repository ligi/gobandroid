package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_hd.logic.GoGame;

public class GoProblemActivity extends GoActivity {
	public byte doMoveWithUIFeedback(byte x,byte y) {
		byte res=super.doMoveWithUIFeedback(x,y);
		if (res==GoGame.MOVE_VALID)
			if (game.getActMove().hasNextMove())
				game.jump(game.getActMove().getnextMove(0));
			else
				game.getActMove().setComment(game.getActMove().getComment()+"\nOff Path");
		game.notifyGameChange();
		return res;
	}
}
