package org.ligi.gobandroid_hd

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.games.Games
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.ligi.gobandroid_hd.events.OptionsItemClickedEvent
import org.ligi.gobandroid_hd.events.TsumegoSolved
import org.ligi.gobandroid_hd.ui.BaseProfileActivity
import org.ligi.gobandroid_hd.ui.ProfileActivityLogic
import org.ligi.kaxt.startActivityFromClass
import org.ligi.tracedroid.logging.Log

object CloudHooks {

    lateinit var ctx: Activity

    val googleApiClient: GoogleApiClient by lazy {
        GoogleApiClient.Builder(ctx).addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
            override fun onConnected(bundle: Bundle?) {
                Log.i("connected")
            }

            override fun onConnectionSuspended(i: Int) {
                // Attempt to reconnect
                googleApiClient.connect()
            }
        }).addApi(Games.API).addScope(Games.SCOPE_GAMES).build()// add other APIs and scopes here as needed
    }

    @TargetApi(14)
    fun onApplicationCreation(app: App) {

        if (Build.VERSION.SDK_INT < 14) {
            return
        }

        val profileActivityLogic = ProfileActivityLogic()

        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {

            @Subscribe
            fun onEvent(event: TsumegoSolved) {
                if (!googleApiClient.isConnected) {
                    return
                }
                Games.Achievements.unlockImmediate(googleApiClient, "CgkIl6-h85UIEAIQBw");
            }

            @Subscribe
            fun onEvent(event: OptionsItemClickedEvent) {
                if (event.optionsItemId == R.id.menu_record) {
                    if (!googleApiClient.isConnected) {
                        AlertDialog.Builder(ctx).setMessage("You need to be signed in to google play games services to use this feature.")
                                .setPositiveButton(android.R.string.ok, { dialogInterface, i ->
                                    dialogInterface.dismiss()
                                    ctx.startActivityFromClass(BaseProfileActivity::class.java)
                                })
                                .show()
                        return
                    }
                    ctx.startActivityForResult(Games.Videos.getCaptureOverlayIntent(googleApiClient), 101)
                }
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                ctx = activity
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {
                EventBus.getDefault().register(this)
                if (CloudPrefs.userWantsPlayConnection) {
                    if (!googleApiClient.isConnected) {
                        googleApiClient.connect()
                    }
                }
                if (activity is BaseProfileActivity) {
                    profileActivityLogic.onResume(activity, googleApiClient)
                }
            }

            override fun onActivityPaused(activity: Activity) {
                EventBus.getDefault().unregister(this)
                if (googleApiClient.isConnected) {
                    googleApiClient.disconnect()
                }
            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }
        })

    }

}
