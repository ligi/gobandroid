package org.ligi.gobandroid_hd.ui.application;

import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class GobandroidFragmentActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        TraceDroid.init(this);
        Log.setTAG("gobandroid");
        TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this);
    }

	public GobandroidSettings getSettings() {
		return new GobandroidSettings(this);
	}
}
