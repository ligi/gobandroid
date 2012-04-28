package org.ligi.gobandroid_hd.ui.ingame_common;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.recording.GameRecordActivity;
import org.ligi.gobandroid_hd.ui.review.GameReviewActivity;
import org.ligi.gobandroid_hd.ui.review.GoGamePlayerActivity;
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoActivity;
import android.content.Intent;

public class SwitchModeHelper {
	public static void startGame(GobandroidFragmentActivity activity,byte mode) {
		activity.getApp().getInteractionScope().setMode(mode);
		Intent go_start_intent=new Intent(activity,GoActivity.class);	
		switch (mode) {
		case InteractionScope.MODE_RECORD:
			go_start_intent=new Intent(activity,GameRecordActivity.class);
			break;
		case InteractionScope.MODE_REVIEW:
			go_start_intent=new Intent(activity,GameReviewActivity.class);
			break;
			
		case InteractionScope.MODE_TSUMEGO:
			go_start_intent=new Intent(activity,TsumegoActivity.class);
			break;
			
		case InteractionScope.MODE_TELEVIZE:
			go_start_intent=new Intent(activity,GoGamePlayerActivity.class);
			break;
		}
		activity.startActivity(go_start_intent);
	}
	
	public static void startGameWithCorrectMode(GobandroidFragmentActivity activity) {
		              startGame(activity,activity.getApp().getInteractionScope().getMode());
	}

}
