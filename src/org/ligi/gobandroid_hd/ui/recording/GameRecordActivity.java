package org.ligi.gobandroid_hd.ui.recording;

import java.util.Vector;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.logic.GoMove;
import org.ligi.gobandroid_hd.ui.GoActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;

public class GameRecordActivity extends GoActivity  implements GoGameChangeListener {

	private Vector<GoMove> on_path_moves;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getBoard().requestFocus();
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
		super.onPrepareOptionsMenu(menu);

		menu.findItem(R.id.menu_game_pass).setVisible(!game.isFinished());
		menu.findItem(R.id.menu_game_results).setVisible(game.isFinished());
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.ingame_record, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onGoGameChange() {
		this.invalidateOptionsMenu();		
	}

	public Fragment getGameExtraFragment() {
		return new RecordingGameExtrasFragment();
	}
	
}
