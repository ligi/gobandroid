package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoMarker;
import org.ligi.gobandroid_hd.logic.GoMove;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;

public class GoProblemActivity extends GoActivity {
	
	private GoMove finishing_move;
	
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


	public GoMove getCorrectMove(GoMove act_mve) {
		if (act_mve.getComment().equals("Correct"))
			return act_mve;
		
		for (GoMove next_moves:act_mve.getNextMoveVariations()) {
			GoMove res=getCorrectMove(next_moves);
			if (res!=null)
				return res;
		}
			
		return null;
	}
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// try to find the correct solution
		finishing_move=getCorrectMove(game.getActMove());
		if (finishing_move==null) 
			new AlertDialog.Builder(this).setMessage("foo").show();
		
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	this.getMenuInflater().inflate(R.menu.ingame_tsumego, menu);
		return super.onCreateOptionsMenu(menu);
	}


	public boolean onOptionsItemSelected(MenuItem item) {                                                                                                 
        
        switch (item.getItemId()) {                                                                                                                   
                                                                                                                                                      
        case R.id.menu_game_hint:  
        	GoMove myActMove=finishing_move;
        	int p=myActMove.getMovePos();
        	while (true) {
        		if(myActMove.isFirstMove())
        			break;
        		finishing_move.addMarker(new GoMarker(myActMove.getX(),myActMove.getY(),""+p));
        		p--;
        		myActMove=myActMove.getParent();
        		
        	}
        	
        	game.jump(finishing_move);
        	
            break;                                                                                                                                
		}
		
		return false;
	}
    

}
