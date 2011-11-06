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

import org.ligi.android.common.views.SquareView;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGameProvider;
import org.ligi.gobandroid_hd.ui.alerts.GameInfoAlert;
import org.ligi.tracedroid.logging.Log;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
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
		extends FragmentActivity implements OnTouchListener {

	private GoBoardViewHD go_board=null;
	private TextView comment_tv;
	public GoGame game;

	private Toast info_toast=null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View customNav =new InGameActionBarView(this);
		
		//   View customNav = LayoutInflater.from(this).inflate(R.layout.actionbar_custom_view, null);
/*
        //Bind to its state change
        ((RadioGroup)customNav.findViewById(R.id.radio_nav)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Toast.makeText(ActionBarCustomNavigation.this, "Navigation selection changed.", Toast.LENGTH_SHORT).show();
            }
        });

        //Attach to the action bar
        getSupportActionBar().setCustomView(customNav);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
    */
		
		this.setContentView(R.layout.game);
		getSupportActionBar().setCustomView(customNav);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
		
		
        if (GoPrefs.getFullscreenEnabled())                
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);                                                                          
        else                                                                                                                                          
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);                                                              
	
        info_toast=Toast.makeText(this, "", Toast.LENGTH_LONG);
        
		go_board=(GoBoardViewHD)findViewById(R.id.go_board);
		
		go_board.setOnTouchListener(this);
		
		comment_tv=(TextView)findViewById(R.id.comments_textview);
		
		game=GoGameProvider.getGame();
		
		game.addGoGameChangeListener(new GoGame.GoGameChangeListener() {
			
			@Override
			public void onGoGameChange() {
				game2ui();
			}
		});
		game2ui();
	}
	
	
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
/*
    	if (!game.isFinished())  {                                                                                                                    
            if (game.canUndo()&&(!game.getGoMover().isMoversMove()))                                                                              
                    menu.add(0, MENU_UNDO, 0, R.string.undo).setIcon(android.R.drawable.ic_menu_revert);                                          
                                                                                                                                                  
            if (!game.getGoMover().isMoversMove())                                                                                                
                    menu.add(0, MENU_PASS, 0,R.string.pass).setIcon(android.R.drawable.ic_menu_set_as);                                           
            }                                                                                                                                     
    else                                                                                                                                          
            menu.add(0, MENU_FINISH, 0,R.string.results).setIcon(android.R.drawable.ic_menu_more);                                                
*/
    	this.getMenuInflater().inflate(R.menu.ingame_common, menu);
		
		return true;//super.onCreateOptionsMenu(menu);
	}


	public boolean onOptionsItemSelected(MenuItem item) {                                                                                                 
        
        switch (item.getItemId()) {                                                                                                                   
                                                                                                                                                      
        case R.id.menu_game_info:                                                                                                                           
                GameInfoAlert.show(this,game);                                                                                                        
                break;                        
                
        case R.id.menu_game_undo:
	            if (!game.canUndo())
	            	break;
	            
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
	            

	            break;
        		
        /*                                                                                                                                              
        case MENU_SHOWCONTROLS:                                                                                                                       
                //review_mode=!review_mode;                                                                                                             
                                                                                                                                                      
                break;                                                                                                                                
        case MENU_FINISH:                                                                                                                             
                GameResultsAlert.show(this, game);                                                                                                    
                break;                                                                                                                                
                                                                                                                                                      
        * imho redundant because of review controls?
                                                                                                                     
        case MENU_PASS:                                                                                                                               
                game.pass();                                                                                                                          
                break;                                                                                                                                
         */                                                                                                                                                    
        case R.id.menu_write_sgf:                                                                                                                          
        	SaveSGFDialog.show(this);
        	break;

		case R.id.menu_settings:
            startActivity(new Intent(this,GoPrefsActivity.class));
            break;
		}
		
		return false;
	}
    
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	
    	switch (keyCode) {
    	case KeyEvent.KEYCODE_DPAD_UP:
    		go_board.prepare_keyinput();
    		if (go_board.touch_y>0) go_board.touch_y--;
    		break;
    	
    	case KeyEvent.KEYCODE_DPAD_LEFT:
    		go_board.prepare_keyinput();
    		if (go_board.touch_x>0) go_board.touch_x--;
    		break;
    	
    	case KeyEvent.KEYCODE_DPAD_DOWN:
    		go_board.prepare_keyinput();
    		if (go_board.touch_y<game.getVisualBoard().getSize()-1) go_board.touch_y++;
    		break;
    	
    	case KeyEvent.KEYCODE_DPAD_RIGHT:
    		go_board.prepare_keyinput();
    		if (go_board.touch_x<game.getVisualBoard().getSize()-1) go_board.touch_x++;
    		break;
    		
    	case KeyEvent.KEYCODE_DPAD_CENTER:
    		doMoveWithUIFeedback(go_board.touch_x,go_board.touch_y);
    		//go_board.setZoom(false);
    		break;
    		
    	case KeyEvent.KEYCODE_BACK:
    		/*if (go_board.isZoomed())
    			go_board.setZoom(false);
    		else{ */
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
    				
    		//}
    		return true;
    		
    	
    	}
    	go_board.postInvalidate();
    	
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
		go_board.postInvalidate();
		comment_tv.setText(game.getActMove().getComment());
	}
	
	public boolean onTouch( View v, MotionEvent event ) {
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
	

    public void doTouch( MotionEvent event) {
				

    	// calculate position on the field by position on the touchscreen
    	go_board.touch_x=(byte)(event.getX()/go_board.stone_size);
    	go_board.touch_y=(byte)(event.getY()/go_board.stone_size);

    	
    	if (event.getAction()==MotionEvent.ACTION_UP) {

    		if (go_board.move_stone_mode) {
    			// TODO check if this is an illegal move ( e.g. in variants )
    			game.getActMove().setXY(go_board.touch_x, go_board.touch_y);
    			game.refreshBoards();
    			go_board.move_stone_mode=false;
    		}
    		else if ((game.getActMove().getX()==go_board.touch_x)&&(game.getActMove().getY()==go_board.touch_y)) 
    			go_board.initializeStoneMove();
    		else 
    			doMoveWithUIFeedback(go_board.touch_x,go_board.touch_y);
        			
    		go_board.touch_x=-1;
    		go_board.touch_y=-1;
        			
    	}

      	go_board.invalidate();  // the board looks different after a move (-;

     }
    
    public boolean doAskToKeepVariant() {
    	return GoPrefs.isAskVariantEnabled();
    }
}