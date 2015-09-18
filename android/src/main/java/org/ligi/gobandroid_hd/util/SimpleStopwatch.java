package org.ligi.gobandroid_hd.util;

public class SimpleStopwatch {
    private final long startedTimeStamp;

    public SimpleStopwatch() {
        startedTimeStamp = System.currentTimeMillis();
    }

    public long elapsed() {
        return System.currentTimeMillis() - startedTimeStamp;
    }

}
