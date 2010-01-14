package org.ligi.gobandroid.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ligi.gobandroid.logic.GoGame;

/**
 * Activity for a Game
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 *         This software is licenced with GPLv3
 * 
 **/

public class GoActivity extends Activity {
	private static final int MENU_UNDO = 0;
	private static final int MENU_PASS = 1;
	private static final int MENU_FINISH = 2;
	private static final int MENU_WRITE_SGF = 3;
	private static final int MENU_SETTINGS = 4;

	GoGame game;
	GoBoardView board_view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		byte size = getIntent().getByteExtra("size", (byte) 9);
		game = new GoGame(size);
		board_view = new GoBoardView(this, game);
		board_view.setOnTouchListener((OnTouchListener) board_view);
		setContentView(board_view);
	}

	int i = 0;

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
			MenuItem finish_menu = menu.add(0, MENU_FINISH, 0,
			"Finished Marking dead Stones");
			finish_menu.setIcon(android.R.drawable.ic_menu_set_as);

		}
		/*
		MenuItem save_menu = menu.add(0, MENU_WRITE_SGF, 0,"Save as SGF");
		save_menu.setIcon(android.R.drawable.ic_menu_save);
		
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
			
			final EditText input = new EditText(this);   
			input.setText("/sdcard/gobandroid/game0.sgf");
			new AlertDialog.Builder(this).setTitle("Save SGF").setMessage("Where should I save it?").setView(input)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString(); 
					
				//value.split("\/")
				File f = new File("/sdcard/gobandroid/");
				f.mkdirs();
				
				try {
					f=new File("/sdcard/gobandroid/foo.sgf");
					f.createNewFile();
					
					FileWriter gpxwriter = new FileWriter(f);
					BufferedWriter out = new BufferedWriter(gpxwriter);

					out.write("Hello world");

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
    		if (board_view.touch_y<game.getVisualBoard().getSize()) board_view.touch_y++;
    		break;
    	
    	case KeyEvent.KEYCODE_DPAD_RIGHT:
    		board_view.prepare_keyinput();
    		if (board_view.touch_x<game.getVisualBoard().getSize()) board_view.touch_x++;
    		break;
    		
    	case KeyEvent.KEYCODE_DPAD_CENTER:
    		if (!game.do_move(board_view.touch_x,board_view.touch_y))	;
    		
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
	
}