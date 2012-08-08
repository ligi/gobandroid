package org.ligi.gobandroid_hd.ui.scoring;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.ui.GoActivity;

import com.actionbarsherlock.view.Menu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.WindowManager;

/**
 * Activity to score a Game 
 * 
 * @author ligi
 * 
 */
public class GameScoringActivity extends GoActivity implements
		GoGameChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO the next line works but needs investigation - i thought more of
		// getBoard().requestFocus(); - but that was not working ..
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getBoard().show_area_stones=true;
		getGame().buildAreaGroups();

	}

	public void doTouch(MotionEvent event) {
		// calculate position on the field by position on the touchscreen
		getApp().getInteractionScope().setTouchPosition(getBoard().pixel2boardPos(
				event.getX(), event.getY()));
		if (event.getAction() == MotionEvent.ACTION_UP) {
			doMoveWithUIFeedback((byte) getApp().getInteractionScope().getTouchX(),
					(byte) getApp().getInteractionScope().getTouchY());	
			getApp().getInteractionScope().setTouchPosition(-1);
		}
	}
	
	@Override
	public byte doMoveWithUIFeedback(byte x, byte y) {
		do_score_touch(x, y);
		getGame().notifyGameChange();
		return GoGame.MOVE_VALID;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getSupportMenuInflater().inflate(R.menu.ingame_score, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onGoGameChange() {
		super.onGoGameChange();
		this.invalidateOptionsMenu();
	}

	@Override
	public void onPause() {
		super.onPause();
		// if we go back to other modes we want to have them alive again ( Zombies ?)
		for (int xg = 0; xg < getGame().getCalcBoard().getSize(); xg++)
			for (int yg = 0; yg < getGame().getCalcBoard().getSize(); yg++)
				if (getGame().getCalcBoard().isCellDead(xg, yg)) {
					getGame().getCalcBoard().toggleCellDead(xg, yg);
				}
	}

	public Fragment getGameExtraFragment() {
		return new GameScoringExtrasFragment();
	}
	
	
	public void do_score_touch(byte x,byte y) {
		
		getGame().buildGroups();
		
		if ((!getGame().getCalcBoard().isCellFree(x, y)) || getGame().getCalcBoard().isCellDead(x, y)) // if
																			// there
																			// is
																			// a
																			// stone/group
			for (byte xg = 0; xg < getGame().getCalcBoard().getSize(); xg++)
				// toggle the whole group dead TODO: should better be done
				// via flood-fill than group compare
				for (byte yg = 0; yg < getGame().getCalcBoard().getSize(); yg++)
					if (getGame().getGroup(xg,yg) == getGame().getGroup(x,y))
						getGame().getCalcBoard().toggleCellDead(xg, yg);

		getGame().buildAreaGroups();

		int _dead_white = 0;
		int _dead_black = 0;

		for (int xg = 0; xg < getGame().getCalcBoard().getSize(); xg++)
			for (int yg = 0; yg < getGame().getCalcBoard().getSize(); yg++)
				if (getGame().getCalcBoard().isCellDead(xg, yg)) {
					if (getGame().getCalcBoard().isCellDeadBlack(xg, yg))
						_dead_black++;

					if (getGame().getCalcBoard().isCellDeadWhite(xg, yg))
						_dead_white++;
				}
		
		getGame().setDeadWhite(_dead_white);
		getGame().setDeadBlack(_dead_black);
	}

}
