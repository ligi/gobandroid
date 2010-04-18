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

package org.ligi.gobandroid.ui;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class GoPrefs {

	private static SharedPreferences shared_prefs;
	
	public static String KEY_FATFINGER="fatfinger";
	public static String KEY_FULLSCREEN="fullscreen";
	public static String KEY_KEEPLIGHT="keeplight";
	public static String KEY_BOARD_SKIN="board_skina";
	public static String KEY_STONES_SKIN="stones_skina";
	
	public static String KEY_MARKLASTSTONE="mark_last_stone";
	
	public static String KEY_SGF_PATH="sgf_path";
	public static String KEY_SGF_FNAME="sgf_fname";
	
	public static String KEY_AI_LEVEL="ai_level";
	
	public static String KEY_LAST_BOARD_SIZE="last_board_size";
	public static String KEY_LAST_HANDICAP="last_handicap";
	public static String KEY_LAST_PLAYER_BLACK="last_player_black";
	public static String KEY_LAST_PLAYER_WHITE="last_player_white";
	
	
	public static String DEFAULT_AI_LEVEL="5";
	public static String DEFAULT_SKIN="no Skin";
	public static String DEFAULT_SGF_PATH="/sdcard/gobandroid/sgf";
	public static String DEFAULT_SGF_FNAME="game";
	
	
	public static void init(Context context) {
		shared_prefs=PreferenceManager.getDefaultSharedPreferences(context)	;
	}
	
	
	public static boolean getFatFingerEnabled() {
		return shared_prefs.getBoolean(KEY_FATFINGER, false);
	}
	
	public static boolean getFullscreenEnabled() {
		return shared_prefs.getBoolean(KEY_FULLSCREEN, false);
	}
	
	public static boolean getKeepLightEnabled() {
		return shared_prefs.getBoolean(KEY_KEEPLIGHT, false);
	}

	public static boolean getMarkLastStone() {
		return shared_prefs.getBoolean(KEY_MARKLASTSTONE, false);
	}

	public static String getBoardSkinName() {
		return shared_prefs.getString(KEY_BOARD_SKIN,DEFAULT_SKIN );
	}
	
	public static String getStoneSkinName() {
		return shared_prefs.getString(KEY_STONES_SKIN,DEFAULT_SKIN );
	}
	
	public static String getSGFPath() {
		return shared_prefs.getString(KEY_SGF_PATH,DEFAULT_SGF_PATH );
	}
	
	public static String getSGFFname() {
		return shared_prefs.getString(KEY_SGF_FNAME,DEFAULT_SGF_FNAME );
	}
	
	public static byte getAILevel() {
		String level_str=shared_prefs.getString(KEY_AI_LEVEL,DEFAULT_AI_LEVEL );
		try {
			return Byte.parseByte(level_str.substring(0, 2));
		} catch (Exception e) {
			return Byte.parseByte(level_str.substring(0, 1));
		}
	}
	
	public static String[] getAllSkinNames() {
		Log.i("gobandroid", "entering all skins");
		File f=new File(GOSkin.skin_base_path);
		int pos=0;
		
		File[] file_list;
		
		if (f.exists())
			file_list=f.listFiles();
		else 
			file_list=new File[0];
		
		String[] skin_strings=new String[1+file_list.length];
		skin_strings[pos++]="no Skin";
		//int selection_remember=0;
		
		for (File skin:file_list)
			{
			//if (shared_prefs.getString("skinname", "").equals(skin.getName()))
			//	selection_remember=pos;
			skin_strings[pos++]=skin.getName();
			}

		Log.i("gobandroid", "ending ");
		return skin_strings;

	}

	public static String[] getAllAILevelStrings() {
		return new String[] {"1 fast/weak","2","3","4","5 balance","6","7","8","9","10 slow/strong"};
	}

	public static String getAILevelString() {
		return  getAllAILevelStrings()[getAILevel()-1];
	}

}

