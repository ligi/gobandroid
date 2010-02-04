package org.ligi.gobandroid.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.EditText;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ligi.gobandroid.logic.GoGame;
import org.ligi.gobandroid.logic.SGFHelper;

/**
 * Activity for a Game
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 * Licence: This software is licenced with GPLv3
 * 
 **/

public class GoActivity extends Activity {
	private static final int MENU_UNDO = 0;
	private static final int MENU_PASS = 1;
	//private static final int MENU_FINISH = 2;
	private static final int MENU_WRITE_SGF = 3;
	private static final int MENU_SETTINGS = 4;

	private GoGame game=null;
	private GoBoardView board_view;

	private SharedPreferences shared_prefs;
	
	private WakeLock mWakeLock=null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		shared_prefs=this.getSharedPreferences("gobandroid", 0);
		Log.i("gobandroid","onCreate" + game);
		
		if (game==null) {
			
			
			// if there is a game saved e.g. on rotation use this game
			if (getLastNonConfigurationInstance()!=null) 
				game=(GoGame)getLastNonConfigurationInstance();
			else {
				// otherwise create a new game
				byte size = getIntent().getByteExtra("size", (byte) 9);
				byte handicap = getIntent().getByteExtra("handicap", (byte) 0);
				game = new GoGame(size,handicap);
			}
		
			board_view = new GoBoardView(this, game);
			board_view.setOnTouchListener((OnTouchListener) board_view);
			setContentView(board_view); 
			}
		
		
		if (shared_prefs.getBoolean("fullscreen", false))
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		else
			this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	
		if (shared_prefs.getBoolean("awake", false))
			{
			final PowerManager pm = (PowerManager) (this.getSystemService(Context.POWER_SERVICE)); 
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "DUBwise Wakelog TAG");  
        	mWakeLock.acquire();
			}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// remember the game in case of e.g. a device rotation
		return(game);
	}
	
	

	@Override
	public void onResume() {
		super.onResume();
		
		board_view.do_skin=shared_prefs.getBoolean("skin", false);
		board_view.do_zoom=shared_prefs.getBoolean("fatfinger", false);
		
	}

	/* Creates the menu items */
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		if (!game.isFinished()) {
			if (game.canUndo()) {
				MenuItem undo_menu = menu.add(0, MENU_UNDO, 0, "Undo");
				undo_menu.setIcon(android.R.drawable.ic_menu_revert);
			}
			MenuItem pass_menu = menu.add(0, MENU_PASS, 0, "Pass");
			pass_menu.setIcon(android.R.drawable.ic_menu_set_as);
		} else {
			/*MenuItem finish_menu = menu.add(0, MENU_FINISH, 0,
			"Finished Marking dead Stones");
			finish_menu.setIcon(android.R.drawable.ic_menu_set_as);
			 */
		}
		
		MenuItem save_menu = menu.add(0, MENU_WRITE_SGF, 0,"Save as SGF");
		save_menu.setIcon(android.R.drawable.ic_menu_save);
		/*
		MenuItem settings_menu = menu.add(0, MENU_SETTINGS, 0,"Settings");
		settings_menu.setIcon(android.R.drawable.ic_menu_preferences);
		
*/
		return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case MENU_UNDO:
			game.undo();
			break;

		case MENU_PASS:
			game.pass();
			break;

		case MENU_WRITE_SGF:
			
			
			SharedPreferences shared_prefs = this.getSharedPreferences("gobandroid", 0);
			
			final String sgf_fname=shared_prefs.getString("sgf_fname", "game");
			final String sgf_path=shared_prefs.getString("sgf_path", "/sdcard/gobandroid");
			
			
			final EditText input = new EditText(this);   
			input.setText(sgf_fname);

			new AlertDialog.Builder(this).setTitle("Save SGF").setMessage("How should the file I will write to " +sgf_path + " be named?").setView(input)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString(); 
					
				//value.split("\/")
				File f = new File(sgf_path);
				f.mkdirs();
				
				try {
					f=new File(sgf_path + "/"+value+".sgf");
					f.createNewFile();
					
					FileWriter gpxwriter = new FileWriter(f);
					BufferedWriter out = new BufferedWriter(gpxwriter);

					out.write(SGFHelper.game2sgf(game));

					out.close();
					gpxwriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
			}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			// Do nothing.
			}
			}).show();
						break;

		case MENU_SETTINGS:
			 
             
             startActivity(new Intent(this,SettingsActivity.class));
             break;
		}
		board_view.invalidate();
		return false;
	}

	
    
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	
    	switch (keyCode) {
    	case KeyEvent.KEYCODE_DPAD_UP:
    		board_view.prepare_keyinput();
    		if (board_view.touch_y>0) board_view.touch_y--;
    		break;
    	
    	case KeyEvent.KEYCODE_DPAD_LEFT:
    		board_view.prepare_keyinput();
    		if (board_view.touch_x>0) board_view.touch_x--;
    		break;
    	
    	case KeyEvent.KEYCODE_DPAD_DOWN:
    		board_view.prepare_keyinput();
    		if (board_view.touch_y<game.getVisualBoard().getSize()-1) board_view.touch_y++;
    		break;
    	
    	case KeyEvent.KEYCODE_DPAD_RIGHT:
    		board_view.prepare_keyinput();
    		if (board_view.touch_x<game.getVisualBoard().getSize()-1) board_view.touch_x++;
    		break;
    		
    	case KeyEvent.KEYCODE_DPAD_CENTER:
    		game.do_move(board_view.touch_x,board_view.touch_y);
    		board_view.setZoom(false);
    		break;
    		
    	case KeyEvent.KEYCODE_BACK:
    		if (board_view.isZoomed())
    			{ 
    			Log.i("gobandroid","unzoom");
    			board_view.setZoom(false);
    			return true;
    			}
    		break;
    	}
    	board_view.invalidate();
    	return super.onKeyDown(keyCode, event);	
    }
	
    @Override 
    public void onDestroy() {
    	super.onDestroy();
    	if (mWakeLock!=null)
    		mWakeLock.release();
    }
}