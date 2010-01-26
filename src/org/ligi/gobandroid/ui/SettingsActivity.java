package org.ligi.gobandroid.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
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

public class SettingsActivity extends Activity implements OnCheckedChangeListener, OnClickListener {
	
	private CheckBox fat_finger_checkbox;
	private CheckBox skin_checkbox;
	
	private Button sgf_path_btn;
	private Button sgf_file_btn;
	
	private final static String DEFAULT_SGF_PATH="/sdcard/gobandroid";
	private final static String DEFAULT_SGF_FNAME="game";

	private SharedPreferences shared_prefs;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		shared_prefs = this.getSharedPreferences("gobandroid", 0);
				
		fat_finger_checkbox=(CheckBox)findViewById(R.id.FatFingerCheckBox);
		fat_finger_checkbox.setChecked(shared_prefs.getBoolean("fatfinger", false));
		fat_finger_checkbox.setOnCheckedChangeListener(this);
		
		skin_checkbox=(CheckBox)findViewById(R.id.SkinCheckBox);
		skin_checkbox.setChecked(shared_prefs.getBoolean("skin", false));
		skin_checkbox.setOnCheckedChangeListener(this);
		
		sgf_path_btn=(Button)findViewById(R.id.SGFPathButton);
		sgf_path_btn.setOnClickListener(this);
		sgf_file_btn=(Button)findViewById(R.id.SGFFilenameButton);
		sgf_file_btn.setOnClickListener(this);
		
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
		else
			if (buttonView==skin_checkbox)	
				editor.putBoolean("skin", isChecked);
		editor.commit();
	}

	@Override
	public void onClick(View arg0) {
		

		Log.i("gobandroid","click"+arg0);
		final EditText input = new EditText(this);   
		
		if (arg0==sgf_path_btn)
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
		else if (arg0==sgf_file_btn) {
			input.setText(shared_prefs.getString("sgf_fname", DEFAULT_SGF_FNAME));
			
			new AlertDialog.Builder(this).setTitle("Set SGF Filename").setMessage("Please Enter the default Filename fot SGF's!").setView(input)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				SharedPreferences.Editor editor=shared_prefs.edit();
				editor.putString("sgf_fname", ""+input.getText());
				editor.commit();		
				refresh_labels();
			}	
			
			}).show();
		}
		
		
		
		
		
		}

}