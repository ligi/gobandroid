package org.ligi.gobandroid_hd.ui.ingame_common;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.editing.EditGameActivity;
import org.ligi.gobandroid_hd.ui.game_setup.GoSetupActivity;
import org.ligi.gobandroid_hd.ui.gnugo.PlayAgainstGnugoActivity;
import org.ligi.gobandroid_hd.ui.recording.GameRecordActivity;
import org.ligi.gobandroid_hd.ui.review.GameReviewActivity;
import org.ligi.gobandroid_hd.ui.review.GoGamePlayerActivity;
import org.ligi.gobandroid_hd.ui.scoring.GameScoringActivity;
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoActivity;

import android.content.Context;
import android.content.Intent;

public class SwitchModeHelper {
	
	public static Intent getIntentByMode(Context ctx,int mode) {
		switch (mode) {

		case InteractionScope.MODE_EDIT:
			return  new Intent(ctx,EditGameActivity.class);

		case InteractionScope.MODE_RECORD:
			return  new Intent(ctx,GameRecordActivity.class);

		case InteractionScope.MODE_REVIEW:
			return new Intent(ctx,GameReviewActivity.class);
		
		case InteractionScope.MODE_TSUMEGO:
			return new Intent(ctx,TsumegoActivity.class);
			
		case InteractionScope.MODE_TELEVIZE:
			return new Intent(ctx,GoGamePlayerActivity.class);
			
		case InteractionScope.MODE_COUNT:
			return new Intent(ctx,GameScoringActivity.class);
		
		case InteractionScope.MODE_GNUGO:
			return new Intent(ctx,PlayAgainstGnugoActivity.class);

		case InteractionScope.MODE_SETUP:
			return new Intent(ctx,GoSetupActivity.class);

		default:
			return null;
		}
	}
	public static void startGame(GobandroidFragmentActivity activity,byte mode) {
		activity.getApp().getInteractionScope().setMode(mode);
		activity.startActivity(getIntentByMode(activity, mode));
	}
	
	public static void startGameWithCorrectMode(GobandroidFragmentActivity activity) {
		              startGame(activity,activity.getApp().getInteractionScope().getMode());
	}

}
