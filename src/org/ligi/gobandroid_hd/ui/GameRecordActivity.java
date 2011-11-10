package org.ligi.gobandroid_hd.ui;

import java.util.Vector;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.logic.GoMove;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.tracedroid.logging.Log;

import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;

public class GameRecordActivity extends GoActivity  implements GoGameChangeListener {

	private Vector<GoMove> on_path_moves;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.i("prep opt" + game.isFinished());
		// TODO Auto-generated method stub
		menu.clear();
		if (game.isFinished())
			this.getMenuInflater().inflate(R.menu.ingame_record_end, menu);
		else
			this.getMenuInflater().inflate(R.menu.ingame_record_pass, menu);
		
		this.getMenuInflater().inflate(R.menu.ingame_record, menu);
		this.getMenuInflater().inflate(R.menu.ingame_common, menu);
		return true;
	}

	@Override
	public void onGoGameChange() {
		//this.invalidateOptionsMenu();		
	}

}
