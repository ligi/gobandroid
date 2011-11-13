package org.ligi.gobandroid_hd.ui.application;

import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class GobandroidFragmentActivity extends FragmentActivity {

	private GoogleAnalyticsTracker tracker=null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TraceDroid.init(this);
        Log.setTAG("gobandroid");
        TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this);
    }
    
    public GoogleAnalyticsTracker getTracker() {
    	if (tracker==null) {
            tracker = GoogleAnalyticsTracker.getInstance();
            tracker.startNewSession("UA-27002728-1", this);
    	}
    	return tracker;
    }
    
    @Override
    protected void onDestroy() {
      super.onDestroy();
      tracker.stopSession();
    }

	public GobandroidSettings getSettings() {
		return new GobandroidSettings(this);
	}
}
