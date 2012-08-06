package org.ligi.gobandroid_hd.ui.review;

import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.alerts.GameForwardAlert;
import org.ligi.gobandroid_hd.ui.fragments.NavigationAndCommentFragment;
import org.ligi.gobandroid_hd.ui.fragments.ZoomGameExtrasFragment;
import com.actionbarsherlock.view.MenuItem;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class GameReviewActivity extends GoActivity {

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		}
		return super.onOptionsItemSelected(item);
	}

	public Fragment getGameExtraFragment() {
		return new NavigationAndCommentFragment();
	}

	@Override
	public byte doMoveWithUIFeedback(byte x, byte y) {
		// we want the user not to be able to edit in review mode
		return GoGame.MOVE_VALID;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getBoard().setOnKeyListener(this);
		getBoard().do_actpos_highlight = false;

	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN)
			switch (keyCode) {
			case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if (!getGame().canUndo())
					return true;
				getGame().undo();
				return true;

			case KeyEvent.KEYCODE_DPAD_RIGHT:
			case KeyEvent.KEYCODE_MEDIA_NEXT:
				GameForwardAlert.show(this, getGame());
				return true;

			case KeyEvent.KEYCODE_DPAD_UP:
			case KeyEvent.KEYCODE_DPAD_DOWN:
				return false;

			}
		return super.onKey(v, keyCode, event);
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP)
			setFragment(getGameExtraFragment());
		else if (event.getAction() == MotionEvent.ACTION_DOWN)
			setFragment(getZoomFragment());

		doTouch(event);
		return true;
	}

	@Override
	public ZoomGameExtrasFragment getZoomFragment() {
		if (myZoomFragment == null)
			myZoomFragment = new ZoomGameExtrasFragment(false);
		return myZoomFragment;
	}

	@Override
	public void quit(final boolean toHome) {
		new EndReviewDialog(this).show();
	}

	@Override
	public void initializeStoneMove() {		
		// we do not want this behaviour so we override and do nothing 
	}
}
