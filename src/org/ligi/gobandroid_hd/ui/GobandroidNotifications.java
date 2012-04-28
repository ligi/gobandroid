package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_hd.GobandroidApp;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.sgf_listing.SGFSDCardListActivity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class GobandroidNotifications {

	private final static int GOLINK_NOTIFICATION_ID=10001;
	private final static int NEWTSUMEGOS_NOTIFICATION_ID=10002;
	
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
	
	public static String BOOL_FROM_NOTIFICATION_EXTRA_KEY="from_notification";
	public static void addNewTsumegosNotification(Context context,int count) {
		GobandroidApp app=(GobandroidApp)context.getApplicationContext();
		
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Activity.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.ic_launcher, "New Tsumegos available!", System.currentTimeMillis());
 
    	Intent i=new Intent(context,SGFSDCardListActivity.class);    	
    	i.setData((Uri.parse("file://"+app.getSettings().getTsumegoPath())));
    	i.putExtra(BOOL_FROM_NOTIFICATION_EXTRA_KEY, true);
    	app.getInteractionScope().setMode(InteractionScope.MODE_TSUMEGO);
    	
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
        
        notification.defaults=Notification.FLAG_ONLY_ALERT_ONCE+Notification.FLAG_AUTO_CANCEL;
        
        notification.setLatestEventInfo(context, "New Tsumegos available!",""+ count +" new tsumegos", pendingIntent);
        notificationManager.notify(NEWTSUMEGOS_NOTIFICATION_ID, notification);
	}
	
	public static void cancelNewTsumegosNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Activity.NOTIFICATION_SERVICE);
		notificationManager.cancel(NEWTSUMEGOS_NOTIFICATION_ID);
	}
}
