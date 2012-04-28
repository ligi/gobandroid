package org.ligi.gobandroid_hd.ui.tsumego.fetch;

import org.ligi.gobandroid_hd.ui.Refreshable;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

public class DownloadProblemsDialog {
	
    public static void show(GobandroidFragmentActivity activity,Refreshable refreshable) {
    	activity.getTracker().trackPageView("/tsumego/refresh");
    	new DownloadProblemsDialogTask(activity,refreshable).execute(TsumegoDownloadHelper.getDefaultList(activity.getApp()));
    }
}
