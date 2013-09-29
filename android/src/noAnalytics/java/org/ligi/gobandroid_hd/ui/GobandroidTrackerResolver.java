package org.ligi.gobandroid_hd.ui;

/**
 * Created by ligi on 6/1/13.
 */
public class GobandroidTrackerResolver {

    public static GobandroidTracker getTracker() {
        return new GobandroidNotTracker();
    }
}
