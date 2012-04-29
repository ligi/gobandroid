package org.ligi.gobandroid_hd;

import org.ligi.gobandroid_hd.backend.GobandroidBackend;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.application.GobandroidSettings;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.c2dm.C2DMessaging;

import android.app.Application;
import android.content.pm.PackageManager.NameNotFoundException;

public class GobandroidApp extends Application {

	private GoogleAnalyticsTracker tracker=null;  // noticed that some analytics really helps
	private InteractionScope interaction_scope;   // holds things like mode/act game between activitysw
	
	public String getAppVersion() {
	    try {
		    return getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		}
		catch (NameNotFoundException e) {
			Log.w("cannot determine app version - that's strange but not critical");
			return "vX.Y";
		}
	    
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		TraceDroid.init(this);
	    Log.setTAG("gobandroid");
	    
	    tracker = GoogleAnalyticsTracker.getInstance();
		
        tracker.startNewSession("UA-27002728-1", 30,this);
        
        interaction_scope=new InteractionScope();
        
        C2DMessaging.register(this, "marcus.bueschleb@googlemail.com");
        
	    getTracker().trackPageView("/enter/v"+getAppVersion());
	
	    GobandroidBackend.registerDevice(this);
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
          Log.w("tracker is null - thats weird");
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
