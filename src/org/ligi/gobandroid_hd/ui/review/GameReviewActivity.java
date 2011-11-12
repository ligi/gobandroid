package org.ligi.gobandroid_hd.ui.review;

import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.NavigationAndCommentFragment;
import android.support.v4.app.Fragment;

public class GameReviewActivity extends GoActivity  {

	public Fragment getGameExtraFragment() {
		return new NavigationAndCommentFragment();
	}
	
	
	@Override
	public byte doMoveWithUIFeedback(byte x,byte y) {
		// we want the user not to be able to edit in review mode
		return GoGame.MOVE_VALID;
	}	 
}
