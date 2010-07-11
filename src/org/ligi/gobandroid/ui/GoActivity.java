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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ligi.gobandroid.R;
import org.ligi.gobandroid.logic.GnuGoMover;
import org.ligi.gobandroid.logic.GoGame;
import org.ligi.gobandroid.logic.GoGameProvider;
import org.ligi.gobandroid.logic.SGFHelper;
import org.ligi.gobandroid.ui.alerts.GameInfoAlert;
import org.ligi.gobandroid.ui.alerts.GameResultsAlert;
import org.ligi.tracedroid.logging.Log;

/**
 * Activity for a Go Game
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 * License: This software is licensed with GPLv3
 * 
 **/

public class GoActivity 
		extends Activity 
		implements OnClickListener, OnTouchListener, Runnable
{

	private static final int MENU_UNDO = 0;
	private static final int MENU_PASS = 1;
	private static final int MENU_FINISH = 2;
	private static final int MENU_WRITE_SGF = 3;
	private static final int MENU_SETTINGS = 4;
	private static final int MENU_SHOWCONTROLS= 5;
	private static final int MENU_GAMEINFO = 6;

	private GoGame game=null;
	private GoBoardView board_view;
	private GoBoardOverlay overlay;
	
	private WakeLock mWakeLock=null;
	
	private boolean running=true;

	private boolean review_mode=false;
    
	private Toast info_toast=null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		GoPrefs.init(this);	
		
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		
		info_toast=Toast.makeText(this, "", Toast.LENGTH_LONG);

		if (game==null) {
			// passed from e.g. SGFLoadActivity
			if (GoGameProvider.getGame()!=null)
				game=GoGameProvider.getGame();
			else
			// if there is a game saved e.g. on rotation use this game
			if (getLastNonConfigurationInstance()!=null) 
				game=(GoGame)getLastNonConfigurationInstance();
			else {
				
					byte size = getIntent().getByteExtra("size", (byte) 9);
					byte handicap = getIntent().getByteExtra("handicap", (byte) 0);
		
					int white_player=getIntent().getIntExtra("white_player", 0);
					int black_player=getIntent().getIntExtra("black_player", 0);
					
					game = new GoGame(size,handicap);
					review_mode=false;
					
					game.setGoMover(new GnuGoMover(this,game,black_player!=0,white_player!=0,GoPrefs.getAILevel()));
				
				}
		
			board_view = new GoBoardView(this, game);
			board_view.setOnTouchListener(this);
			
			FrameLayout rel=new FrameLayout(this);
			rel.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));

			FrameLayout.LayoutParams bottom_nav_params=new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
			board_view.setBackgroundColor(0x0000);
			board_view.setLayoutParams(bottom_nav_params);
			
			rel.addView(board_view);
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);

			overlay=new GoBoardOverlay(this,board_view,dm.widthPixels,dm.heightPixels,dm.widthPixels>dm.heightPixels);
			rel.addView(overlay.getView());

			setContentView(rel);
			overlay.updateCommentsSize(dm.widthPixels,dm.heightPixels,dm.widthPixels>dm.heightPixels);
		}
		
		GoGameProvider.setGame(game);
		updateControlsStatus();
	
		if (GoPrefs.getKeepLightEnabled()) 	{
			final PowerManager pm = (PowerManager) (this.getSystemService(Context.POWER_SERVICE)); 
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "DUBwise Wakelog TAG");  
        	mWakeLock.acquire();
			}
		
		new Thread(this).start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		overlay.updateCommentsSize(board_view.getWidth(),board_view.getHeight(),board_view.getWidth()>board_view.getHeight());
		updateControlsStatus();
		return false;
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		// remember the game in case of e.g. a device rotation
		return(game);
	}
	
	public void updateControlsStatus() {
		overlay.updateCommentsSize(board_view.getWidth(),board_view.getHeight(),board_view.getWidth()>board_view.getHeight());
		overlay.getCommentTextView().setText(game.getActMove().getComment());
		overlay.updateButtonState();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (game==null)
			return;
		
		Log.i(" resuming go activity" + GoPrefs.getBoardSkinName());
		
		GOSkin.setBoardSkin(GoPrefs.getBoardSkinName());
		GOSkin.setStoneSkin(GoPrefs.getStoneSkinName());
		
		board_view.regenerate_stones_flag=true;
		
		if (GoPrefs.getFullscreenEnabled())
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		else
			this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	
		setCustomTitle(R.layout.top);
		((TopView)(this.findViewById(R.id.TopView))).setGame(game);
	}
	
	/**
	 *  Creates the menu items 
	 **/
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		if (!game.isFinished())  {
			if (game.canUndo()&&(!game.getGoMover().isMoversMove())) 
				menu.add(0, MENU_UNDO, 0, R.string.undo).setIcon(android.R.drawable.ic_menu_revert);
						
			if (!game.getGoMover().isMoversMove())
				menu.add(0, MENU_PASS, 0,R.string.pass).setIcon(android.R.drawable.ic_menu_set_as); 
			}
		else 
			menu.add(0, MENU_FINISH, 0,R.string.results).setIcon(android.R.drawable.ic_menu_more);
			
		menu.add(0, MENU_GAMEINFO, 0,"Game Info").setIcon(android.R.drawable.ic_menu_help);

		menu.add(0, MENU_WRITE_SGF, 0,R.string.save_as_sgf).setIcon(android.R.drawable.ic_menu_save);
		menu.add(0, MENU_SETTINGS, 0, R.string.settings).setIcon(android.R.drawable.ic_menu_preferences);
		
		return true;
	}

	
	/* Handles menu item selections */
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		
		case MENU_GAMEINFO:
			GameInfoAlert.show(this,game);
			break;
			
		case MENU_SHOWCONTROLS:
			review_mode=!review_mode;
			
			break;
		case MENU_FINISH:
			GameResultsAlert.show(this, game);
			break;
				
		case MENU_UNDO:

			if (GoPrefs.isAskVariantEnabled()) { 
				new AlertDialog.Builder(this).setTitle("Keep Variant?").setMessage("Keep this move as variant?")
				.setPositiveButton(R.string.yes , new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					game.undo(true);
				}
				}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					game.undo(false);
					}
				}).show();
			}
			else
				game.undo(GoPrefs.isKeepVariantEnabled());
			
			
			break;

		case MENU_PASS:
			game.pass();
			break;

		case MENU_WRITE_SGF:
			final EditText input = new EditText(this);   
			input.setText(GoPrefs.getSGFFname());

			new AlertDialog.Builder(this).setTitle(R.string.save_sgf).setMessage("How should the file I will write to " +GoPrefs.getSGFPath() + " be named?").setView(input)
			.setPositiveButton(R.string.ok , new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString(); 
					
				File f = new File(GoPrefs.getSGFPath());
				
				if (!f.isDirectory())
					f.mkdirs();
				
				try {
					f=new File(GoPrefs.getSGFPath() + "/"+value+".sgf");
					f.createNewFile();
					
					FileWriter sgf_writer = new FileWriter(f);
					
					BufferedWriter out = new BufferedWriter(sgf_writer);
					
					out.write(SGFHelper.game2sgf(game));
					out.close();
					sgf_writer.close();
				} catch (IOException e) {
					Log.i(""+e);
				}
	
			}
			}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			// Do nothing.
			}
			}).show();
						break;

		case MENU_SETTINGS:
             startActivity(new Intent(this,GoPrefsActivity.class));
             break;
		}
		
		updateControlsStatus() ;
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
    			board_view.setZoom(false);
    		else
    		{
    			new AlertDialog.Builder(this).setTitle(R.string.end_game_quesstion_title)
    			.setMessage( R.string.quit_confirm
    			).setPositiveButton(R.string.yes,  new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				game.getGoMover().stop();
    				finish();
    			}
    		}).setCancelable(true).setNegativeButton(R.string.no,  new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				
    			}
    		}).show();
    				
    		}
    		return true;
    		
    	}
    	updateControlsStatus();
    	board_view.invalidate();
    	return super.onKeyDown(keyCode, event);	
    }
	
    @Override 
    public void onDestroy() {
    	super.onDestroy();
    	releaseWakeLock();
    }
    
    public void releaseWakeLock() {
    	if (mWakeLock==null)
    		return;
    	
    	mWakeLock.release();
    	mWakeLock=null;
    }

    @Override 
    public void onPause() {
    	super.onPause();
    	
		try {
			File f=new File(GoPrefs.getSGFPath() + "/autosave.sgf");
			f.createNewFile();
			
			FileWriter sgf_writer = new FileWriter(f);
			
			BufferedWriter out = new BufferedWriter(sgf_writer);
			
			out.write(SGFHelper.game2sgf(game));
			out.close();
			sgf_writer.close();
			
		} catch (IOException e) {
			Log.i(""+e);
		}
	
		releaseWakeLock();
    }
    
    
	@Override
	public void onClick(View btn) {
		
	}

	/**
	 * show a the info toast with a specified text from a resource ID
	 * 
	 * @param resId
	 */
	public void showInfoToast(int resId) {
		info_toast.setText(resId);
		info_toast.show();
	}
	
	public boolean onTouch( View v, MotionEvent event ) {
		
		if (!game.getGoMover().isReady())
			showInfoToast(R.string.wait_gnugo);
		else if (game.getGoMover().isMoversMove())
			showInfoToast(R.string.not_your_turn);
		else
			board_view.doTouch(event);
		
    	updateControlsStatus();
    	return true;
    }
 
    
    @Override 
    protected void onRestoreInstanceState(Bundle savedInstanceState) { 
      super.onRestoreInstanceState(savedInstanceState); 
      review_mode=savedInstanceState.getBoolean("review_mode");
      updateControlsStatus();
    } 
    
    @Override 
    protected void onSaveInstanceState(Bundle outState) { 
      outState.putBoolean("review_mode", review_mode);
      super.onSaveInstanceState(outState); 
    }

	class UpdateBoardViewClass implements Runnable {
		@Override
		public void run() {
			board_view.invalidate();
			updateControlsStatus();
		}
	}
	
	class UptateOverlayVisibilityClass implements Runnable {
		@Override
		public void run() {
			overlay.getView().setVisibility(getBoardViewNeededVisibility());
	    
		}
	}

	private int getBoardViewNeededVisibility() {
		return board_view.isZoomed()?View.INVISIBLE:View.VISIBLE;
	}
	private int act_move_pos=0;
	
	@Override
	public void run() {
		Looper.prepare();
		while (running) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) { }
				// invalidate if the move changed - e.g. when go engine moved
				if (game.getActMove().getMovePos()!=act_move_pos)
					{
					act_move_pos=game.getActMove().getMovePos();
					this.runOnUiThread(new UpdateBoardViewClass());
					
					}
				if (overlay.getView().getVisibility() != getBoardViewNeededVisibility())
					this.runOnUiThread(new UptateOverlayVisibilityClass());
				
			}
		}

}