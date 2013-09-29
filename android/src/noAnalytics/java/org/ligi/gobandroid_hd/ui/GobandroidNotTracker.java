package org.ligi.gobandroid_hd.ui;

import android.app.Activity;
import android.content.Context;

/**
 * this is a dummy
 */
public class GobandroidNotTracker implements GobandroidTracker {
    @Override
    public void init(Context ctx) {
        // our job is to do nothing at all - proguard grab me
    }

    @Override
    public void trackException(String s, Exception e, boolean b) {
        // our job is to do nothing at all - proguard grab me
    }

    @Override
    public void trackException(String s, boolean b) {
        // our job is to do nothing at all - proguard grab me
    }

    @Override
    public void trackEvent(String category, String action, String label, Long val) {
        // our job is to do nothing at all - proguard grab me
    }

    @Override
    public void activityStart(Activity gobandroidFragmentActivity) {
        // our job is to do nothing at all - proguard grab me
    }

    @Override
    public void activityStop(Activity gobandroidFragmentActivity) {
        // our job is to do nothing at all - proguard grab me
    }

}
