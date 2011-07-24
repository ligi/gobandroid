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

public class IgsPrefs {

	private static SharedPreferences shared_prefs;
	
	public final static String KEY_IGS_USER_NAME="igs_user_name";
	public final static String KEY_IGS_USER_PASSWORD="igs_user_password";
	public final static String DEFAULT_IGS_USER_NAME="";
	public final static String DEFAULT_IGS_USER_PASSWORD="";

	public static void init(Context context) {
		shared_prefs=PreferenceManager.getDefaultSharedPreferences(context)	;
	}
	
	public static String getIgsUserName() {
		return shared_prefs.getString(KEY_IGS_USER_NAME,DEFAULT_IGS_USER_NAME );
	}	

	public static String getIgsUserPassword() {
		return shared_prefs.getString(KEY_IGS_USER_PASSWORD,DEFAULT_IGS_USER_PASSWORD );
	}	
}
