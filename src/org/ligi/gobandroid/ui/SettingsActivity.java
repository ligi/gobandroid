package org.ligi.gobandroid.ui;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import org.ligi.gobandroid.R;

/**
 * Activity for a Game
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 *         This software is licenced with GPLv3
 * 
 **/

public class SettingsActivity extends Activity implements OnCheckedChangeListener, OnClickListener, OnItemSelectedListener {
	
	private CheckBox fat_finger_checkbox;
	
	private CheckBox fullscreen_checkbox;
	private CheckBox awake_checkbox;
	
	private Button sgf_path_btn;
	private Button sgf_file_btn;
	private Button skin_install_btn;
	
	private final static String DEFAULT_SGF_PATH="/sdcard/gobandroid";
	private final static String DEFAULT_SGF_FNAME="game";

	
	private Spinner skin_spinner;
	private String[] skin_strings;
	
	private SharedPreferences shared_prefs;
	
	
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		shared_prefs = this.getSharedPreferences("gobandroid", 0);
				//this.getD		
	}
	
	
	@Override
	public void onResume() {
		super.onResume();

		fat_finger_checkbox=(CheckBox)findViewById(R.id.FatFingerCheckBox);
		fat_finger_checkbox.setChecked(shared_prefs.getBoolean("fatfinger", false));
		fat_finger_checkbox.setOnCheckedChangeListener(this);
		
		
		awake_checkbox=(CheckBox)findViewById(R.id.ScreenAwakeCheckBox);
		awake_checkbox.setChecked(shared_prefs.getBoolean("awake", false));
		awake_checkbox.setOnCheckedChangeListener(this);
		
		fullscreen_checkbox=(CheckBox)findViewById(R.id.FullScreenCheckBox);
		fullscreen_checkbox.setChecked(shared_prefs.getBoolean("fullscreen", false));
		fullscreen_checkbox.setOnCheckedChangeListener(this);
		
		
		
		sgf_path_btn=(Button)findViewById(R.id.SGFPathButton);
		sgf_path_btn.setOnClickListener(this);
		sgf_file_btn=(Button)findViewById(R.id.SGFFilenameButton);
		sgf_file_btn.setOnClickListener(this);
		
		skin_install_btn=(Button)findViewById(R.id.GetSkinsButton);
		skin_install_btn.setOnClickListener(this);

		File f=new File("/sdcard/gobandroid/skins");
		int pos=0;
		
		File[] file_list;
		
		if (f.exists())
			file_list=f.listFiles();
		else 
			file_list=new File[0];
		
		skin_strings=new String[1+file_list.length];
		skin_strings[pos++]="no Skin";
		int selection_remember=0;
		
		for (File skin:file_list)
			{
			if (shared_prefs.getString("skinname", "").equals(skin.getName()))
				selection_remember=pos;
			skin_strings[pos++]=skin.getName();
			}
		
		ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(this,
	            android.R.layout.simple_spinner_item , skin_strings);

 		spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

 		skin_spinner=(Spinner)findViewById(R.id.SkinSpinner);
 		skin_spinner.setAdapter(spinner_adapter);
 		skin_spinner.setOnItemSelectedListener(this);
 		
 		skin_spinner.setSelection(selection_remember);
 		
 		refresh_labels();

	}
	
	public void refresh_labels() {
		
		TextView sgf_path_label=(TextView)	findViewById(R.id.SGFPathLabel);
		sgf_path_label.setText(shared_prefs.getString("sgf_path", DEFAULT_SGF_PATH));
		
		TextView sgf_fname_label=(TextView)	findViewById(R.id.SGFFNameLabel);
		sgf_fname_label.setText(shared_prefs.getString("sgf_fname", DEFAULT_SGF_FNAME));
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		SharedPreferences.Editor editor=shared_prefs.edit();
		
		if (buttonView==fat_finger_checkbox)
			editor.putBoolean("fatfinger", isChecked);
		
		else if (buttonView==fullscreen_checkbox)
			editor.putBoolean("fullscreen", isChecked);
		else if (buttonView==awake_checkbox)
			editor.putBoolean("awake", isChecked);
		
		editor.commit();
	}

	@Override
	public void onClick(View clicked_view) {
	
		final EditText input = new EditText(this);   
		
		if (clicked_view==sgf_path_btn)
		{
			input.setText(shared_prefs.getString("sgf_path", DEFAULT_SGF_PATH));
			
			new AlertDialog.Builder(this).setTitle("Set SGF Path").setMessage("Please Enter the default Path for SGF's").setView(input)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Log.i("gobandroid","change" + input.getText());
				SharedPreferences.Editor editor=shared_prefs.edit();	
				editor.putString("sgf_path", ""+input.getText()).commit();
				Log.i("gobandroid","change after" + shared_prefs.getString("sgf_path", "default"));
				refresh_labels();
			}	
			
			
			}).show();
		}
		else if (clicked_view==sgf_file_btn) {
			input.setText(shared_prefs.getString("sgf_fname", DEFAULT_SGF_FNAME));
			
			new AlertDialog.Builder(this).setTitle("Set SGF Filename").setMessage("Please Enter the default Filename fot SGF's!").setView(input)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				SharedPreferences.Editor editor=shared_prefs.edit();
				editor.putString("sgf_fname", ""+input.getText());
				editor.commit();		
				refresh_labels();
			}}).show();
		}
		else if (clicked_view==skin_install_btn)
		{
			//Uri uri = Uri.fromParts("package", "org.ligi.gobandroid.skinstaller.lironah", null);
			Intent intent = new Intent(Intent.ACTION_VIEW); 
			intent.setData(Uri.parse("market://search?q=org.ligi.gobandroid.skinstaller")); 
			startActivity(intent);
			
		}
		}


	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
	if (arg0==skin_spinner) {
		SharedPreferences.Editor editor=shared_prefs.edit();
		editor.putBoolean("skin",  (arg2!=0));
		Log.i("gobandroid","setting skin to " + (arg2!=0));
		editor.putString("skinname", skin_strings[arg2]);
		editor.commit();
	}
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}