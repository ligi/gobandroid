package org.ligi.gobandroid_hd.ui.ingame_common;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.GoInteractionProvider;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.recording.GameRecordActivity;
import org.ligi.gobandroid_hd.ui.review.GameReviewActivity;
import org.ligi.gobandroid_hd.ui.review.GoGamePlayerActivity;
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

public class SwitchModeHelper {
	public static void startGame(GobandroidFragmentActivity activity,byte mode) {
		GoInteractionProvider.setMode(mode);
		Intent go_start_intent=new Intent(activity,GoActivity.class);	
		switch (mode) {
		case GoInteractionProvider.MODE_RECORD:
			go_start_intent=new Intent(activity,GameRecordActivity.class);
			break;
		case GoInteractionProvider.MODE_REVIEW:
			go_start_intent=new Intent(activity,GameReviewActivity.class);
			break;
			
		case GoInteractionProvider.MODE_TSUMEGO:
			go_start_intent=new Intent(activity,TsumegoActivity.class);
			break;
			
		case GoInteractionProvider.MODE_TELEVIZE:
			go_start_intent=new Intent(activity,GoGamePlayerActivity.class);
			break;
		}
		activity.startActivity(go_start_intent);
	}
	
	public static void startGameWithCorrectMode(GobandroidFragmentActivity activity) {
		startGame(activity,GoInteractionProvider.getMode());
	}
	
	public static void show(final GobandroidFragmentActivity activity) {
		
		new AlertDialog.Builder(activity)
			.setTitle(R.string.switch_game_mode)
			.setItems(R.array.gamemode_options,new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int item) {
        			switch(item) {
        			case 0:
        				activity.finish();
        				startGame(activity,GoInteractionProvider.MODE_RECORD);
        				break;

        			case 1:
        				activity.finish();
        				startGame(activity,GoInteractionProvider.MODE_REVIEW);
        				break;
        				
        			case 2:
        				activity.finish();
        				startGame(activity,GoInteractionProvider.MODE_TSUMEGO);
        				break;
        			}
        		}

        	})

			.show();
	}
}
