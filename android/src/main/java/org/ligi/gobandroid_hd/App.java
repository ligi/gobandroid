package org.ligi.gobandroid_hd;

import android.app.Application;
import android.content.pm.PackageManager.NameNotFoundException;

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
    private static GoGame game;

    public static boolean isTesting = false;

    public static GobandroidSettings getGobandroidSettings() {
        return new GobandroidSettings(instance);
    }

    // the InteractionScope holds things like mode/act game between activities
    private static InteractionScope interaction_scope;

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

        CloudHooks.onApplicationCreation(this);

    }

    public static InteractionScope getInteractionScope() {
        return interaction_scope;
    }

    public static GoGame getGame() {
        if (game == null) {
            game = new GoGame((byte) 9);
        }
        return game;
    }

    public static void setGame(GoGame p_game) {
        getInteractionScope().ask_variant_session = true;

        if (game == null) {
            game = p_game;
        } else { // keep listeners and stuff
            game.setGame(p_game);
        }
    }

    public GobandroidSettings getSettings() {
        return new GobandroidSettings(this);
    }


    public static GobandroidTracker getTracker() {
        return GobandroidTrackerResolver.getTracker();
    }
}
