package org.ligi.gobandroid_hd.ui.tsumego.fetch;

import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

public class DownloadProblemsDialog {
	
    public static void show(GobandroidFragmentActivity activity) {
    	
    	new DownloadTask(activity).execute(
    			new TsumegoSource(activity.getSettings().getTsumegoPath()+"1.easy/","http://gogameguru.com/i/2011/08/","ggg-easy-%02d.sgf"),
    			new TsumegoSource(activity.getSettings().getTsumegoPath()+"2.intermediate/","http://gogameguru.com/i/2011/08/","ggg-intermediate-%02d.sgf"),
    			new TsumegoSource(activity.getSettings().getTsumegoPath()+"3.hard/","http://gogameguru.com/i/2011/08/","ggg-hard-%02d.sgf")
    			);
    }
}
