package org.ligi.gobandroid_hd.ui;

import android.app.Activity;
import android.content.Context;

public interface GobandroidTracker {

    public void init(Context ctx);

    void trackException(String s, Exception e, boolean b);

    void trackException(String s, boolean b);

    void trackEvent(String category, String action, String label, Long val);

    void activityStart(Activity gobandroidFragmentActivity);

    void activityStop(Activity gobandroidFragmentActivity);
}
