package org.ligi.gobandroid_hd.ui.tsumego.fetch;

import org.ligi.gobandroid_hd.ui.Refreshable;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

import com.google.analytics.tracking.android.EasyTracker;

public class DownloadProblemsDialog {
	
    public static void show(GobandroidFragmentActivity activity,Refreshable refreshable) {
    	EasyTracker.getTracker().trackEvent("ui_action","tsumego","refresh",null);
    	new DownloadProblemsDialogTask(activity,refreshable).execute(TsumegoDownloadHelper.getDefaultList(activity.getApp()));
    }
}
