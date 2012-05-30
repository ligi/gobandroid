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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.gobandroid_hd.ui.alerts.GameInfoAlert;
import org.ligi.gobandroid_hd.ui.alerts.GameResultsAlert;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.fragments.DefaultGameExtrasFragment;
import org.ligi.gobandroid_hd.ui.fragments.ZoomGameExtrasFragment;
import org.ligi.gobandroid_hd.ui.recording.SaveSGFDialog;
import org.ligi.tracedroid.logging.Log;

import org.ligi.android.common.activitys.ActivityOrientationLocker;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;
/**
 * Activity for a Go Game
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 * License: This software is licensed with GPLv3
 * 
 **/

public class GoActivity 
		extends GobandroidFragmentActivity implements OnTouchListener, OnKeyListener,  GoGame.GoGameChangeListener {

	private GoBoardViewHD go_board=null;

	private Toast info_toast=null;
	
	public ZoomGameExtrasFragment myZoomFragment;
	private Fragment actFragment;

	public GoSoundManager sound_man ;
	
	private InteractionScope interaction_scope;
	
	
	public Fragment getGameExtraFragment() {
		
		return new DefaultGameExtrasFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		interaction_scope=getApp().getInteractionScope();
		this.getSupportActionBar().setHomeButtonEnabled(true);
		
		ActivityOrientationLocker.disableRotation(this); 

		if (getSettings().isWakeLockEnabled()) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		
		
		if (getGame()==null) { // cannot do anything without a game 
			finish();
			return;
		}
			
		if (sound_man==null)
			sound_man=new GoSoundManager(this);
		
		View customNav =new InGameActionBarView(this);
		
		FragmentTransaction fragmentTransAction =this.getSupportFragmentManager().beginTransaction();
		
		
		fragmentTransAction.add(R.id.game_extra_container, getGameExtraFragment()).commit();
		
		this.setContentView(R.layout.game);
		getSupportActionBar().setCustomView(customNav);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        customNav.setFocusable(false);
     
        info_toast=Toast.makeText(this.getBaseContext(), "", Toast.LENGTH_LONG);
        
        setupBoard();
        
		game2ui();
		getZoomFragment();
	}
	

	/**
	 * find the go board widget and set up some properties 
	 */
	private void setupBoard() {
		
		go_board=(GoBoardViewHD)findViewById(R.id.go_board);
		
		if (go_board==null)
			return; // had an NPE here - TODO figure out why exactly and if this fix has some disadvantage
		
		go_board.setOnTouchListener(this);
		go_board.setOnKeyListener(this);
			
	
	}
	
	private int last_processed_move_change_num=0;
	
	@Override
	public void onGoGameChange() {
		if (getGame().getActMove().getMovePos()>last_processed_move_change_num) {
			if (getGame().isBlackToMove())
				sound_man.playSound(GoSoundManager.SOUND_PLACE1);
			else
				sound_man.playSound(GoSoundManager.SOUND_PLACE2);
		}
		last_processed_move_change_num=getGame().getActMove().getMovePos();
		game2ui();
	}
	
	

	@Override 
	protected void onStart() {
		if (getGame()==null)
			Log.w("we do not have a game in onStart of a GoGame activity - thats crazy!");
		else
			getGame().addGoGameChangeListener(this);
		
		super.onStart();
	}

	
	@Override
	protected void onStop() {
		if (getGame()==null)
			Log.w("we do not have a game (anymore) in onStop of a GoGame activity - thats crazy!");
		else
			getGame().removeGoGameChangeListener(this);
		
		super.onStop();
	}

	/**
	 * set some preferences on the go board - intended to be called in onResume
	 * 
	 */
	private void setBoardPreferences() {
		if (go_board==null) { 
			Log.w("setBoardPreferences() called with go_board==null - means setupBoard() was propably not called - skipping to not FC");
			return;
		}

		go_board.do_legend=getSettings().isLegendEnabled();
		go_board.legend_sgf_mode=getSettings().isSGFLegendEnabled();
		go_board.setGridEmboss(getSettings().isGridEmbossEnabled());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setBoardPreferences();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				sound_man.playSound(GoSoundManager.SOUND_START);
			}
			
		}, 100);
	}
	
	public ZoomGameExtrasFragment getZoomFragment() {
		if (myZoomFragment==null)
			myZoomFragment=new ZoomGameExtrasFragment(true);
		return myZoomFragment;
	}

	@Override
	public boolean doFullScreen() {
		return getSettings().isFullscreenEnabled()|getResources().getBoolean(R.bool.force_fullscreen);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getSupportMenuInflater().inflate(R.menu.ingame_common, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {                                                                                                 
        
        switch (item.getItemId()) {                                                                                                                   
                           
	        case R.id.menu_game_switchmode:
	        	new SwitchModeDialog(this).show();
	        	return true;
	        	
	        case R.id.menu_game_info:                                                                                                                           
                new GameInfoAlert(this,getGame()).show(); 
                return true;                  
	                
	        case R.id.menu_game_undo:
	            if (!getGame().canUndo())
	            	break;
	            
	            requestUndo();
	            return true;

  
	        case R.id.menu_game_pass:
	        	getGame().pass();        
	        	getGame().notifyGameChange();
	        	return true;
	        	
	        case R.id.menu_game_results:
	        	new GameResultsAlert(this,getGame()).show();                                                                                                    
	        	return true;
	        	
	        case R.id.menu_write_sgf:                                                                                                                          
	        	new SaveSGFDialog(this).show();
	        	return true;
	
	        case R.id.preferences:
	        	startActivity(new Intent(this,GoPrefsActivity.class));
	        	return true;
	        	
	        case android.R.id.home:
	        	quit(true);
	        	return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * control whether we want to ask the user - different in modes
	 * @return
	 */
	public boolean isAsk4QuitEnabled() {
		return true;
	}
	
	private void shutdown(boolean toHome) {
		sound_man.playSound(GoSoundManager.SOUND_END);
		getGame().getGoMover().stop();
		finish();
		
		if (toHome) {
			startActivity(new Intent(this,gobandroid.class));
		}
	}
	
	public void quit(final boolean toHome) {
		if (!isAsk4QuitEnabled()) {
			shutdown(toHome);
			return;
		}
		
		new AlertDialog.Builder(this).setTitle(R.string.end_game_quesstion_title)
		.setMessage( R.string.quit_confirm
		).setPositiveButton(R.string.yes,  new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			shutdown(toHome);
		}
		}).setCancelable(true).setNegativeButton(R.string.no,  new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		}
		}).show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				quit(false);
				return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * show a the info toast with a specified text from a resource ID
	 * 
	 * @param resId
 	**/
    public void showInfoToast(int resId) {
		info_toast.setText(resId);
		info_toast.show();
	}

    public byte doMoveWithUIFeedback(byte x,byte y) {
    	info_toast.cancel();
    	byte res=getGame().do_move(x, y);
    	switch(res){
    		case GoGame.MOVE_INVALID_IS_KO:
    			showInfoToast(R.string.invalid_move_ko);
    			break;
    		case GoGame.MOVE_INVALID_CELL_NO_LIBERTIES:
    			showInfoToast(R.string.invalid_move_no_liberties);
    			break;
    	}
    	return res;
    }
	
	public void game2ui() {
		go_board.postInvalidate();
		refreshZoomFragment();
	}
	
	public void setFragment(Fragment newFragment) {
		if (actFragment==newFragment) {
			Log.i("GoFrag same same");
			return;
		}
		Log.i("GoFrag changing" + newFragment);
		actFragment=newFragment;
		FragmentTransaction fragmentTransAction =getSupportFragmentManager().beginTransaction();
		fragmentTransAction.replace(R.id.game_extra_container,actFragment).commit();
	}
	
	@Override
	public boolean onTouch( View v, MotionEvent event ) {
		
		if (event.getAction()==MotionEvent.ACTION_UP) {
			setFragment(getGameExtraFragment());
			if (getResources().getBoolean(R.bool.small))
					this.getSupportActionBar().show();
			
		}else if (event.getAction()==MotionEvent.ACTION_DOWN) {
			setFragment(getZoomFragment());
			if (getResources().getBoolean(R.bool.small))
				this.getSupportActionBar().hide();
			
			if (getGame().isBlackToMove())
				sound_man.playSound(GoSoundManager.SOUND_PICKUP1);
			else
				sound_man.playSound(GoSoundManager.SOUND_PICKUP2);
		}
			
		Log.i("touch");
		if (!getGame().getGoMover().isReady())
			showInfoToast(R.string.wait_gnugo);
		else if (getGame().getGoMover().isMoversMove())
			showInfoToast(R.string.not_your_turn);
		else
			doTouch(event);
		
    	//updateControlsStatus();
    	return true;
    }
	

    @Override 
    public void onPause() {
    	super.onPause();
    	
		try {
			File f=new File(getSettings().getReviewPath() + "/autosave.sgf");
			f.createNewFile();
			
			FileWriter sgf_writer = new FileWriter(f);
			
			BufferedWriter out = new BufferedWriter(sgf_writer);
			
			out.write(SGFHelper.game2sgf(getGame()));
			out.close();
			sgf_writer.close();
			
		} catch (IOException e) {
			Log.i(""+e);
		}
	
    }

    public void doTouch( MotionEvent event) {
				
    	// calculate position on the field by position on the touchscreen

    	
    	interaction_scope.setTouchPosition(getBoard().pixel2boardPos(event.getX(),event.getY()));
    	if (event.getAction()==MotionEvent.ACTION_UP) {

    		if (go_board.move_stone_mode) {
    			// TODO check if this is an illegal move ( e.g. in variants )
    			getGame().getActMove().setXY((byte)interaction_scope.getTouchX(),(byte)interaction_scope.getTouchY());
    			getGame().refreshBoards();
    			go_board.move_stone_mode=false;
    		}
    		else if ((getGame().getActMove().getX()==interaction_scope.getTouchX())&&(getGame().getActMove().getY()==interaction_scope.getTouchY())) 
    			go_board.initializeStoneMove();
    		else 
    			doMoveWithUIFeedback((byte)interaction_scope.getTouchX(),(byte)interaction_scope.getTouchY());
        		
    		interaction_scope.setTouchPosition(-1);
        			
    	}


    	getGame().notifyGameChange();
    }
    
    public boolean doAskToKeepVariant() {
    	return GoPrefs.isAskVariantEnabled()&&getApp().getInteractionScope().ask_variant_session;
    }

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		Log.i("key event");
		if (event.getAction()==KeyEvent.ACTION_DOWN)
    	switch (keyCode) {
    	case KeyEvent.KEYCODE_DPAD_UP:
    		go_board.prepare_keyinput();
    		if (interaction_scope.getTouchY()>0) 
    			interaction_scope.touch_position-=getGame().getSize();
    		else
    			return false;
    		break;
    		
    	case KeyEvent.KEYCODE_DPAD_LEFT:
    		go_board.prepare_keyinput();
    		if (interaction_scope.getTouchX()>0) 
    			interaction_scope.touch_position--;
    		else
    			return false;
    		break;
    		
    	case KeyEvent.KEYCODE_DPAD_DOWN:
    		go_board.prepare_keyinput();
    		if (interaction_scope.getTouchY()<getGame().getVisualBoard().getSize()-1) 
    			interaction_scope.touch_position+=getGame().getSize();
    		else
    			return false;
    		break;
    		
    	case KeyEvent.KEYCODE_DPAD_RIGHT:
    		go_board.prepare_keyinput();
    		if (interaction_scope.getTouchX()<getGame().getVisualBoard().getSize()-1)
    			interaction_scope.touch_position++;
    		else
    			return false;
    		break;
    		
    	case KeyEvent.KEYCODE_DPAD_CENTER:
    		doMoveWithUIFeedback((byte)interaction_scope.getTouchX(),(byte)interaction_scope.getTouchY());
    		break;
    		
    	default:
    		
    		return false;
    	
    	}
    	go_board.postInvalidate();
    	refreshZoomFragment();
      	return true;
	}
	
	public void refreshZoomFragment() {
		if (getZoomFragment().getBoard()==null) // nothing we can do
			return;
	  	if (myZoomFragment.getBoard()!=null)
      		myZoomFragment.getBoard().postInvalidate();
    
	}
	public GoBoardViewHD getBoard() {
		if (go_board==null)
			setupBoard();
		return go_board;
	}
	
	public void requestUndo() {
		if (doAskToKeepVariant()) {                                                                                                  
			new UndoWithVariationDialog(this).show();
	    }                                                                                                                                     
	    else                                                                                                                                  
	    	getGame().undo(GoPrefs.isKeepVariantEnabled());
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	
}