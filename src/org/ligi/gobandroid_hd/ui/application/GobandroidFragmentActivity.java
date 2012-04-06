package org.ligi.gobandroid_hd.ui.application;

import org.ligi.gobandroid_hd.ui.gobandroid;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

public class GobandroidFragmentActivity extends SherlockFragmentActivity {

	private GoogleAnalyticsTracker tracker=null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TraceDroid.init(this);
        Log.setTAG("gobandroid");
        TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this);


        if ((this.getSupportActionBar()!=null) && (this.getSupportActionBar().getCustomView()!=null))
        	this.getSupportActionBar().getCustomView().setFocusable(false);
    }
    
    public boolean doFullScreen() {
    	return false;
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		
    	if (doFullScreen())                
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);                                                                          
        else                                                                                                                                          
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);                                                              
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
      if (tracker!=null) {
    	  tracker.dispatch();
    	  tracker.stopSession();
      }
    }

	public GobandroidSettings getSettings() {
		return new GobandroidSettings(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            Intent intent = new Intent(this, gobandroid.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode==KeyEvent.KEYCODE_WINDOW)
			return false;
		return super.onKeyDown(keyCode, event);
	}
}
