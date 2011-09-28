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
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGameProvider;
import org.ligi.gobandroid_hd.ui.alerts.GameInfoAlert;
import org.ligi.gobandroid_hd.ui.alerts.GameResultsAlert;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
/**
 * Activity for a Go Game
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 * License: This software is licensed with GPLv3
 * 
 **/

public 	class GoActivity 
		extends FragmentActivity {

	private GoBoardViewHD go_board=null;
	private TextView comment_tv;
	private GoGame game;
	
	private static final int MENU_UNDO = 0;
    private static final int MENU_PASS = 1;
    private static final int MENU_FINISH = 2;
    private static final int MENU_WRITE_SGF = 3;
    private static final int MENU_SETTINGS = 4;
    private static final int MENU_SHOWCONTROLS= 5;
    private static final int MENU_GAMEINFO = 6;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.game);
	
        if (GoPrefs.getFullscreenEnabled())                                                                                                           
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);                                                                          
        else                                                                                                                                          
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);                                                              
	
		go_board=(GoBoardViewHD)findViewById(R.id.go_board);
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
	
    public boolean onOptionsItemSelected(MenuItem item) {                                                                                                 
        
        switch (item.getItemId()) {                                                                                                                   
                                                                                                                                                      
        case MENU_GAMEINFO:                                                                                                                           
                GameInfoAlert.show(this,game);                                                                                                        
                break;                                                                                                                                
                                                                                                                                                      
        case MENU_SHOWCONTROLS:                                                                                                                       
                //review_mode=!review_mode;                                                                                                             
                                                                                                                                                      
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
        	SaveSGFDialog.show(this);
        	break;

		case MENU_SETTINGS:
            startActivity(new Intent(this,GoPrefsActivity.class));
            break;
		}
		
		return false;
	}

	
	public void game2ui() {
		go_board.postInvalidate();
		comment_tv.setText(game.getActMove().getComment());
	}

}