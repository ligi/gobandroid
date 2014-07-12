package org.ligi.gobandroid_hd.ui;

public class GobandroidTrackerResolver {

    public static GobandroidTracker getTracker() {
        return new GobandroidEasyTracker();
    }
}
