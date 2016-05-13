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

    var isAnnounceMoveActive: Boolean by booleanPrefVar()
    var isShowForwardAlertWanted: Boolean by booleanPrefVar(default = true, key = "show_var_alert_win")
    val isSoundWanted: Boolean by booleanPrefVar(default = true, key = R.string.prefs_do_sound)

    var isAskVariantWanted: Boolean by booleanPrefVar(default = true)
    var isKeepVariantWanted: Boolean by booleanPrefVar(default = true)

    var lastBoardSize: Int by intPrefVar(default = 9)
    var lastHandicap: Int by intPrefVar(default = 0)

    var username: String by stringPrefVar()
    var rank: String by stringPrefVar()

    val isFullscreenEnabled: Boolean by booleanPrefVar(key = R.string.prefs_fullscreen)
    val isLegendEnabled: Boolean by booleanPrefVar(key = R.string.prefs_do_legend)
    val isSGFLegendEnabled: Boolean by booleanPrefVar(key = R.string.prefs_sgf_legend)
    val isConstantLightWanted: Boolean by booleanPrefVar(key = R.string.prefs_constant_light)
    val isGridEmbossEnabled: Boolean by booleanPrefVar(key = R.string.prefs_grid_emboss)
    val isTsumegoPushEnabled: Boolean by booleanPrefVar(key = R.string.prefs_push_tsumego)

    private val dayNightModeString: String by stringPrefVar(key = R.string.prefs_daynight)
    private var lastSeenSGFPackInt: Int by intPrefVar()

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
