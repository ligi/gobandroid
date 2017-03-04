/**
 * gobandroid
 * by Marcus -Ligi- Bueschleb
 * http://ligi.de
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see //www.gnu.org/licenses/>.
 */

package org.ligi.gobandroid_hd.ui

import android.support.v7.app.AppCompatDelegate
import com.chibatching.kotpref.KotprefModel
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.application.GoAndroidEnvironment

/**
 * Class to store and access Preferences used in Gobandroid

 * @author [Marcus -LiGi- Bueschleb ](http://ligi.de)
 * *
 *
 *
 * *         This software is licenced with GPLv3
 */

object GoPrefs : KotprefModel() {

    override val kotprefName: String = GoAndroidEnvironment.Companion.settingsXMLName

    var isAnnounceMoveActive: Boolean by booleanPref()
    var isShowForwardAlertWanted: Boolean by booleanPref(default = true, key = "show_var_alert_win")
    var isSoundWanted: Boolean by booleanPref(default = true, key = R.string.prefs_do_sound)

    var isKeepVariantWanted: Boolean by booleanPref(default = true)

    var hasAcknowledgedJunctionInfo: Boolean by booleanPref(default = false)

    var lastBoardSize: Int by intPref(default = 9)
    var lastHandicap: Int by intPref(default = 0)

    var boardLineWidth: Int by intPref(default = 1, key = R.string.prefs_linewidth)

    var username: String by stringPref()
    var rank: String by stringPref()

    var isFullscreenEnabled: Boolean by booleanPref(key = R.string.prefs_fullscreen)
    var isLegendEnabled: Boolean by booleanPref(key = R.string.prefs_do_legend, default = true)
    var isSGFLegendEnabled: Boolean by booleanPref(key = R.string.prefs_sgf_legend, default = true)
    var isConstantLightWanted: Boolean by booleanPref(key = R.string.prefs_constant_light, default = true)
    var isTsumegoPushEnabled: Boolean by booleanPref(key = R.string.prefs_push_tsumego, default = true)

    var isTransitionDone: Boolean by booleanPref(default = false)
    var isTsumegoCleanDone: Boolean by booleanPref(default = false)

    private val dayNightModeString: String by stringPref(key = R.string.prefs_daynight)
    private var lastSeenSGFPackInt: Int by intPref()

    @AppCompatDelegate.NightMode
    fun getThemeInt(): Int {
        return when (dayNightModeString) {
            "day" -> AppCompatDelegate.MODE_NIGHT_NO
            "night" -> AppCompatDelegate.MODE_NIGHT_YES
            "auto" -> AppCompatDelegate.MODE_NIGHT_AUTO
            else -> AppCompatDelegate.MODE_NIGHT_AUTO
        }
    }

    fun isVersionSeen(newVersion: Int): Boolean {
        val isNew = newVersion > lastSeenSGFPackInt
        lastSeenSGFPackInt = newVersion
        return isNew
    }

}
