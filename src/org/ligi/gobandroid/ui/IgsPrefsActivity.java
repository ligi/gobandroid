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

import org.ligi.gobandroid.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.EditTextPreference;
import android.preference.Preference.OnPreferenceChangeListener;

public class IgsPrefsActivity extends PreferenceActivity implements OnPreferenceChangeListener {

	private EditTextPreference igs_user_name;
	private EditTextPreference igs_user_password;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPreferenceScreen(createPreferenceHierarchy());
    }
	
    private PreferenceScreen createPreferenceHierarchy() {
        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
        
        root.setPersistent(true);
        
        /* Gameplay section */
        PreferenceCategory inlinePrefCat = new PreferenceCategory(this);
        inlinePrefCat.setTitle("IGS Settings");
        root.addPreference(inlinePrefCat);
               	                
        igs_user_name = new EditTextPreference(this);
        igs_user_name.setTitle("IGS Username");
        igs_user_name.setDialogTitle("IGS Username");
        igs_user_name.setDialogMessage("Enter IGS Username");
        root.addPreference(igs_user_name);
        igs_user_name.setKey(IgsPrefs.KEY_IGS_USER_NAME);
        
        igs_user_name.setText(IgsPrefs.getIgsUserName());
        igs_user_name.setSummary(IgsPrefs.getIgsUserName());
        igs_user_name.setOnPreferenceChangeListener(this);

        igs_user_password = new EditTextPreference(this);
        igs_user_password.setTitle("IGS Password");
        igs_user_password.setDialogTitle("IGS Password");
        igs_user_password.setDialogMessage("Enter IGS Password");
        root.addPreference(igs_user_password);
        igs_user_password.setKey(IgsPrefs.KEY_IGS_USER_PASSWORD);
        
        igs_user_password.setText(IgsPrefs.getIgsUserPassword());
        igs_user_password.setSummary(IgsPrefs.getIgsUserPassword());
        igs_user_password.setOnPreferenceChangeListener(this);

        return root;
    }
    
    
    @Override
 	public boolean onPreferenceChange(Preference preference, Object newValue) {
	  	
 		if (preference==igs_user_name || preference==igs_user_password)
	  		preference.setSummary((String)newValue);
 		
	  	return true; // return that we are OK with preferences
	} 	
}
