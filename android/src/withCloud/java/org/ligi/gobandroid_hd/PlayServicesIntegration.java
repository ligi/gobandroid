package org.ligi.gobandroid_hd;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.appstate.OnStateLoadedListener;

import org.ligi.gobandroid_hd.helper.PlayServicesHelper;
import org.ligi.tracedroid.logging.Log;

public class PlayServicesIntegration implements AppLifecycleIntegrator, PlayServicesHelper.GameHelperListener {

    private final PlayServicesHelper playHelper;

    public PlayServicesIntegration(Activity context) {
        playHelper = new PlayServicesHelper(context);
        playHelper.setup(this, PlayServicesHelper.CLIENT_PLUS | PlayServicesHelper.CLIENT_APPSTATE);
    }

    @Override
    public void onSignInFailed() {
    }

    @Override
    public void onSignInSucceeded() {
        Log.i("StatCount" + playHelper.getAppStateClient().getMaxNumKeys());
        playHelper.getAppStateClient().updateState(1, "test".getBytes());
        playHelper.getAppStateClient().loadState(new OnStateLoadedListener() {
            @Override
            public void onStateLoaded(int i, int i2, byte[] bytes) {
                Log.i("AppStateTest", "load?? {" + new String(bytes) + "}");
            }

            @Override
            public void onStateConflict(int i, String s, byte[] bytes, byte[] bytes2) {
                Log.i("AppStateTest", "conflict " + new String(bytes) + " <> " + new String(bytes2));
            }
        }, 1);
    }

    @Override
    public void onStart(Activity activity) {
        playHelper.onStart(activity);
    }

    @Override
    public void onStop(Activity activity) {
        playHelper.onStop();
    }

    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        playHelper.onActivityResult(requestCode, responseCode, intent);
    }

    public PlayServicesHelper getHelper() {
        return playHelper;
    }
}
