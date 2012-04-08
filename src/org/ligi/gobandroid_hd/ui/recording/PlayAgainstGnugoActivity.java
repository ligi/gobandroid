package org.ligi.gobandroid_hd.ui.recording;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GnuGoMover;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.ui.GoActivity;

import com.actionbarsherlock.view.Menu;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

public class PlayAgainstGnugoActivity extends GoActivity  implements GoGameChangeListener {

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO the next line works but needs investigation - i thought more of getBoard().requestFocus(); - but that was not working ..
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		game.setGoMover(new GnuGoMover(this,game,false,true,(byte)10));
    }		
    
	public byte doMoveWithUIFeedback(byte x,byte y) {
		
		byte res=super.doMoveWithUIFeedback(x,y);
		if (res==GoGame.MOVE_VALID)
			if (game.getActMove().hasNextMove())
				game.jump(game.getActMove().getnextMove(0));
			

		game.notifyGameChange();
		return res;
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
		this.getSupportMenuInflater().inflate(R.menu.ingame_record, menu);
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
