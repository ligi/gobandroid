package org.ligi.gobandroid_hd;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;
import com.chibatching.kotpref.Kotpref;
import org.ligi.gobandroid_hd.etc.AppComponent;
import org.ligi.gobandroid_hd.etc.AppModule;
import org.ligi.gobandroid_hd.etc.DaggerAppComponent;
import org.ligi.gobandroid_hd.ui.GoPrefs;
import org.ligi.gobandroid_hd.ui.GobandroidTracker;
import org.ligi.gobandroid_hd.ui.GobandroidTrackerResolver;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;

/**
 * the central Application-Context
 */
public class App extends Application {


    private static AppComponent component;

    public static boolean isTesting = false;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerAppComponent.builder().appModule(new AppModule(this)).build();

        getTracker().init(this);

        TraceDroid.init(this);
        Log.setTAG("gobandroid");

        CloudHooks.onApplicationCreation(this);

        Kotpref.INSTANCE.init(this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        AppCompatDelegate.setDefaultNightMode(GoPrefs.INSTANCE.getThemeInt());

    }

    public static GobandroidTracker getTracker() {
        return GobandroidTrackerResolver.getTracker();
    }

    public static AppComponent component() {
        return component;
    }

    public static void setComponent(AppComponent newComponent) {
        component = newComponent;
    }
}
