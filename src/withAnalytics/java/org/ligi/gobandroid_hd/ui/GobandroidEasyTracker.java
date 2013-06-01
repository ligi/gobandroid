package org.ligi.gobandroid_hd.ui;

import android.app.Activity;
import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;

/**
 * Created by ligi on 6/1/13.
 */
public class GobandroidEasyTracker implements GobandroidTracker {
    @Override
    public void init(Context ctx) {
        EasyTracker.getInstance().setContext(ctx);
    }

    @Override
    public void trackException(String s, Exception e, boolean b) {
        EasyTracker.getTracker().trackException(s,e,b);
    }

    @Override
    public void trackException(String s, boolean b) {
        EasyTracker.getTracker().trackException(s,b);
    }

    @Override
    public void trackEvent(String category, String action, String label, Long val) {
        EasyTracker.getTracker().trackEvent(category, action, label, val);
    }

    @Override
    public void activityStart(Activity activity) {
        EasyTracker.getInstance().activityStart(activity);
    }

    @Override
    public void activityStop(Activity activity) {
        EasyTracker.getInstance().activityStop(activity);
    }
}
