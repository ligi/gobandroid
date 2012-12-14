package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_beta.R;
import org.ligi.gobandroid_hd.GobandroidApp;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.etc.GobandroidConfiguration;
import org.ligi.gobandroid_hd.ui.sgf_listing.SGFSDCardListActivity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Class to care about notifications used in gobandroid
 * 
 * @author <a href="http://ligi.de">Marcus -LiGi- Bueschleb </a>
 * 
 *         This software is licenced with GPLv3
 */
public class GobandroidNotifications {

	private final static int GOLINK_NOTIFICATION_ID = 10001;
	private final static int NEWTSUMEGOS_NOTIFICATION_ID = 10002;
	private final static int CLOUDMOVE_NOTIFICATION_ID = 10003;

	public static void addGoLinkNotification(Context context, String golink) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher,
				context.getString(R.string.the_go_game_you_reviewed), System.currentTimeMillis());

		Intent notificationIntent = new Intent(context,
				GoLinkLoadActivity.class);
		notificationIntent.setData(Uri.parse(golink));
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);

		notification.defaults = Notification.FLAG_ONLY_ALERT_ONCE
				+ Notification.FLAG_AUTO_CANCEL;

		notification.setLatestEventInfo(context, context.getString(R.string.the_go_game_you_reviewed),
				golink, pendingIntent);
		notificationManager.notify(GOLINK_NOTIFICATION_ID, notification);
	}

	public static void cancelGoLinkNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		notificationManager.cancel(GOLINK_NOTIFICATION_ID);
	}

	public static String BOOL_FROM_NOTIFICATION_EXTRA_KEY = "from_notification";

	public static void addNewTsumegosNotification(Context context, int count) {
		GobandroidApp app = (GobandroidApp) context.getApplicationContext();

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher,
				context.getString(R.string.new_tsumegos_available), System.currentTimeMillis());

		Intent i = new Intent(context, SGFSDCardListActivity.class);
		i.setData((Uri.parse("file://" + app.getSettings().getTsumegoPath())));
		i.putExtra(BOOL_FROM_NOTIFICATION_EXTRA_KEY, true);
		app.getInteractionScope().setMode(InteractionScope.MODE_TSUMEGO);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i,
				0);

		notification.defaults = Notification.FLAG_ONLY_ALERT_ONCE
				+ Notification.FLAG_AUTO_CANCEL;

		notification.setLatestEventInfo(context, context.getString(R.string.new_tsumegos_available), ""
				+ count + " new tsumegos", pendingIntent);
		notificationManager.notify(NEWTSUMEGOS_NOTIFICATION_ID, notification);
	}
	

	public static void cancelNewTsumegosNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		notificationManager.cancel(NEWTSUMEGOS_NOTIFICATION_ID);
	}
	
	public static void addNewCloudMoveNotification(Context context, String game_key) {
		GobandroidApp app = (GobandroidApp) context.getApplicationContext();

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		
		Notification notification = new Notification(R.drawable.ic_launcher,
				"A move in one online game you play is done", System.currentTimeMillis());

		Intent i = new Intent(context, SGFLoadActivity.class);
		i.setData(Uri.parse(GobandroidConfiguration.CLOUD_GOBAN_URL_BASE +game_key));
		i.putExtra(BOOL_FROM_NOTIFICATION_EXTRA_KEY, true);
		app.getInteractionScope().setMode(InteractionScope.MODE_TSUMEGO);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i,
				0);

		notification.defaults = Notification.FLAG_ONLY_ALERT_ONCE
				+ Notification.FLAG_AUTO_CANCEL;

		notification.setLatestEventInfo(context, "A online game you played", "new move"
				, pendingIntent);
		notificationManager.notify(CLOUDMOVE_NOTIFICATION_ID, notification);
	}
	
	public static void cancelCloudMoveNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		notificationManager.cancel(CLOUDMOVE_NOTIFICATION_ID);
	}
	

}
