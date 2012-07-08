package org.ligi.gobandroid_hd;

import org.ligi.gobandroid_hd.backend.GobandroidBackend;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.application.GobandroidSettings;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;

import android.app.Application;
import android.content.pm.PackageManager.NameNotFoundException;

import com.google.android.c2dm.C2DMessaging;

public class GobandroidApp extends Application {

	
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
        
        interaction_scope=new InteractionScope();
        
        C2DMessaging.register(this, "marcus.bueschleb@googlemail.com");
        
	    GobandroidBackend.registerDevice(this);
	}

	@Override
	public void onTerminate() {
		
		super.onTerminate();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
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
