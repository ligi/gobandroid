package org.ligi.gobandroid_hd;

import android.app.Activity;

/**
 * Created by ligi on 7/12/13.
 */
public interface AppLifecycleIntegrator {
    public void onStart(Activity activity);

    public void onStop(Activity activity);
}
