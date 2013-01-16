package org.ligi.gobandroid_hd;

import java.io.IOException;

import org.ligi.gobandroid_hd.GobandroidApp;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.etc.GobandroidConfiguration;
import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.gobandroid_hd.ui.GobandroidNotifications;
import org.ligi.gobandroid_hd.ui.tsumego.fetch.DownloadProblemsForNotification;
import org.ligi.tracedroid.logging.Log;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gcm.GCMBaseIntentService;

/**
 * interface to the C2DM service utilising the classes from google's
 * chrome2phone ( http://chrometophone.googlecode.com )
 * 
 * @author ligi
 * 
 */
public class GCMIntentService extends GCMBaseIntentService {

	public GCMIntentService() {
		super(GobandroidConfiguration.GCM_SENDER_ID);
	}

	@Override
	public void onRegistered(Context context, String registrationId) {
		Log.i("GCM registered with regid:" + registrationId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i("GCM incoming Message");
		EasyTracker.getInstance().setContext(context);
		if (intent.getExtras() == null)
			return;
		Bundle e = intent.getExtras();
		String game_key = e.getString("game_key");
		if (game_key != null) { // cloud game message

			Log.i("GCM act" + ((GobandroidApp) context.getApplicationContext()).getGame().toString());

			GobandroidApp ga = (GobandroidApp) context.getApplicationContext();
			Log.i("GCM incoming Message cloud game" + game_key + "+ game cloud key" + ga.getGame().getCloudKey());
			if (!ga.hasActiveGoActivity() || ga.getGame().getCloudKey() == null || !ga.getGame().getCloudKey().equals(game_key)) {

				new GobandroidNotifications(this).addNewCloudMoveNotification(game_key);
			} else
				try {

					String sgf = ga.getCloudgoban().games().get(game_key).execute().getSgf().getValue();
					ga.getGame().setGame(SGFHelper.sgf2game(sgf, null));

					while (ga.getGame().getActMove().hasNextMove())
						ga.getGame().jump(ga.getGame().getActMove().getnextMove(0)); // mainstream

					ga.getGame().setCloudDefs(game_key, e.getString("role"));

					ga.getGame().notifyGameChange();

				} catch (IOException e1) {

				}

		}
		if (e.getString("max_tsumego") != null) { // todo use the supplied value
													// here
			Log.i("GCM starting DownloadProblemsForNotification");
			DownloadProblemsForNotification.show(context);
		}
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.e("Error in GCM" + errorId);
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {

	}

}
