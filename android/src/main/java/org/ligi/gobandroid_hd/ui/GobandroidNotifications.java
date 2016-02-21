package org.ligi.gobandroid_hd.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.BuildConfig;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.etc.GobandroidConfiguration;
import org.ligi.gobandroid_hd.ui.sgf_listing.SGFFileSystemListActivity;

import javax.inject.Inject;

import static org.ligi.gobandroid_hd.InteractionScope.Mode.TSUMEGO;

/**
 * Class to care about notifications used in gobandroid
 *
 * @author <a href="http://ligi.de">Marcus -LiGi- Bueschleb </a>
 *         <p/>
 *         This software is licenced with GPLv3
 */
public class GobandroidNotifications {

    private final static int GOLINK_NOTIFICATION_ID = 10001;
    private final static int NEWTSUMEGOS_NOTIFICATION_ID = 10002;
    private final static int CLOUDMOVE_NOTIFICATION_ID = 10003;

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private Intent notificationIntent;

    @Inject
    InteractionScope interactionScope;

    @Inject
    App app;

    public GobandroidNotifications(Context context) {
        App.component().inject(this);
        notificationBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher);
        notificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
        notificationBuilder.setSound(Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID + "/" + R.raw.go_place1));
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher);

        // notificationBuilder.setVibrate(new long[] { 1000, 100, 1000, 100 });
        // needs permission vibrate - not yet sure if wanted yet
    }

    public void addGoLinkNotification(String golink) {
        notificationBuilder.setTicker(app.getString(R.string.the_go_game_you_reviewed));

        notificationIntent = new Intent(app, GoLinkLoadActivity.class);
        notificationIntent.setData(Uri.parse(golink));

        notificationBuilder.setContentTitle(app.getString(R.string.the_go_game_you_reviewed));
        notificationBuilder.setContentText(golink);

        doNotify(GOLINK_NOTIFICATION_ID);
    }

    public void cancelGoLinkNotification() {
        notificationManager.cancel(GOLINK_NOTIFICATION_ID);
    }

    public static String BOOL_FROM_NOTIFICATION_EXTRA_KEY = "from_notification";

    public void addNewTsumegosNotification(int count) {
        notificationBuilder.setTicker(app.getString(R.string.new_tsumegos_available));

        notificationIntent = new Intent(app, SGFFileSystemListActivity.class);
        notificationIntent.setData((Uri.parse("file://" + App.component().settings().getTsumegoPath())));
        notificationIntent.putExtra(BOOL_FROM_NOTIFICATION_EXTRA_KEY, true);
        interactionScope.setMode(TSUMEGO);

        notificationBuilder.setContentTitle(app.getString(R.string.new_tsumegos_available));
        notificationBuilder.setContentText("" + count + " new tsumegos");

        doNotify(NEWTSUMEGOS_NOTIFICATION_ID);
    }

    public void cancelNewTsumegosNotification() {
        notificationManager.cancel(NEWTSUMEGOS_NOTIFICATION_ID);
    }

    public void addNewCloudMoveNotification(String game_key) {

        notificationBuilder.setTicker(app.getString(R.string.a_move_in_one_online_game_you_participate_is_done));
        notificationBuilder.setContentTitle(app.getString(R.string.new_move));
        notificationBuilder.setContentText(app.getString(R.string.in_an_online_game_you_participate));

        // create the pending intent as reaction to click
        notificationIntent = new Intent(app, SGFLoadActivity.class);
        notificationIntent.setData(Uri.parse(GobandroidConfiguration.CLOUD_GOBAN_URL_BASE + game_key));
        notificationIntent.putExtra(BOOL_FROM_NOTIFICATION_EXTRA_KEY, true);

        doNotify(CLOUDMOVE_NOTIFICATION_ID);
    }

    private void doNotify(int id) {
        PendingIntent pendingIntent = PendingIntent.getActivity(app, 0, notificationIntent, 0);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(id, notificationBuilder.build());
    }

    public void cancelCloudMoveNotification() {
        notificationManager.cancel(CLOUDMOVE_NOTIFICATION_ID);
    }

}
