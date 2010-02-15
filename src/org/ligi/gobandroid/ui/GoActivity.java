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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ligi.gobandroid.R;
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
	private static final int MENU_FINISH = 2;
	private static final int MENU_WRITE_SGF = 3;
	private static final int MENU_SETTINGS = 4;

	private GoGame game=null;
	private GoBoardView board_view;

	private SharedPreferences shared_prefs;
	
	private WakeLock mWakeLock=null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		
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
		
		
		GOSkin.setSkin(shared_prefs.getString("skinname", ""));
		GOSkin.setEnabled(shared_prefs.getBoolean("skin", false));
		
		

		Log.i("gobandroid","1do skin" +GOSkin.useSkin());
		Log.i("gobandroid","1name skin" +GOSkin.getSkinName() + "/" + shared_prefs.getString("skinname", ""));
		
		
		board_view.do_zoom=shared_prefs.getBoolean("fatfinger", false);
		
		SharedPreferences.Editor editor=shared_prefs.edit();
		if (shared_prefs.getBoolean("skin", false))
			editor.putBoolean("skin",GOSkin.setSkin(shared_prefs.getString("skinname", "")) );
		
		Log.i("gobandroid","do skin" +GOSkin.useSkin());
		Log.i("gobandroid","name skin" +GOSkin.getSkinName());
		
		editor.commit();
		
		setCustomTitle(R.layout.top);
		((TopView)(this.findViewById(R.id.TopView))).setGame(game);
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
			MenuItem finish_menu = menu.add(0, MENU_FINISH, 0,
			"Finish");
			finish_menu.setIcon(android.R.drawable.ic_menu_set_as);
			
		}
		
		MenuItem save_menu = menu.add(0, MENU_WRITE_SGF, 0,"Save as SGF");
		save_menu.setIcon(android.R.drawable.ic_menu_save);
		/*
		MenuItem settings_menu = menu.add(0, MENU_SETTINGS, 0,"Settings");
		settings_menu.setIcon(android.R.drawable.ic_menu_preferences);
		
*/
		return true;
	}

	public TextView filledTextView(String txt,boolean center,float size) {
		TextView res=new TextView(this);
		res.setText(txt);
		res.setPadding(3, 0, 10, 0);
		if (center)
				res.setGravity(Gravity.CENTER_HORIZONTAL);
		res.setTextSize(size);
		return res;
	}
	
	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case MENU_FINISH:
			TableLayout table=new TableLayout(this);
			TableRow row=new TableRow(this);
			
			row.addView(filledTextView("",true,0.0f));
			
			ImageView img=new ImageView(this);
			img.setImageBitmap(GOSkin.getBlackStone(32));
			img.setPadding(0, 0, 20, 0);
			
			row.addView(img);

			
			img=new ImageView(this);
			img.setImageBitmap(GOSkin.getWhiteStone(32));
			row.addView(img);
			
			table.addView(row);
			
			row=new TableRow(this);
			
			float size1=20.0f;
			float size2=23.0f;
			
			row.addView(filledTextView("Territory",false,size1));
			row.addView(filledTextView(""+game.territory_black,true,size1));
			row.addView(filledTextView(""+game.territory_white,true,size1));
			table.addView(row);
			
			row=new TableRow(this);
			row.addView(filledTextView("Captures",false,size1));
			row.addView(filledTextView(""+game.getCapturesBlack(),true,size1));
			row.addView(filledTextView(""+game.getCapturesWhite(),true,size1));
			table.addView(row);
			
			row=new TableRow(this);
			row.addView(filledTextView("Komi",false,size1));
			row.addView(filledTextView("0",true,size1));
			row.addView(filledTextView(""+game.getKomi(),true,size1));
			
			table.addView(row);
			
			row=new TableRow(this);
			row.addView(filledTextView("Final Points",false,size2));
			row.addView(filledTextView(""+game.getPointsBlack(),true,size2));
			row.addView(filledTextView(""+game.getPointsWhite(),true,size2));
			table.addView(row);
			
			
			
			String game_fin_txt="";
			if (game.getPointsBlack()==game.getPointsWhite())
				 game_fin_txt=("The Game ended in a draw");
						
			if (game.getPointsBlack()>game.getPointsWhite())
				game_fin_txt=("Black won with " + (game.getPointsBlack()-game.getPointsWhite()) + " Points.");
						
			if (game.getPointsWhite()>game.getPointsBlack())
				game_fin_txt=("White won with " + (game.getPointsWhite()-game.getPointsBlack()) + " Points.");
			
			new AlertDialog.Builder(this).setTitle("Game Result").setView(table)
			.setMessage(
					 game_fin_txt
		).setPositiveButton("OK",  new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				finish();
			}
		}).show();
			
			break;
				
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

	private void setCustomTitle(int view_id) {
		try {
		// retrieve value for
		//com.android.internal.R.id.title_container
		int titleContainerId = (Integer) Class.forName(
		"com.android.internal.R$id").getField
		("title_container").get(null);

		// remove all views from titleContainer
		((ViewGroup) this.getWindow().findViewById
				(titleContainerId)).removeAllViews();

		// add new custom title view

		((ViewGroup) this.getWindow().findViewById
					(titleContainerId)).setVisibility(View.VISIBLE	);
			this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, view_id);
		
		} catch(Exception ex) {}
		
		
	} // end of setCustomTitle

    
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