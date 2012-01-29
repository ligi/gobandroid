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

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGameProvider;
import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.gobandroid_hd.ui.alerts.GameInfoAlert;
import org.ligi.gobandroid_hd.ui.alerts.GameResultsAlert;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.ingame_common.SwitchModeHelper;
import org.ligi.gobandroid_hd.ui.recording.SaveSGFDialog;
import org.ligi.tracedroid.logging.Log;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
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
		extends GobandroidFragmentActivity implements OnTouchListener, OnKeyListener {

	private GoBoardViewHD go_board=null;
	public GoGame game;

	private Toast info_toast=null;
	
	private ZoomGameExtrasFragment myZoomFragment;
	private Fragment actFragment;

	public GoSoundManager sound_man ;
	 
	public Fragment getGameExtraFragment() {
		return new DefaultGameExtrasFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getSettings().isWakeLockEnabled()) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		
		game=GoGameProvider.getGame();
		
		if (game==null) { // cannot do anything without a 
			finish();
			return;
		}
			
		
		sound_man=new GoSoundManager(this);
		
		View customNav =new InGameActionBarView(this);
		
		FragmentTransaction fragmentTransAction =this.getSupportFragmentManager().beginTransaction();
		myZoomFragment= new ZoomGameExtrasFragment();
		
		fragmentTransAction.add(R.id.game_extra_container, getGameExtraFragment()).commit();
		
		this.setContentView(R.layout.game);
		getSupportActionBar().setCustomView(customNav);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        customNav.setFocusable(false);
     
        info_toast=Toast.makeText(this.getBaseContext(), "", Toast.LENGTH_LONG);
        
        setupBoard();
		
		game2ui();
    	sound_man.playGameIntro();
	}
	

	/**
	 * find the go board widget and set up some properties 
	 */
	private void setupBoard() {
		
		go_board=(GoBoardViewHD)findViewById(R.id.go_board);
		
		go_board.setOnTouchListener(this);
		go_board.setOnKeyListener(this);
		go_board.do_legend=getSettings().isLegendEnabled();
		go_board.legend_sgf_mode=getSettings().isSGFLegendEnabled();
		go_board.grid_embos=getSettings().isGridEmbossEnabled();
		
		game.addGoGameChangeListener(new GoGame.GoGameChangeListener() {
			
			@Override
			public void onGoGameChange() {
				game2ui();
			}
		});
	}
	
	@Override
	public boolean doFullScreen() {
		return getSettings().isFullscreenEnabled()|getResources().getBoolean(R.bool.force_fullscreen);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.ingame_common, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {                                                                                                 
        
        switch (item.getItemId()) {                                                                                                                   
                           
	        case R.id.menu_game_switchmode:
	        	SwitchModeHelper.show(this);
	        	break;
	        	
	        case R.id.menu_game_info:                                                                                                                           
                GameInfoAlert.show(this,game); 
                
                getBoard().screenshot(game.getMetaData().getFileName()+".png");
	        	break;                        
	                
	        case R.id.menu_game_undo:
	            if (!game.canUndo())
	            	break;
	            
	            requestUndo();
	            break;

  
	        case R.id.menu_game_pass:
	        	game.pass();        
	        	game.notifyGameChange();
	        	break;
	        case R.id.menu_game_results:
	        	GameResultsAlert.show(this, game);                                                                                                    
	        	break;
	        case R.id.menu_write_sgf:                                                                                                                          
	        	SaveSGFDialog.show(this);
	        	break;
	
		}
		
		return false;
	}
	
	/**
	 * control whether we want to ask the user - diffrent in modes
	 * @return
	 */
	public boolean isAsk4QuitEnabled() {
		return true;
	}
	
	public void shutdown() {
		sound_man.playSound(GoSoundManager.SOUND_END);
		game.getGoMover().stop();
		finish();
	}
	
	public void ask4quit() {
		if (!isAsk4QuitEnabled()) {
			shutdown();
			return;
		}
		
		new AlertDialog.Builder(this).setTitle(R.string.end_game_quesstion_title)
		.setMessage( R.string.quit_confirm
		).setPositiveButton(R.string.yes,  new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			shutdown();
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
				ask4quit();
				return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}


	
    @Override
	protected void onResume() {
		super.onResume();
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
    	byte res=game.do_move(x, y);
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
		new Handler() {
			@Override
	         public void  handleMessage(Message msg) {
				go_board.postInvalidate();
		      	if (myZoomFragment.getBoard()!=null)
		      		myZoomFragment.getBoard().invalidate();
	         }
		}.sendEmptyMessage(0);
	}
	
	public void setFragment(Fragment newFragment) {
		if (actFragment==newFragment)
			return;
		actFragment=newFragment;
		FragmentTransaction fragmentTransAction =this.getSupportFragmentManager().beginTransaction();
		fragmentTransAction.replace(R.id.game_extra_container,actFragment).commit();
	}
	
	public boolean onTouch( View v, MotionEvent event ) {
		
		if (event.getAction()==MotionEvent.ACTION_UP) {
			setFragment(getGameExtraFragment());
			if (getResources().getBoolean(R.bool.small))
					this.getSupportActionBar().show();
			
			if (game.isBlackToMove())
				sound_man.playSound(GoSoundManager.SOUND_PLACE1);
			else
				sound_man.playSound(GoSoundManager.SOUND_PLACE2);
			
		}else if (event.getAction()==MotionEvent.ACTION_DOWN)
		 {
			setFragment(myZoomFragment);
			if (getResources().getBoolean(R.bool.small))
				this.getSupportActionBar().hide();
			
			if (game.isBlackToMove())
				sound_man.playSound(GoSoundManager.SOUND_PICKUP1);
			else
				sound_man.playSound(GoSoundManager.SOUND_PICKUP2);
		}
			
		Log.i("touch");
		if (!game.getGoMover().isReady())
			showInfoToast(R.string.wait_gnugo);
		else if (game.getGoMover().isMoversMove())
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
			
			out.write(SGFHelper.game2sgf(game));
			out.close();
			sgf_writer.close();
			
		} catch (IOException e) {
			Log.i(""+e);
		}
	
    }

    public void doTouch( MotionEvent event) {
				
    	// calculate position on the field by position on the touchscreen

    	GoInteractionProvider.setTouchPosition(getBoard().pixel2boardPos(event.getX(),event.getY()));
    	if (event.getAction()==MotionEvent.ACTION_UP) {

    		if (go_board.move_stone_mode) {
    			// TODO check if this is an illegal move ( e.g. in variants )
    			game.getActMove().setXY((byte)GoInteractionProvider.getTouchX(),(byte)GoInteractionProvider.getTouchY());
    			game.refreshBoards();
    			go_board.move_stone_mode=false;
    		}
    		else if ((game.getActMove().getX()==GoInteractionProvider.getTouchX())&&(game.getActMove().getY()==GoInteractionProvider.getTouchY())) 
    			go_board.initializeStoneMove();
    		else 
    			doMoveWithUIFeedback((byte)GoInteractionProvider.getTouchX(),(byte)GoInteractionProvider.getTouchY());
        		
    		GoInteractionProvider.setTouchPosition(-1);
        			
    	}


    	game.notifyGameChange();
    }
    
    public boolean doAskToKeepVariant() {
    	return GoPrefs.isAskVariantEnabled();
    }

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		Log.i("key event");
		if (event.getAction()==KeyEvent.ACTION_DOWN)
    	switch (keyCode) {
    	case KeyEvent.KEYCODE_DPAD_UP:
    		go_board.prepare_keyinput();
    		if (GoInteractionProvider.getTouchY()>0) 
    			GoInteractionProvider.touch_position-=game.getSize();
    		else
    			return false;
    		break;
    		
    	case KeyEvent.KEYCODE_DPAD_LEFT:
    		go_board.prepare_keyinput();
    		if (GoInteractionProvider.getTouchX()>0) 
    			GoInteractionProvider.touch_position--;
    		else
    			return false;
    		break;
    		
    	case KeyEvent.KEYCODE_DPAD_DOWN:
    		go_board.prepare_keyinput();
    		if (GoInteractionProvider.getTouchY()<game.getVisualBoard().getSize()-1) 
    			GoInteractionProvider.touch_position+=game.getSize();
    		else
    			return false;
    		break;
    		
    	case KeyEvent.KEYCODE_DPAD_RIGHT:
    		go_board.prepare_keyinput();
    		if (GoInteractionProvider.getTouchX()<game.getVisualBoard().getSize()-1)
    			GoInteractionProvider.touch_position++;
    		else
    			return false;
    		break;
    		
    	case KeyEvent.KEYCODE_DPAD_CENTER:
    		doMoveWithUIFeedback((byte)GoInteractionProvider.getTouchX(),(byte)GoInteractionProvider.getTouchY());
    		break;
    		
    
    	
    	}
    	go_board.postInvalidate();
      	if (myZoomFragment.getBoard()!=null)
      		myZoomFragment.getBoard().invalidate();
      	return true;
	}
	
	public GoBoardViewHD getBoard() {
		return go_board;
	}
	
	public void requestUndo() {
		if (doAskToKeepVariant()) {                                                                                                  
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
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	
}