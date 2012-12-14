package org.ligi.gobandroid_hd.backend;

import java.io.IOException;

import org.ligi.gobandroid_beta.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.ingame_common.SwitchModeHelper;
import org.ligi.tracedroid.logging.Log;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;

import com.google.android.gcm.GCMRegistrar;
import com.google.api.services.cloudgoban.model.GoGameParticipation;

public class CloudGobanHelper {

	public static void registerGame(final GobandroidFragmentActivity activity,String game_key,String role,final boolean start_after_reg,Handler handler) {
		
		
		GoGameParticipation gn = new GoGameParticipation();
		gn.setGameKey(game_key);
		gn.setContact(GCMRegistrar
				.getRegistrationId(activity));
		
		gn.setRole(""+role);
		
		for (int attempt=0;attempt<6;attempt++)
			try {
				GoGameParticipation participation=activity.getApp().getCloudgoban().participation().insert(gn).execute();
				if (null!=participation.getEncodedKey()) {
					if (participation.getRole().equals("s")) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								new AlertDialog.Builder(activity).setMessage(R.string.this_game_has_2_players_but_you_can_only_watch_it)
								.setPositiveButton("OK", new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (start_after_reg) {
											activity.finish();
											SwitchModeHelper.startGameWithCorrectMode(activity);
											
										}
									}
									
								}).show();
							}});
						
					}
					else 
						if (start_after_reg)
							SwitchModeHelper.startGameWithCorrectMode(activity);
					activity.getGame().setCloudDefs(game_key, participation.getRole());
					return;
				}
				
				Thread.sleep(attempt*attempt*1000); // exponential back off
			} catch (IOException e) {
				// retries take care of that sort of
			} catch (InterruptedException e) {
				Log.i("cannot sleep");
				e.printStackTrace();
			}
		
		
	}
}
