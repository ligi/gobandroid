package org.ligi.gobandroid_hd;

import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.application.GobandroidSettings;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import android.app.Application;

public class GobandroidApp extends Application {

	private GoogleAnalyticsTracker tracker=null;  // noticed that some analytics really helps
	private InteractionScope interaction_scope;   // holds things like mode/act game between activitysw
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		TraceDroid.init(this);
	    Log.setTAG("gobandroid");
	    
	    tracker = GoogleAnalyticsTracker.getInstance();
        tracker.startNewSession("UA-27002728-1", this);
        
        interaction_scope=new InteractionScope();
	}

	@Override
	public void onTerminate() {
		if (tracker!=null) {
	    	  tracker.dispatch();
	    	  tracker.stopSession();
	      }
		
		super.onTerminate();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	public GoogleAnalyticsTracker getTracker() {
    	if (tracker==null) {
          
    	}
    	return tracker;
    }
	
	public InteractionScope getInteractionScope() {
		return interaction_scope;
	}
	
	public GoGame getGame() {
		return getInteractionScope().getGame();
	}
	

	public GobandroidSettings getSettings() {
		return new GobandroidSettings(this);
	}
}
