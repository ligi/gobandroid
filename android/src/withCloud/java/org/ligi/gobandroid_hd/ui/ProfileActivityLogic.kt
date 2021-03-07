package org.ligi.gobandroid_hd.ui

import android.content.IntentSender
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.games.Games
import org.ligi.gobandroid_hd.CloudPrefs
import org.ligi.gobandroid_hd.R
import org.ligi.kaxt.startActivityFromClass

class ProfileActivityLogic : GoogleApiClient.OnConnectionFailedListener {

    internal lateinit var base: BaseProfileActivity
    internal lateinit var mGoogleApiClient: GoogleApiClient

    val signInButton by lazy { base.findViewById(R.id.sign_in_button) as SignInButton }

    val buttonsToShowWhenSignedIn by lazy {
        arrayOf(base.findViewById(R.id.sign_out_button) as Button,
                base.findViewById(R.id.achievements) as Button)
    }

    fun onResume(base: BaseProfileActivity, mGoogleApiClient: GoogleApiClient) {
        this.base = base
        this.mGoogleApiClient = mGoogleApiClient

        base.findViewById<Button>(R.id.sign_out_button).setOnClickListener {
            CloudPrefs.userWantsPlayConnection = false
            mGoogleApiClient.disconnect()
            refresh()
        }

        base.findViewById<Button>(R.id.achievements).setOnClickListener {
            val achievementsIntent = Games.Achievements.getAchievementsIntent(mGoogleApiClient)
            base.startActivityForResult(achievementsIntent, 816)
        }

        base.findViewById<Button>(R.id.turnbased).setOnClickListener {
            base.startActivityFromClass(TurnBasedActivity::class.java)
        }

        base.findViewById<Button>(R.id.sign_in_button).setOnClickListener {
            mGoogleApiClient.registerConnectionFailedListener(this)
            CloudPrefs.userWantsPlayConnection = true
            mGoogleApiClient.connect()
        }

        mGoogleApiClient.registerConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
            override fun onConnected(bundle: Bundle?) {
                refresh()
            }

            override fun onConnectionSuspended(i: Int) {
                refresh()
            }
        })

        refresh()
    }

    fun refresh() {
        signInButton.visibility = if (mGoogleApiClient.isConnected || mGoogleApiClient.isConnecting) View.GONE else View.VISIBLE
        for (button in buttonsToShowWhenSignedIn) {
            button.visibility = if (mGoogleApiClient.isConnected) View.VISIBLE else View.GONE
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        try {
            connectionResult.startResolutionForResult(base, 1001)

            if (connectionResult.hasResolution()) {
                connectionResult.startResolutionForResult(base, 1001)
            } else {
                GooglePlayServicesUtil.showErrorDialogFragment(connectionResult.errorCode, base, null, 100, null)
            }

        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }

    }

}
