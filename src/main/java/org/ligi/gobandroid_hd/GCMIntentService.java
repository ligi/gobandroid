package org.ligi.gobandroid_hd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gcm.GCMBaseIntentService;
import org.ligi.gobandroid_hd.etc.GobandroidConfiguration;
import org.ligi.gobandroid_hd.ui.tsumego.fetch.DownloadProblemsForNotification;
import org.ligi.tracedroid.logging.Log;

/**
 * interface to the C2DM service utilising the classes from google's
 * chrome2phone ( http://chrometophone.googlecode.com )
 *
 * @author ligi
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

        if (intent.getExtras() == null)
            return;

        App.getTracker().init(context);
        CloudHooks.onGCMMessage(context,intent);

        Bundle extras = intent.getExtras();

        if (extras.getString("max_tsumego") != null) { // todo use the supplied value
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
