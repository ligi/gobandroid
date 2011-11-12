package org.ligi.gobandroid_hd.ui.review;

import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.NavigationAndCommentFragment;
import android.support.v4.app.Fragment;

public class GameReviewActivity extends GoActivity  {

	public Fragment getGameExtraFragment() {
		return new NavigationAndCommentFragment();
	}
	
}
