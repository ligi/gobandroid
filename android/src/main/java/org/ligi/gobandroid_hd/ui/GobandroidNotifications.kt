package org.ligi.gobandroid_hd.ui

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.BuildConfig
import org.ligi.gobandroid_hd.InteractionScope
import org.ligi.gobandroid_hd.InteractionScope.Mode.TSUMEGO
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.etc.GobandroidConfiguration
import org.ligi.gobandroid_hd.ui.sgf_listing.SGFFileSystemListActivity

/**
 * Class to care about notifications used in gobandroid

 * @author [Marcus -LiGi- Bueschleb ](http://ligi.de)
 * *
 *
 *
 * *         This software is licenced with GPLv3
 */
class GobandroidNotifications(context: Context) {

    private val notificationBuilder: NotificationCompat.Builder
    private val notificationManager: NotificationManager
    private var notificationIntent: Intent? = null

    internal val interactionScope: InteractionScope  by App.kodein.lazy.instance()
    internal val app: App  by App.kodein.lazy.instance()

    init {
        notificationBuilder = NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
        notificationManager = context.getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder.setSound(Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID + "/" + R.raw.go_place1))
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)

        // notificationBuilder.setVibrate(new long[] { 1000, 100, 1000, 100 });
        // needs permission vibrate - not yet sure if wanted yet
    }

    fun addGoLinkNotification(golink: String) {
        notificationBuilder.setTicker(app.getString(R.string.the_go_game_you_reviewed))

        notificationIntent = Intent(app, GoLinkLoadActivity::class.java)
        notificationIntent!!.data = Uri.parse(golink)

        notificationBuilder.setContentTitle(app.getString(R.string.the_go_game_you_reviewed))
        notificationBuilder.setContentText(golink)

        doNotify(GOLINK_NOTIFICATION_ID)
    }

    fun cancelGoLinkNotification() {
        notificationManager.cancel(GOLINK_NOTIFICATION_ID)
    }

    fun addNewTsumegosNotification(count: Int) {
        notificationBuilder.setTicker(app.getString(R.string.new_tsumegos_available))

        notificationIntent = Intent(app, SGFFileSystemListActivity::class.java)
        notificationIntent!!.data = Uri.parse("file://" + App.env.tsumegoPath)
        notificationIntent!!.putExtra(BOOL_FROM_NOTIFICATION_EXTRA_KEY, true)
        interactionScope.mode = TSUMEGO

        notificationBuilder.setContentTitle(app.getString(R.string.new_tsumegos_available))
        notificationBuilder.setContentText("" + count + " new tsumegos")

        doNotify(NEWTSUMEGOS_NOTIFICATION_ID)
    }

    fun cancelNewTsumegosNotification() {
        notificationManager.cancel(NEWTSUMEGOS_NOTIFICATION_ID)
    }

    fun addNewCloudMoveNotification(game_key: String) {

        notificationBuilder.setTicker(app.getString(R.string.a_move_in_one_online_game_you_participate_is_done))
        notificationBuilder.setContentTitle(app.getString(R.string.new_move))
        notificationBuilder.setContentText(app.getString(R.string.in_an_online_game_you_participate))

        // create the pending intent as reaction to click
        notificationIntent = Intent(app, SGFLoadActivity::class.java)
        notificationIntent!!.data = Uri.parse(GobandroidConfiguration.CLOUD_GOBAN_URL_BASE + game_key)
        notificationIntent!!.putExtra(BOOL_FROM_NOTIFICATION_EXTRA_KEY, true)

        doNotify(CLOUDMOVE_NOTIFICATION_ID)
    }

    private fun doNotify(id: Int) {
        val pendingIntent = PendingIntent.getActivity(app, 0, notificationIntent, 0)
        notificationBuilder.setContentIntent(pendingIntent)
        notificationManager.notify(id, notificationBuilder.build())
    }

    fun cancelCloudMoveNotification() {
        notificationManager.cancel(CLOUDMOVE_NOTIFICATION_ID)
    }

    companion object {

        private val GOLINK_NOTIFICATION_ID = 10001
        private val NEWTSUMEGOS_NOTIFICATION_ID = 10002
        private val CLOUDMOVE_NOTIFICATION_ID = 10003

        var BOOL_FROM_NOTIFICATION_EXTRA_KEY = "from_notification"
    }

}
