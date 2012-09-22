/**
 * gobandroid 
 * by Marcus -Ligi- Bueschleb 
 * http://ligi.de
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as 
 * published by the Free Software Foundation; 
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 **/

package org.ligi.gobandroid_hd.ui.game_setup;

import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.recording.GameRecordActivity;
import org.ligi.tracedroid.logging.Log;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.WindowManager;

/**
 * Activity for setting up a game ( board size / handicap / .. )
 * 
 * TODO needs cleaning
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 *         This software is licensed with GPLv3
 * 
 **/

public class GoSetupActivity extends GoActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getApp().getInteractionScope().setMode(InteractionScope.MODE_SETUP);
		// TODO the next line works but needs investigation - i thought more of
		// getBoard().requestFocus(); - but that was not working ..
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Log.i("starting setup");
	}
	
	
	@Override
	public Fragment getGameExtraFragment() {
		return new GameSetupFragment();
	}
	

	@Override
	public byte doMoveWithUIFeedback(byte x, byte y) {
		byte res = super.doMoveWithUIFeedback(x, y);
		if (res == GoGame.MOVE_VALID)
			if (getGame().getActMove().hasNextMove())
				getGame().jump(getGame().getActMove().getnextMove(0));

		getGame().notifyGameChange();
		Log.i(getGame().getVisualBoard().toString());
		
		this.startActivity(new Intent(this, GameRecordActivity.class));
		finish();
		return res;
	}
}