package org.ligi.gobandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Activity for a Game
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         
 * This software is licenced with GPLv3 
 * 
**/


public class GoActivity extends Activity {
    private static final int MENU_UNDO = 0;
    public String[] menu_items= {"Start Game" ,"Quit" };
    
    GoGame game;
    View board_view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent getIntent=getIntent(); 
        byte size=getIntent.getByteExtra("size",(byte)9);
        game=new GoGame(size);
        board_view=new GoBoardView(this,game);
        board_view.setOnTouchListener((OnTouchListener)board_view );
        setContentView(board_view);
    }
    
int i=0;
    /* Creates the menu items */
//public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    //public boolean onCreateOptionsMenu(Menu menu) {
   public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (game.canUndo()) {
            MenuItem settings_menu=menu.add(0,MENU_UNDO,0,"Undo" + (i++) );
            settings_menu.setIcon(android.R.drawable.ic_menu_revert);
        }
       return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        
        switch (item.getItemId()) {
        case MENU_UNDO:
            game.undo();
            board_view.invalidate();
            
        }
        return false;
    }

      
}