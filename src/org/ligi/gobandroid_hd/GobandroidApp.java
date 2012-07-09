package org.ligi.gobandroid_hd;

import org.ligi.gobandroid_hd.backend.GobandroidBackend;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.application.GobandroidSettings;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;

import android.app.Application;
import android.content.pm.PackageManager.NameNotFoundException;

import com.google.android.gcm.GCMRegistrar;

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
	
	public final static String SENDER_ID="280756770711";
	
	@Override
	public void onCreate() {
		super.onCreate();
		TraceDroid.init(this);
	    Log.setTAG("gobandroid");
        
        interaction_scope=new InteractionScope();

        // Make sure the device has the proper dependencies.                                                                
        GCMRegistrar.checkDevice(this);
        // Make sure the manifest was properly set - comment out this line                                                  
        // while developing the app, then uncomment it when it's ready.                                                     
        GCMRegistrar.checkManifest(this);
        //setContentView(R.layout.main);
        
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Automatically registers application on startup.                                                              
            GCMRegistrar.register(this, SENDER_ID);
        }
        Log.i("gobandroid GCM regid  " + regId);
        
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
