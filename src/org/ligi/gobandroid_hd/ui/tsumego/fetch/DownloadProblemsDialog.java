package org.ligi.gobandroid_hd.ui.tsumego.fetch;

import org.ligi.gobandroid_hd.ui.Refreshable;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

public class DownloadProblemsDialog {
	
	private final static String BASE_URL="http://gogameguru.com/i/go-problems/";
	
    public static void show(GobandroidFragmentActivity activity,Refreshable refreshable) {
    	activity.getTracker().trackPageView("/tsumego/refresh");
    	new DownloadTask(activity,refreshable).execute(
    			new TsumegoSource(activity.getSettings().getTsumegoPath()+"1.easy/",BASE_URL,"ggg-easy-%02d.sgf"),
    			new TsumegoSource(activity.getSettings().getTsumegoPath()+"2.intermediate/",BASE_URL,"ggg-intermediate-%02d.sgf"),
    			new TsumegoSource(activity.getSettings().getTsumegoPath()+"3.hard/",BASE_URL,"ggg-hard-%02d.sgf")
    			);
    }
}
