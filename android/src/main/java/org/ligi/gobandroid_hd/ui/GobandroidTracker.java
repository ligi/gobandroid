package org.ligi.gobandroid_hd.ui;

import android.content.Context;

public interface GobandroidTracker {

    void init(Context ctx);

    void trackException(String s, Exception e, boolean b);

    void trackException(String s, boolean b);

    void trackEvent(String category, String action, String label, Long val);
}
