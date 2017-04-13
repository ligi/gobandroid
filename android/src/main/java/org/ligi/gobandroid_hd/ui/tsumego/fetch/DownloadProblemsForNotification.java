package org.ligi.gobandroid_hd.ui.tsumego.fetch;

import android.content.Context;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.ui.GobandroidNotifications;

public class DownloadProblemsForNotification {

    public static void show(Context ctx) {
        final App app = (App) ctx.getApplicationContext();

        App.Companion.getTracker().trackEvent("ui_action", "tsumego", "refresh_notification", null);

        final int res = TsumegoDownloadHelper.INSTANCE.doDownloadDefault(app);
        if (res > 0) {
            new GobandroidNotifications(ctx).addNewTsumegosNotification(res);
        }
    }
}
