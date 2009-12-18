package org.ligi.gobandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    public String[] menu_items= {"Start Game" ,"Quit" };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("gobandroid" , "before setsize");
        Intent getIntent=getIntent(); 
        byte size=getIntent.getByteExtra("size",(byte)13);
        Log.d("gobandroid" , "size"+size);
        
        
        
        GoGame game=new GoGame(size);
        View board_view=new GoBoardView(this,game);
        board_view.setOnTouchListener((OnTouchListener)board_view );
        setContentView(board_view);
        
    }
      
}