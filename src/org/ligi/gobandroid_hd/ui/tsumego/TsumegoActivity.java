package org.ligi.gobandroid_hd.ui.tsumego;

import java.util.Vector;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.logic.GoMove;
import org.ligi.gobandroid_hd.ui.GoActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;

public class TsumegoActivity extends GoActivity implements GoGameChangeListener {
	
	private GoMove finishing_move;

	private TsumegoGameExtrasFragment myTsumegoExtrasFragment;
	private Vector<GoMove> on_path_moves;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setTitle(R.string.tsumego);
		
		// build a on path Vector to do a fast isOnPath() later 
		on_path_moves=new Vector<GoMove>();
		recursive_add_on_path_moves(game.getFirstMove());
				
		// try to find the correct solution
		finishing_move=getCorrectMove(game.getFirstMove());
		
		if (finishing_move==null) 
			new AlertDialog.Builder(this).setMessage("foo").show();

		game.addGoGameChangeListener(this);
    }
    
    private void recursive_add_on_path_moves(GoMove act) {
    	on_path_moves.add(act);
    	if (act.hasNextMove())
    		for (GoMove child:act.getNextMoveVariations())
    			recursive_add_on_path_moves(child);
    }
    
    @Override
	protected void onDestroy() {
    	game.removeGoGameChangeListener(this);
    	super.onDestroy();
    }

	public boolean isOnPath() {
		return on_path_moves.contains(game.getActMove());
    }
    
	public byte doMoveWithUIFeedback(byte x,byte y) {
		
		byte res=super.doMoveWithUIFeedback(x,y);
		if (res==GoGame.MOVE_VALID)
			if (game.getActMove().hasNextMove())
				game.jump(game.getActMove().getnextMove(0));
			

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
	public boolean onCreateOptionsMenu(Menu menu) {
    	this.getMenuInflater().inflate(R.menu.ingame_tsumego, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {                                                                                                 
        if (!super.onOptionsItemSelected(item))
        switch (item.getItemId()) {                                                                                                                   
                                                                                                                                                      
        case R.id.menu_game_hint:  
        	TsumegoHintAlert.show(this,finishing_move);
            break;                                                                                                                                
		}
		
		return false;
	}

    @Override
	public boolean doAskToKeepVariant() {
    	// we do not want to keep variants in tsumego mode
		return false;
	}
    
    @Override
	public Fragment getGameExtraFragment() {
    	//if(myTsumegoExtrasFragment==null)
    	myTsumegoExtrasFragment=new TsumegoGameExtrasFragment();

		return myTsumegoExtrasFragment;
	}

	@Override
	public void onGoGameChange() {
		if (myTsumegoExtrasFragment!=null) {
			myTsumegoExtrasFragment.setOffPathVisibility(!isOnPath());
			myTsumegoExtrasFragment.setCorrectVisibility(game.getActMove().equals(finishing_move));
		}
	}

}
