package org.ligi.gobandroid_hd.ui.application

import android.content.Context
import android.preference.PreferenceManager
import android.support.annotation.StringRes
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.GoPrefs

class GobandroidSettingsTransition(// we need some context
        private val ctx: Context) {

    fun transition() {
        val prefs = GoPrefs

        if (!prefs.isTransitionDone) {
            prefs.isFullscreenEnabled = isFullscreenEnabled
            prefs.isSoundWanted = isSoundEnabled
            prefs.isLegendEnabled = isLegendEnabled
            prefs.isSGFLegendEnabled = isSGFLegendEnabled
            prefs.isConstantLightWanted = isConstantLightWanted
            prefs.isTsumegoPushEnabled = isTsumegoPushEnabled
            prefs.username = username
            prefs.rank = rank
            prefs.isVersionSeen(preferences.getInt("VERSION", 0))
            prefs.isTransitionDone = true
        }
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(ctx)

    private val isFullscreenEnabled= preferences.getBoolean(ctx.getString(R.string.prefs_fullscreen), false)

    private val isSoundEnabled = getBoolForKey(R.string.prefs_do_sound, true)

    private val isLegendEnabled = getBoolForKey(R.string.prefs_do_legend, true)

    private val isSGFLegendEnabled= getBoolForKey(R.string.prefs_sgf_legend, true)

    private val isConstantLightWanted = getBoolForKey(R.string.prefs_constant_light, true)

    private val isTsumegoPushEnabled= getBoolForKey(R.string.prefs_push_tsumego, true)

    private val username = preferences.getString(KEY_USERNAME, "")

    private val rank = preferences.getString(KEY_RANK, "")


    private fun getBoolForKey(@StringRes stringRes: Int, defaultValue: Boolean): Boolean {
        return preferences.getBoolean(ctx.getString(stringRes), defaultValue)
    }

    companion object {

        private val KEY_USERNAME = "username"
        private val KEY_RANK = "rank"
    }

}