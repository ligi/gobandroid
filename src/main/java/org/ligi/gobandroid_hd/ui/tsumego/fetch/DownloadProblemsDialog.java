package org.ligi.gobandroid_hd.ui.tsumego.fetch;

import android.os.AsyncTask;
import org.ligi.gobandroid_hd.GobandroidApp;
import org.ligi.gobandroid_hd.ui.Refreshable;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

public class DownloadProblemsDialog {

    public static AsyncTask<TsumegoSource[], String, Integer> getAndRunTask(GobandroidFragmentActivity activity, Refreshable refreshable) {
        GobandroidApp.getTracker().trackEvent("ui_action", "tsumego", "refresh", null);
        AsyncTask<TsumegoSource[], String, Integer> res = new DownloadProblemsDialogTask(activity, refreshable);
        res.execute(TsumegoDownloadHelper.getDefaultList(activity.getApp()));
        return res;
    }
}
