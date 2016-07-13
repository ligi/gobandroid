package org.ligi.gobandroid_hd.base

import android.app.Activity
import android.support.annotation.StringRes
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.setFailureHandler
import android.view.WindowManager
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.logic.sgf.SGFReader
import reporting.SpooningFailureHandler

abstract class AssetAwareJunitTest {

    open fun setUp(activity: Activity) {
        setFailureHandler(SpooningFailureHandler(getInstrumentation()))
        activity.runOnUiThread { activity.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD) }
    }

    fun readAsset(fileName: String): String? {
        return GobandroidTestBaseUtil.readAsset(getInstrumentation().context, fileName)
    }

    fun readGame(fileName: String): GoGame {
        return SGFReader.sgf2game(readAsset("sgf/$fileName.sgf"), null)
    }

    fun getString(@StringRes resId: Int): String {
        return getInstrumentation().targetContext.getString(resId)
    }

}
