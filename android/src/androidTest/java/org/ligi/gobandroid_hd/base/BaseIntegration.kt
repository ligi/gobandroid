package org.ligi.gobandroid_hd.base

import android.app.Activity
import android.support.annotation.StringRes
import android.support.test.espresso.Espresso.setFailureHandler
import android.test.ActivityInstrumentationTestCase2
import android.view.WindowManager
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.logic.sgf.SGFReader
import reporting.SpooningFailureHandler

abstract class BaseIntegration<T : Activity>(activityClass: Class<T>) : ActivityInstrumentationTestCase2<T>(activityClass) {

    @Throws(Exception::class)
    public override fun setUp() {
        super.setUp()
        setFailureHandler(SpooningFailureHandler(this.instrumentation))
    }

    override fun getActivity(): T {
        val activity = super.getActivity()
        activity.runOnUiThread { activity.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD) }

        return activity
    }

    protected fun readAsset(fileName: String): String? {
        return GobandroidTestBaseUtil.readAsset(instrumentation.context, fileName)
    }

    protected fun readGame(fileName: String): GoGame {
        return SGFReader.sgf2game(readAsset("sgf/$fileName.sgf"), null)
    }

    protected fun getString(@StringRes resId: Int): String {
        return instrumentation.targetContext.getString(resId)
    }

}
