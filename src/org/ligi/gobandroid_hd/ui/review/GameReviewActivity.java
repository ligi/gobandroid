package org.ligi.gobandroid_hd.ui.review;

import org.ligi.gobandroid_beta.R;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.alerts.GameForwardAlert;
import org.ligi.gobandroid_hd.ui.fragments.NavigationAndCommentFragment;
import org.ligi.gobandroid_hd.ui.fragments.ZoomGameExtrasFragment;
import org.ligi.gobandroid_hd.ui.ingame_common.SwitchModeHelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.actionbarsherlock.view.Menu;

public class GameReviewActivity extends GoActivity {

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

			case KeyEvent.KEYCODE_BOOKMARK:
				new BookmarkDialog(this).show();
				return true;

			case KeyEvent.KEYCODE_MEDIA_PLAY:
				SwitchModeHelper.startGame(this, InteractionScope.MODE_TELEVIZE);
				return true;

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

	@Override
	public void doTouch(MotionEvent event) {
		// super.doTouch(event); - Do not call! Not needed and breaks marking
		// dead stones

		getApp().getInteractionScope().setTouchPosition(getBoard().pixel2boardPos(event.getX(), event.getY()));

		if (event.getAction() == MotionEvent.ACTION_UP)
			setFragment(getGameExtraFragment());
		else if (event.getAction() == MotionEvent.ACTION_DOWN)
			setFragment(getZoomFragment());

		refreshZoomFragment();

		// calculate position on the field by position on the touchscreen

		if (event.getAction() == MotionEvent.ACTION_UP) {
			doMoveWithUIFeedback((byte) getApp().getInteractionScope().getTouchX(), (byte) getApp().getInteractionScope().getTouchY());
			getApp().getInteractionScope().setTouchPosition(-1);
		}

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getSupportMenuInflater().inflate(R.menu.ingame_review, menu);
		return super.onCreateOptionsMenu(menu);
	}

}
