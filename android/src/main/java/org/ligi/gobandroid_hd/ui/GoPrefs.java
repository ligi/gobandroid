/**
 * gobandroid
 * by Marcus -Ligi- Bueschleb
 * http://ligi.de
 * <p/>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 **/

package org.ligi.gobandroid_hd.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

/**
 * Class to store and access Preferences used in Gobandroid
 *
 * @author <a href="http://ligi.de">Marcus -LiGi- Bueschleb </a>
 *         <p/>
 *         This software is licenced with GPLv3
 */

public class GoPrefs {

    private static SharedPreferences shared_prefs;


    public final static String KEY_ANNOUNCE_MOVE = "announce move";

    public final static String KEY_VARIANT_MODE = "variant_mode";

    public final static String KEY_SGF_PATH = "sgf_path";

    public final static String KEY_LAST_BOARD_SIZE = "last_board_size";
    public final static String KEY_LAST_HANDICAP = "last_handicap";

    public final static String DEFAULT_SGF_PATH = Environment.getExternalStorageDirectory().getPath() + "/gobandroid/sgf";

    public final static int DEFAULT_LAST_BOARD_SIZE = 9;
    public final static int DEFAULT_LAST_HANDICAP = 0;

    public final static String SHOW_ALERT_WIN = "show_var_alert_win";

    public static void init(Context context) {
        shared_prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isAnnounceMoveActive() {
        return shared_prefs.getBoolean(KEY_ANNOUNCE_MOVE, true);
    }

    public static void setAnnounceMoveActive(boolean announce) {
        shared_prefs.edit().putBoolean(KEY_ANNOUNCE_MOVE, announce).apply();
    }

    public static void setLastBoardSize(int size) {
        shared_prefs.edit().putInt(KEY_LAST_BOARD_SIZE, size).apply();
    }

    public static int getLastBoardSize() {
        return shared_prefs.getInt(KEY_LAST_BOARD_SIZE, DEFAULT_LAST_BOARD_SIZE);
    }

    public static void setLastHandicap(int size) {
        shared_prefs.edit().putInt(KEY_LAST_HANDICAP, size).apply();
    }

    public static int getLastHandicap() {
        return shared_prefs.getInt(KEY_LAST_HANDICAP, DEFAULT_LAST_HANDICAP);
    }

    public static boolean isAskVariantEnabled() {
        return shared_prefs.getString(KEY_VARIANT_MODE, "ask").equals("ask");
    }

    public static boolean isKeepVariantEnabled() {
        return shared_prefs.getString(KEY_VARIANT_MODE, "ask").equals("keep");
    }

    public static String getSGFPath() {
        return shared_prefs.getString(KEY_SGF_PATH, DEFAULT_SGF_PATH);
    }

    public static boolean isShowForwardAlertWanted() {
        return shared_prefs.getBoolean(SHOW_ALERT_WIN, true);
    }
    public static void setShowForwardAlert(boolean show) {
        shared_prefs.edit().putBoolean(SHOW_ALERT_WIN, show).apply();
    }

}
