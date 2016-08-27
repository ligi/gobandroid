package org.ligi.gobandroid_hd.base

import android.app.Activity
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.setFailureHandler
import android.support.test.rule.ActivityTestRule
import android.view.WindowManager
import reporting.SpooningFailureHandler

class EnvironmentPreparingTestRule<T : Activity>(activityClass: Class<T>, autoLaunch: Boolean = true) : ActivityTestRule<T>(activityClass, true, autoLaunch) {

    override fun afterActivityLaunched() {
        super.afterActivityLaunched()
        setFailureHandler(SpooningFailureHandler(InstrumentationRegistry.getInstrumentation()))
        activity.runOnUiThread { activity.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD) }
    }

}