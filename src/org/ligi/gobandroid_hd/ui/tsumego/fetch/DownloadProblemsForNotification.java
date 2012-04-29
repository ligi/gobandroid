package org.ligi.gobandroid_hd.ui.tsumego.fetch;

import org.ligi.gobandroid_hd.GobandroidApp;
import org.ligi.gobandroid_hd.ui.GobandroidNotifications;

import android.content.Context;

public class DownloadProblemsForNotification {
	
    public static void show(Context ctx) {
    	
    	GobandroidApp app=(GobandroidApp)ctx.getApplicationContext();
    	
    	app.getTracker().trackPageView("/tsumego/refresh_notification");
    	int res=TsumegoDownloadHelper.doDownloadDefault(app);
    	if (res>0)
    		GobandroidNotifications.addNewTsumegosNotification(ctx, res);
    }
}
