package org.ligi.gobandroid_hd.ui.recording;

import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_beta.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.ui.GoActivity;

import com.actionbarsherlock.view.Menu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

/**
 * Activity to record a Game - or play on one device
 * 
 * @author ligi
 * 
 */
public class GameRecordActivity extends GoActivity implements
		GoGameChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getApp().getInteractionScope().setMode(InteractionScope.MODE_RECORD);
		// TODO the next line works but needs investigation - i thought more of
		// getBoard().requestFocus(); - but that was not working ..
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public byte doMoveWithUIFeedback(byte x, byte y) {
		byte res = super.doMoveWithUIFeedback(x, y);
		if (res == GoGame.MOVE_VALID)
			if (getGame().getActMove().hasNextMove())
				getGame().jump(getGame().getActMove().getnextMove(0));

		getGame().notifyGameChange();
		return res;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		try {
			menu.findItem(R.id.menu_game_pass).setVisible(
					!getGame().isFinished());
			/*menu.findItem(R.id.menu_game_results).setVisible(
					getGame().isFinished());*/
			menu.findItem(R.id.menu_game_undo).setVisible(getGame().canUndo());
		} catch (NullPointerException e) {
		} // we do not care when they do not exist

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getSupportMenuInflater().inflate(R.menu.ingame_record, menu);
		return super.onCreateOptionsMenu(menu);

	}

	public boolean doAutosave() {
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public void onGoGameChange() {
		super.onGoGameChange();
		this.invalidateOptionsMenu();
	}

	@Override
	public Fragment getGameExtraFragment() {
		return new RecordingGameExtrasFragment();
	}

}
