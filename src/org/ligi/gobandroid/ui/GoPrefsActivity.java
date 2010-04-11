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

public class GoPrefsActivity extends PreferenceActivity implements OnPreferenceChangeListener {

	private ListPreference boardSkinPref;
	private ListPreference stoneSkinPref;
	private EditTextPreference sgf_path_pref;
	private EditTextPreference sgf_fname_pref;
	private ListPreference aiLevelPref;
	
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
        inlinePrefCat.setTitle("Gameplay");
        root.addPreference(inlinePrefCat);
        
        
        CheckBoxPreference nextScreenCheckBoxPref = new CheckBoxPreference(this);
        nextScreenCheckBoxPref.setKey(GoPrefs.KEY_FATFINGER);
        nextScreenCheckBoxPref.setTitle("first tap zooms");
        nextScreenCheckBoxPref.setSummary("aka fat finger mode");
        inlinePrefCat.addPreference(nextScreenCheckBoxPref);

        CheckBoxPreference markLastStoneCheckBoxPref = new CheckBoxPreference(this);
        markLastStoneCheckBoxPref.setKey(GoPrefs.KEY_MARKLASTSTONE);
        markLastStoneCheckBoxPref.setTitle("mark last stone");
        markLastStoneCheckBoxPref.setSummary("little circle showing last stone");
        inlinePrefCat.addPreference(markLastStoneCheckBoxPref);

        
        /* Skin section */
        PreferenceCategory skinPrefCat = new PreferenceCategory(this);
        skinPrefCat.setTitle("Skin");
        root.addPreference( skinPrefCat);
        
        PreferenceScreen intentPref = getPreferenceManager().createPreferenceScreen(this);
        Intent i=new Intent().setAction(Intent.ACTION_VIEW)
        .setData(Uri.parse("market://search?q=org.ligi.gobandroid.skinstaller"));
        intentPref.setIntent(i);
        intentPref.setTitle("Install Skins");
        intentPref.setSummary("get skins from the market");
        skinPrefCat.addPreference(intentPref);

        boardSkinPref = new ListPreference(this);
        boardSkinPref.setEntries(GoPrefs.getAllSkinNames());
        boardSkinPref.setEntryValues(GoPrefs.getAllSkinNames());
        boardSkinPref.setDialogTitle("Set Skin");
        boardSkinPref.setKey(GoPrefs.KEY_BOARD_SKIN);
        boardSkinPref.setTitle("Board Skin");
        boardSkinPref.setSummary(GoPrefs.getBoardSkinName());
        boardSkinPref.setOnPreferenceChangeListener(this);
        boardSkinPref.setDefaultValue(GoPrefs.getBoardSkinName());
        skinPrefCat.addPreference(boardSkinPref);

        
        stoneSkinPref = new ListPreference(this);
        stoneSkinPref.setEntries(GoPrefs.getAllSkinNames());
        stoneSkinPref.setEntryValues(GoPrefs.getAllSkinNames());
        stoneSkinPref.setDialogTitle("Set Skin");
        stoneSkinPref.setKey(GoPrefs.KEY_STONES_SKIN);
        stoneSkinPref.setTitle("Stone Skin");
        stoneSkinPref.setSummary(GoPrefs.getStoneSkinName());
        stoneSkinPref.setOnPreferenceChangeListener(this);
        stoneSkinPref.setDefaultValue(GoPrefs.getStoneSkinName());
        skinPrefCat.addPreference(stoneSkinPref);

        /* UI section */
        PreferenceCategory uiPrefCat = new PreferenceCategory(this);
        uiPrefCat.setTitle("Screen");
        root.addPreference( uiPrefCat);

        CheckBoxPreference fullscreenCheckBoxPref = new CheckBoxPreference(this);
        fullscreenCheckBoxPref.setKey(GoPrefs.KEY_FULLSCREEN);
        fullscreenCheckBoxPref.setTitle("Fullscreen Board");
        fullscreenCheckBoxPref.setSummary("see some more Board");
       	uiPrefCat.addPreference(fullscreenCheckBoxPref);


        CheckBoxPreference keepScreenAwakeCheckBoxPref = new CheckBoxPreference(this);
        keepScreenAwakeCheckBoxPref.setKey(GoPrefs.KEY_KEEPLIGHT);
        keepScreenAwakeCheckBoxPref.setTitle("Constant Light");
        keepScreenAwakeCheckBoxPref.setSummary("drain your Battery while playing");
       	uiPrefCat.addPreference(keepScreenAwakeCheckBoxPref);

        /* SGF section */
        PreferenceCategory sgfPrefCat = new PreferenceCategory(this);
        sgfPrefCat.setTitle("SGF Preferences");
        root.addPreference( sgfPrefCat);
       
        sgf_path_pref = new EditTextPreference(this);
        sgf_path_pref.setTitle("Path");
        sgf_path_pref.setDialogTitle("SGF Path");
        sgf_path_pref.setDialogMessage("Please enter the Path where the SGF Files should be saved.");
        sgfPrefCat.addPreference(sgf_path_pref);
        sgf_path_pref.setKey(GoPrefs.KEY_SGF_PATH);
        
        sgf_path_pref.setText(GoPrefs.getSGFPath());
        sgf_path_pref.setSummary(GoPrefs.getSGFPath());
        sgf_path_pref.setOnPreferenceChangeListener(this);

        sgf_fname_pref = new EditTextPreference(this);
        sgf_fname_pref.setTitle("Filename");
        sgfPrefCat.addPreference(sgf_fname_pref);
        sgf_fname_pref.setDialogTitle("SGF Filename");
        sgf_fname_pref.setDialogMessage("Please enter the default Filename for SGF Files.");
        sgf_fname_pref.setKey(GoPrefs.KEY_SGF_FNAME);
        //sgf_fname_pref.setDefaultValue(GoPrefs.DEFAULT_SGF_FNAME);        
        sgf_fname_pref.setText(GoPrefs.getSGFFname());
        sgf_fname_pref.setSummary(GoPrefs.getSGFFname());
        sgf_fname_pref.setOnPreferenceChangeListener(this);

        PreferenceCategory aiPrefCat = new PreferenceCategory(this);
        aiPrefCat.setTitle("A.I. Preferences");
        root.addPreference( aiPrefCat);

        
        aiLevelPref = new ListPreference(this);
        aiLevelPref.setEntries(GoPrefs.getAllAILevelStrings());
        aiLevelPref.setEntryValues(GoPrefs.getAllAILevelStrings());
        aiLevelPref.setDialogTitle("Set A.I. Strength");
        aiLevelPref.setKey(GoPrefs.KEY_AI_LEVEL);
        aiLevelPref.setTitle("A.I. strength");
        aiLevelPref.setSummary(GoPrefs.getAILevelString());
        aiLevelPref.setOnPreferenceChangeListener(this);
        aiLevelPref.setDefaultValue(GoPrefs.DEFAULT_AI_LEVEL);
        aiPrefCat.addPreference(aiLevelPref);
        
        return root;
    }
    
    
    @Override
 	public boolean onPreferenceChange(Preference preference, Object newValue) {
	  	
 		if ((preference==sgf_path_pref)||(preference==sgf_fname_pref)
 				||(preference==boardSkinPref)|| (preference==stoneSkinPref)|| (preference==aiLevelPref))
	  		preference.setSummary((String)newValue);
 		  	
	  	return true; // return that we are OK with preferences
	} 	
}
