package org.ligi.gobandroid_hd.ui.tsumego.fetch;

import android.os.AsyncTask;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.ui.Refreshable;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

public class DownloadProblemsDialog {

    public static AsyncTask<TsumegoSource[], String, Integer> getAndRunTask(GobandroidFragmentActivity activity, Refreshable refreshable) {
        App.getTracker().trackEvent("ui_action", "tsumego", "refresh", null);
        final AsyncTask<TsumegoSource[], String, Integer> res = new DownloadProblemsDialogTask(activity, refreshable);
        res.execute(TsumegoDownloadHelper.getDefaultList(activity.getApp()));
        return res;
    }
}
