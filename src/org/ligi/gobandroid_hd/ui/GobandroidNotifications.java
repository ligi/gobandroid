package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_hd.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class GobandroidNotifications {

	private final static int GOLINK_NOTIFICATION_ID=10001;
	
	public static void addGoLinkNotification(Context context,String golink) {
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Activity.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.ic_launcher, "The Go Game you reviewed", System.currentTimeMillis());
 
        Intent notificationIntent = new Intent(context,GoLinkLoadActivity.class);
        notificationIntent.setData(Uri.parse(golink));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        
        notification.defaults=Notification.FLAG_ONLY_ALERT_ONCE+Notification.FLAG_AUTO_CANCEL;
        
        notification.setLatestEventInfo(context, "The Go Game you reviewed",golink, pendingIntent);
        notificationManager.notify(GOLINK_NOTIFICATION_ID, notification);
	}
	
	public static void cancelGoLinkNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Activity.NOTIFICATION_SERVICE);
		notificationManager.cancel(GOLINK_NOTIFICATION_ID);
	}
}
