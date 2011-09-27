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

import org.ligi.gobandroid_hd.R;

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
	private CheckBoxPreference SGFLegendCheckBoxPref;
	private CheckBoxPreference doLegendCheckBoxPref;
	
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
        inlinePrefCat.setTitle(R.string.gameplay);
        root.addPreference(inlinePrefCat);
        
        
        CheckBoxPreference nextScreenCheckBoxPref = new CheckBoxPreference(this);
        nextScreenCheckBoxPref.setKey(GoPrefs.KEY_FATFINGER);
        nextScreenCheckBoxPref.setTitle(R.string.first_tap_zooms);
        nextScreenCheckBoxPref.setSummary(R.string.first_tap_zooms_summary);
        inlinePrefCat.addPreference(nextScreenCheckBoxPref);

        nextScreenCheckBoxPref = new CheckBoxPreference(this);
        nextScreenCheckBoxPref.setKey(GoPrefs.KEY_VIEWABLESTONE);
        nextScreenCheckBoxPref.setTitle(R.string.viewable_stone);
        nextScreenCheckBoxPref.setSummary(R.string.viewable_stone_sum);
        inlinePrefCat.addPreference(nextScreenCheckBoxPref);

        CheckBoxPreference markLastStoneCheckBoxPref = new CheckBoxPreference(this);
        markLastStoneCheckBoxPref.setKey(GoPrefs.KEY_MARKLASTSTONE);
        markLastStoneCheckBoxPref.setTitle(R.string.mark_last_stone);
        markLastStoneCheckBoxPref.setSummary(R.string.mark_last_stone_summary);
        inlinePrefCat.addPreference(markLastStoneCheckBoxPref);
        
        /* Skin section */
        PreferenceCategory skinPrefCat = new PreferenceCategory(this);
        skinPrefCat.setTitle(R.string.skin);
        root.addPreference( skinPrefCat);
        
        PreferenceScreen intentPref = getPreferenceManager().createPreferenceScreen(this);
        Intent i=new Intent().setAction(Intent.ACTION_VIEW)
        .setData(Uri.parse("market://search?q=skinstaller"));
        intentPref.setIntent(i);
        intentPref.setTitle(R.string.install_skins);
        intentPref.setSummary(R.string.get_skins_from_market);
        skinPrefCat.addPreference(intentPref);

        boardSkinPref = new ListPreference(this);
        boardSkinPref.setEntries(GoPrefs.getAllSkinNames());
        boardSkinPref.setEntryValues(GoPrefs.getAllSkinNames());
        boardSkinPref.setDialogTitle(R.string.set_skin);
        boardSkinPref.setKey(GoPrefs.KEY_BOARD_SKIN);
        boardSkinPref.setTitle(R.string.board_skin);
        boardSkinPref.setSummary(GoPrefs.getBoardSkinName());
        boardSkinPref.setOnPreferenceChangeListener(this);
        boardSkinPref.setDefaultValue(GoPrefs.getBoardSkinName());
        skinPrefCat.addPreference(boardSkinPref);

        
        stoneSkinPref = new ListPreference(this);
        stoneSkinPref.setEntries(GoPrefs.getAllSkinNames());
        stoneSkinPref.setEntryValues(GoPrefs.getAllSkinNames());
        stoneSkinPref.setDialogTitle(R.string.set_skin);
        stoneSkinPref.setKey(GoPrefs.KEY_STONES_SKIN);
        stoneSkinPref.setTitle(R.string.stone_skin);
        stoneSkinPref.setSummary(GoPrefs.getStoneSkinName());
        stoneSkinPref.setOnPreferenceChangeListener(this);
        stoneSkinPref.setDefaultValue(GoPrefs.getStoneSkinName());
        skinPrefCat.addPreference(stoneSkinPref);

        /* UI section */
        PreferenceCategory uiPrefCat = new PreferenceCategory(this);
        uiPrefCat.setTitle(R.string.screen);
        root.addPreference( uiPrefCat);

        CheckBoxPreference fullscreenCheckBoxPref = new CheckBoxPreference(this);
        fullscreenCheckBoxPref.setKey(GoPrefs.KEY_FULLSCREEN);
        fullscreenCheckBoxPref.setTitle(R.string.fullscreen_board);
        fullscreenCheckBoxPref.setSummary(R.string.fullscreen_board_summary);
       	uiPrefCat.addPreference(fullscreenCheckBoxPref);


        CheckBoxPreference keepScreenAwakeCheckBoxPref = new CheckBoxPreference(this);
        keepScreenAwakeCheckBoxPref.setKey(GoPrefs.KEY_KEEPLIGHT);
        keepScreenAwakeCheckBoxPref.setTitle(R.string.constant_light);
        keepScreenAwakeCheckBoxPref.setSummary(R.string.drain_your_battery_while_playing);
       	uiPrefCat.addPreference(keepScreenAwakeCheckBoxPref);

        CheckBoxPreference doEmbossCheckBoxPref = new CheckBoxPreference(this);
        doEmbossCheckBoxPref.setKey(GoPrefs.KEY_GRID_EMBOSS);
        doEmbossCheckBoxPref.setTitle(R.string.grid_emboss);
        doEmbossCheckBoxPref.setDefaultValue(GoPrefs.getGridEmbossEnabled());
        doEmbossCheckBoxPref.setSummary(R.string.grid_emboss_summary);
       	uiPrefCat.addPreference(doEmbossCheckBoxPref);

        doLegendCheckBoxPref = new CheckBoxPreference(this);
        doLegendCheckBoxPref.setKey(GoPrefs.KEY_DO_LEGEND);
        doLegendCheckBoxPref.setTitle(R.string.show_legend);
        doLegendCheckBoxPref.setSummary(R.string.show_legend_summary);
        doLegendCheckBoxPref.setDefaultValue(GoPrefs.getLegendEnabled());
        doLegendCheckBoxPref.setOnPreferenceChangeListener(this);
       	uiPrefCat.addPreference(doLegendCheckBoxPref);

        SGFLegendCheckBoxPref = new CheckBoxPreference(this);
        SGFLegendCheckBoxPref.setKey(GoPrefs.KEY_SGF_LEGEND);
        SGFLegendCheckBoxPref.setTitle(R.string.sgf_legend);
        SGFLegendCheckBoxPref.setSummary(R.string.sgf_legend_summary);
        SGFLegendCheckBoxPref.setEnabled(GoPrefs.getLegendEnabled());
        SGFLegendCheckBoxPref.setDefaultValue(GoPrefs.getLegendSGFMode());
        
       	uiPrefCat.addPreference(SGFLegendCheckBoxPref);
       	
        /* SGF section */
        PreferenceCategory sgfPrefCat = new PreferenceCategory(this);
        sgfPrefCat.setTitle(R.string.sgf_preferences);
        root.addPreference( sgfPrefCat);


        String[] variant_option_labels=new String[] {"ask","keep","discard"};
        String[] variant_options=new String[] {"ask","keep","discard"};
        
        ListPreference variantHandlePreference = new ListPreference(this);
        variantHandlePreference.setEntries(variant_option_labels);
        variantHandlePreference.setEntryValues(variant_options);
        
        variantHandlePreference.setDialogTitle(R.string.variant_mode);
        variantHandlePreference.setKey(GoPrefs.KEY_VARIANT_MODE);
        variantHandlePreference.setTitle(R.string.variant_mode);
        variantHandlePreference.setSummary(R.string.variant_mode_summary);
        variantHandlePreference.setOnPreferenceChangeListener(this);
        variantHandlePreference.setDefaultValue("ask");
        sgfPrefCat.addPreference(variantHandlePreference);
                
        sgf_path_pref = new EditTextPreference(this);
        sgf_path_pref.setTitle(R.string.path);
        sgf_path_pref.setDialogTitle(R.string.sgf_path);
        sgf_path_pref.setDialogMessage(R.string.enter_sgf_path_request);
        sgfPrefCat.addPreference(sgf_path_pref);
        sgf_path_pref.setKey(GoPrefs.KEY_SGF_PATH);
        
        sgf_path_pref.setText(GoPrefs.getSGFPath());
        sgf_path_pref.setSummary(GoPrefs.getSGFPath());
        sgf_path_pref.setOnPreferenceChangeListener(this);

        sgf_fname_pref = new EditTextPreference(this);
        sgf_fname_pref.setTitle(R.string.filename);
        sgfPrefCat.addPreference(sgf_fname_pref);
        sgf_fname_pref.setDialogTitle(R.string.sgf_filename);
        sgf_fname_pref.setDialogMessage(R.string.enter_default_filename_for_sgf_request);
        sgf_fname_pref.setKey(GoPrefs.KEY_SGF_FNAME);
        //sgf_fname_pref.setDefaultValue(GoPrefs.DEFAULT_SGF_FNAME);        
        sgf_fname_pref.setText(GoPrefs.getSGFFname());
        sgf_fname_pref.setSummary(GoPrefs.getSGFFname());
        sgf_fname_pref.setOnPreferenceChangeListener(this);

        PreferenceCategory aiPrefCat = new PreferenceCategory(this);
        aiPrefCat.setTitle(R.string.ai_preferences);
        root.addPreference( aiPrefCat);
        
        aiLevelPref = new ListPreference(this);
        aiLevelPref.setEntries(GoPrefs.getAllAILevelStrings());
        aiLevelPref.setEntryValues(GoPrefs.getAllAILevelStrings());
        aiLevelPref.setDialogTitle(R.string.set_ai_strength);
        aiLevelPref.setKey(GoPrefs.KEY_AI_LEVEL);
        aiLevelPref.setTitle(R.string.ai_strength);
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
 		
 		if (preference==doLegendCheckBoxPref)
 			SGFLegendCheckBoxPref.setEnabled((Boolean)newValue);
 		
	  	return true; // return that we are OK with preferences
	} 	
}
