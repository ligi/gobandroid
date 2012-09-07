package org.ligi.gobandroid_hd;

import org.ligi.gobandroid_hd.etc.GobandroidConfiguration;
import org.ligi.gobandroid_hd.ui.tsumego.fetch.DownloadProblemsForNotification;
import org.ligi.tracedroid.logging.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gcm.GCMBaseIntentService;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * interface to the C2DM service utilizing the classes from
 * google's chrome2phone ( http://chrometophone.googlecode.com )
 * 
 * @author ligi
 *
 */
public class GCMIntentService extends GCMBaseIntentService  {
	      
  public GCMIntentService() {
    super(GobandroidConfiguration.GCM_SENDER_ID);
  }
  
	@Override
	public void onRegistered(Context context, String registrationId) {
		Log.i("C2DM registered" + registrationId + " regid:" + registrationId ) ;
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		EasyTracker.getInstance().setContext(context);
		if (intent.getExtras()==null)
			return;
		Bundle e=intent.getExtras();
		
		if (e.getString("max_tsumego")!=null) { // todo use the supplied value here
			Log.i("C2DM starting DownloadProblemsForNotification " );
			DownloadProblemsForNotification.show(context);
		}
	}

	@Override 
	public void onError(Context context, String errorId) {
		Log.e("Error in C2DM" + errorId);
	}
	
	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		
	}
	
}
