package org.ligi.gobandroid_hd;

import android.app.Application;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.GobandroidTracker;
import org.ligi.gobandroid_hd.ui.GobandroidTrackerResolver;
import org.ligi.gobandroid_hd.ui.application.GobandroidSettings;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;

/**
 * the central Application-Context
 */
public class App extends Application {

    private static App instance;
    public static boolean isTesting = false;

    public static GobandroidSettings getGobandroidSettings() {
        return new GobandroidSettings(instance);
    }

    ;

    // the InteractionScope holds things like mode/act game between activities
    private InteractionScope interaction_scope;

    public static String getVersion() {
        try {
            return instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            Log.w("cannot determine app version - that's strange but not critical");
            return "vX.Y";
        }
    }


    public static int getVersionCode() {
        try {
            return instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            Log.w("cannot determine app version - that's strange but not critical");
            return 0;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        getTracker().init(this);

        TraceDroid.init(this);
        Log.setTAG("gobandroid");

        interaction_scope = new InteractionScope();

        if (Build.VERSION.SDK_INT > 7) { // need at least 8 for GCM
            initGCM();
        }

        CloudHooks.onApplicationCreation(this);

    }


    private void initGCM() {
        /*try {
            // Make sure the device has the proper dependencies.
            GCMRegistrar.checkDevice(this);

            final String regId = GCMRegistrar.getRegistrationId(this);
            Log.i("initGCM with regId=" + regId);
            if (TextUtils.isEmpty(regId)) {
                // Automatically registers application on startup.
                GCMRegistrar.register(this, GobandroidConfiguration.GCM_SENDER_ID);
            } else {
                GobandroidBackend.registerDevice(this);
            }
        } catch (Exception e) {
            getTracker().trackException("cannot init GCM", e, false);
        }
        */
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


    /*
    public Cloudgoban getCloudgoban() {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = new GsonFactory();

        return new Cloudgoban(transport, jsonFactory, null);

    }
    */


    public static GobandroidTracker getTracker() {
        return GobandroidTrackerResolver.getTracker();
    }
}
