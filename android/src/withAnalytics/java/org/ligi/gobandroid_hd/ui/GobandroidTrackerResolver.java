package org.ligi.gobandroid_hd.ui;

public class GobandroidTrackerResolver {

    private static AnalyticsTracker tracker = null;
    public static GobandroidTracker getTracker() {
        if (tracker == null) {
            tracker = new AnalyticsTracker();
        }
        return tracker;
    }
}
