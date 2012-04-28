package org.ligi.gobandroid_hd;

import java.io.IOException;

import org.ligi.gobandroid_hd.backend.GobandroidBackend;
import org.ligi.gobandroid_hd.ui.tsumego.fetch.DownloadProblemsForNotification;
import org.ligi.tracedroid.logging.Log;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Secure;

import com.google.android.c2dm.C2DMBaseReceiver;

/**
 * interface to the C2DM service utilizing the classes from
 * google's chrome2phone ( http://chrometophone.googlecode.com )
 * 
 * @author ligi
 *
 */
public class C2DMReceiver extends C2DMBaseReceiver {

	public C2DMReceiver() {
		super("marcus.bueschleb@googlemail.com"); // init with our ID
	}

	@Override
	public void onRegistered(Context context, String registrationId)
			throws IOException {
		super.onRegistered(context, registrationId);
		String device_id=Secure.getString( getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
		boolean res=GobandroidBackend.registerPush(device_id, registrationId);
		Log.i("C2DM registered" + registrationId + " regid:" + res + " device_id" + device_id) ;
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		String msg=intent.getExtras().getString("message");
		
		Log.i("C2DM Message " + msg);
		
		if (msg.equals("new_tsumegos")) {
			Log.i("C2DM starting DownloadProblemsForNotification " );
			DownloadProblemsForNotification.show(context);
		}
	}

	@Override 
	public void onError(Context context, String errorId) {
		Log.e("Error in C2DM" + errorId);
	}
}
