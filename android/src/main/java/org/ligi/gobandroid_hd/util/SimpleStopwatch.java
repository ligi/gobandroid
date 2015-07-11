package org.ligi.gobandroid_hd.util;

public class SimpleStopwatch {
    private Long startedTimeStamp = null;

    public long elapsed() {
        return System.currentTimeMillis() - startedTimeStamp;
    }

    public boolean isRunning() {
        return startedTimeStamp != null;
    }

    public void start() {
        startedTimeStamp = System.currentTimeMillis();
    }

    public void reset() {
        startedTimeStamp = null;
    }
}
