/**
 * gobandroid 
 * by Marcus -Ligi- Bueschleb 
 * http://ligi.de
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as 
 * published by the Free Software Foundation; 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. 
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
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

    public final static String KEY_FATFINGER = "fatfinger";
    public final static String KEY_VIEWABLESTONE = "viewablestone";
    public final static String KEY_VIEW_STONE_DISTANCE = "view_stone_distance";

    public final static String KEY_KEEPLIGHT = "keeplight";
    public final static String KEY_BOARD_SKIN = "board_skina";
    public final static String KEY_STONES_SKIN = "stones_skina";

    public final static String KEY_ANNOUNCE_MOVE = "announce move";

    public final static String KEY_VARIANT_MODE = "variant_mode";

    public final static String KEY_DO_LEGEND = "do_legend";
    public final static String KEY_SGF_LEGEND = "sgf legend";

    public final static String KEY_MARKLASTSTONE = "mark_last_stone";

    public final static String KEY_SGF_PATH = "sgf_path";
    public final static String KEY_SGF_FNAME = "sgf_fname";

    public final static String KEY_AI_LEVEL = "ai_level";

    public final static String KEY_LAST_BOARD_SIZE = "last_board_size";
    public final static String KEY_LAST_HANDICAP = "last_handicap";
    public final static String KEY_LAST_PLAYER_BLACK = "last_player_black";
    public final static String KEY_LAST_PLAYER_WHITE = "last_player_white";

    public final static String KEY_GRID_EMBOSS = "grid_emboss";

    public final static String DEFAULT_VIEWABLE_DISTANCE = "1";

    public final static String DEFAULT_AI_LEVEL = "5";
    public final static String DEFAULT_SKIN = "no Skin";
    public final static String DEFAULT_SGF_PATH = Environment.getExternalStorageDirectory().getPath() + "/gobandroid/sgf";
    public final static String DEFAULT_SGF_FNAME = "game";

    public final static int DEFAULT_LAST_BOARD_SIZE = 9;
    public final static int DEFAULT_LAST_HANDICAP = 0;

    public final static String DEFAULT_LAST_PLAYER_BLACK = "last_player_black";
    public final static String DEFAULT_LAST_PLAYER_WHITE = "last_player_white";

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

    public static String getLastPlayerBlack() {
        return shared_prefs.getString(KEY_LAST_PLAYER_BLACK, DEFAULT_LAST_PLAYER_BLACK);
    }

    public static String getLastPlayerWhite() {
        return shared_prefs.getString(KEY_LAST_PLAYER_WHITE, DEFAULT_LAST_PLAYER_WHITE);
    }

    public static void setLastPlayerWhite(String last_player) {
        shared_prefs.edit().putString(KEY_LAST_PLAYER_WHITE, last_player).apply();
    }

    public static void setLastPlayerBlack(String last_player) {
        shared_prefs.edit().putString(KEY_LAST_PLAYER_BLACK, last_player).apply();
    }

    public static boolean getGridEmbossEnabled() {
        return shared_prefs.getBoolean(KEY_GRID_EMBOSS, true);
    }

    public static boolean isAskVariantEnabled() {
        return shared_prefs.getString(KEY_VARIANT_MODE, "ask").equals("ask");
    }

    public static boolean isKeepVariantEnabled() {
        return shared_prefs.getString(KEY_VARIANT_MODE, "ask").equals("keep");
    }

    public static boolean getFatFingerEnabled() {
        //return shared_prefs.getBoolean(KEY_FATFINGER, false);
        return true;
    }

    public static boolean getViewableStoneEnabled() {
        return shared_prefs.getBoolean(KEY_VIEWABLESTONE, false);
    }

    public static String[] getViewableDistanceStrings() {
        return new String[]{"1", "2", "3", "4", "5"};
    }

    public static byte getViewableDistance() {
        String viewdist_str = shared_prefs.getString(KEY_VIEW_STONE_DISTANCE, DEFAULT_VIEWABLE_DISTANCE);
        return Byte.parseByte(viewdist_str);
    }

    public static boolean getKeepLightEnabled() {
        return shared_prefs.getBoolean(KEY_KEEPLIGHT, false);
    }

    public static boolean getMarkLastStone() {
        return shared_prefs.getBoolean(KEY_MARKLASTSTONE, false);
    }

    public static boolean getLegendEnabled() {
        return shared_prefs.getBoolean(KEY_DO_LEGEND, false);
    }

    public static boolean getLegendSGFMode() {
        return shared_prefs.getBoolean(KEY_SGF_LEGEND, false);
    }

    public static String getBoardSkinName() {
        return shared_prefs.getString(KEY_BOARD_SKIN, DEFAULT_SKIN);
    }

    public static String getStoneSkinName() {
        return shared_prefs.getString(KEY_STONES_SKIN, DEFAULT_SKIN);
    }

    public static String getSGFPath() {
        return shared_prefs.getString(KEY_SGF_PATH, DEFAULT_SGF_PATH);
    }

    public static String getSGFFname() {
        return shared_prefs.getString(KEY_SGF_FNAME, DEFAULT_SGF_FNAME);
    }

    public static byte getAILevel() {
        String level_str = shared_prefs.getString(KEY_AI_LEVEL, DEFAULT_AI_LEVEL);
        try {
            return Byte.parseByte(level_str.substring(0, 2));
        } catch (Exception e) {
            return Byte.parseByte(level_str.substring(0, 1));
        }
    }

    public static String[] getAllAILevelStrings() {
        return new String[]{"1 fast/weak", "2", "3", "4", "5 balance", "6", "7", "8", "9", "10 slow/strong"};
    }

    public static String getAILevelString() {
        return getAllAILevelStrings()[getAILevel() - 1];
    }
}
