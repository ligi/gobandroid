package org.ligi.gobandroid_hd;

import android.app.Activity;
import android.content.Intent;

import org.ligi.gobandroid_hd.helper.PlayServicesHelper;
import org.ligi.tracedroid.logging.Log;

public class PlayServicesIntegration implements AppLifecycleIntegrator, PlayServicesHelper.GameHelperListener {

    private final PlayServicesHelper playHelper;

    public PlayServicesIntegration(Activity context) {
        playHelper = new PlayServicesHelper(context);
        playHelper.setup(this, PlayServicesHelper.CLIENT_PLUS );
    }

    @Override
    public void onSignInFailed() {
    }

    @Override
    public void onSignInSucceeded() {
    }

    @Override
    public void onStart(Activity activity) {
        playHelper.onStart(activity);
    }

    @Override
    public void onStop(Activity activity) {
        playHelper.onStop();
    }

}
