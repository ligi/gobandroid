package org.ligi.gobandroid_hd.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.etc.GobandroidConfiguration;
import org.ligi.gobandroid_hd.ui.tsumego.fetch.DownloadProblemsForNotification;
import org.ligi.tracedroid.logging.Log;

public class GCMIntentService extends IntentService {

    public GCMIntentService() {
        super(GobandroidConfiguration.GCM_SENDER_ID);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getExtras() == null) {
            return;
        }

        Log.i("GCM incoming Message");

        App.Companion.getTracker().init(getApplicationContext());

        final Bundle extras = intent.getExtras();
        final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        final String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle

            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Release the wake lock provided by the WakefulBroadcastReceiver.


                if (extras.getString("max_tsumego") != null) { // todo use the supplied value here
                    Log.i("GCM starting DownloadProblemsForNotification");
                    App.Companion.getTracker().trackEvent("event", "gcm", "trigger", 0L);
                    DownloadProblemsForNotification.show(getBaseContext());
                }
            }
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
    }
}


