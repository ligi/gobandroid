package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoMove;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;

public class GoProblemActivity extends GoActivity {
	
	public byte doMoveWithUIFeedback(byte x,byte y) {
		boolean on_path=false;
		
		for (GoMove mve:game.getActMove().getNextMoveVariations())
			on_path|=mve.isOnXY(x, y);
		
		byte res=super.doMoveWithUIFeedback(x,y);
		if (res==GoGame.MOVE_VALID)
			if (game.getActMove().hasNextMove())
				game.jump(game.getActMove().getnextMove(0));
			
		
		if (!on_path)
			game.getActMove().addComment("\nOff Path");
		
		game.notifyGameChange();
		return res;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	this.getMenuInflater().inflate(R.menu.ingame_tsumego, menu);
		return super.onCreateOptionsMenu(menu);
	}


	public boolean onOptionsItemSelected(MenuItem item) {                                                                                                 
        
        switch (item.getItemId()) {                                                                                                                   
                                                                                                                                                      
        case R.id.menu_game_hint:                                                                                                                           
            break;                                                                                                                                
		}
		
		return false;
	}
    

}
