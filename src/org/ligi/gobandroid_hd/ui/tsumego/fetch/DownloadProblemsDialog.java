package org.ligi.gobandroid_hd.ui.tsumego.fetch;

import org.ligi.gobandroid_hd.ui.Refreshable;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

import android.os.AsyncTask;

import com.google.analytics.tracking.android.EasyTracker;

public class DownloadProblemsDialog {

	public static AsyncTask<TsumegoSource[], String, Integer> getAndRunTask(
			GobandroidFragmentActivity activity, Refreshable refreshable) {
		EasyTracker.getTracker().trackEvent("ui_action", "tsumego", "refresh",
				null);
		AsyncTask<TsumegoSource[], String, Integer> res = new DownloadProblemsDialogTask(
				activity, refreshable);
		res.execute(TsumegoDownloadHelper.getDefaultList(activity.getApp()));
		return res;
	}
}
