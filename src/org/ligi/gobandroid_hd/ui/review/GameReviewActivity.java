package org.ligi.gobandroid_hd.ui.review;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGameProvider;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.NavigationAndCommentFragment;
import org.ligi.tracedroid.logging.Log;

import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;

public class GameReviewActivity extends GoActivity  {

	class autoPlayRunnable implements Runnable {

		GoGame game;
		
		@Override
		public void run() {
			game=GoGameProvider.getGame();
			Log.i("gobandroid","automove start" + game.getActMove().getNextMoveVariations().size());
			while (game.getActMove().getNextMoveVariationCount()>0) {
				Log.i("gobandroid","automove move"+game.getActMove().getNextMoveVariationCount());
				game.jump(game.getActMove().getnextMove(0));
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	this.getMenuInflater().inflate(R.menu.ingame_review, menu);
		return super.onCreateOptionsMenu(menu);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch ( item.getItemId()) {
		/*
		case R.id.menu_bookmark:
			
			return true;
			*/
		case R.id.menu_autoplay:
			Log.i("gobandroid","automove init");
			
			new Thread(new autoPlayRunnable()).start();
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	public Fragment getGameExtraFragment() {
		return new NavigationAndCommentFragment();		
	}
	
	
	@Override
	public byte doMoveWithUIFeedback(byte x,byte y) {
		// we want the user not to be able to edit in review mode
		return GoGame.MOVE_VALID;
	}	 
}
