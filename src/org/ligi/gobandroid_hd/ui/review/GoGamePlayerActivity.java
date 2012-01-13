package org.ligi.gobandroid_hd.ui.review;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGameProvider;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.GobanDroidTVActivity;
import org.ligi.gobandroid_hd.ui.NavigationAndCommentFragment;
import org.ligi.gobandroid_hd.ui.alerts.GameForwardAlert;
import org.ligi.tracedroid.logging.Log;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.KeyEvent;
import android.view.View;

public class GoGamePlayerActivity extends GoActivity  {

	private boolean autoplay_active=true;
	
	// timings in ms
	private int pause_for_last_move=23000;
	private int pause_between_moves=2300;
	private int pause_betwen_moves_extra_per_word=500;
	
	class autoPlayRunnable implements Runnable {

		private GoGame game;
		 
		@Override
		public void run() {
			game=GoGameProvider.getGame();
			Log.i("gobandroid","automove start" + game.getActMove().getNextMoveVariations().size());
			while (autoplay_active &&( game.getActMove().getNextMoveVariations().size()>0)) {
				Log.i("gobandroid","automove move"+game.getActMove().getNextMoveVariationCount());
				game.jump(game.getActMove().getnextMove(0));
				try {
					Thread.sleep(calcTime());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			try {
				Thread.sleep( pause_for_last_move );
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Intent next_intent=new Intent(GoGamePlayerActivity.this,GobanDroidTVActivity.class);
			GoGamePlayerActivity.this.startActivity(next_intent);
			
			GoGamePlayerActivity.this.finish();
		}
		
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	this.getMenuInflater().inflate(R.menu.ingame_review, menu);
    	
    	
    	
    	menu.findItem(R.id.menu_autoplay).setTitle(autoplay_active?"autoplay off":"autoplay on");
		return super.onCreateOptionsMenu(menu);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch ( item.getItemId()) {
		case R.id.menu_bookmark:
			BookmarkDialog.show(this);
			return true;

		case R.id.menu_autoplay:
			Log.i("gobandroid","automove init");
	
			if (autoplay_active) {
				autoplay_active=false;
			} else {
				autoplay_active=true;
				new Thread(new autoPlayRunnable()).start();;
			}
			this.invalidateOptionsMenu();
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onStop() {
		autoplay_active=false;
		super.onStop();
	}


	public Fragment getGameExtraFragment() {
		return new NavigationAndCommentFragment();		
	}
	
	
	@Override
	public byte doMoveWithUIFeedback(byte x,byte y) {
		// we want the user not to be able to edit in review mode
		return GoGame.MOVE_VALID;
	}	 
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getBoard().setOnKeyListener(this);
		getBoard().do_mark_act=false;
		
		if (autoplay_active)
			new Thread(new autoPlayRunnable()).start();
		
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
	    	
		if (event.getAction()==KeyEvent.ACTION_DOWN)
	    	switch (keyCode) {
	    	case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
	    	case KeyEvent.KEYCODE_DPAD_LEFT:
	    		if (!game.canUndo())
	    			return true;
	    		game.undo();
	    		return true;
	    		
	    	case KeyEvent.KEYCODE_DPAD_RIGHT:
	    	case KeyEvent.KEYCODE_MEDIA_NEXT:
	    		GameForwardAlert.show(this, game);
	    		return true;
	    		
	    	case KeyEvent.KEYCODE_DPAD_UP:
	    	case KeyEvent.KEYCODE_DPAD_DOWN:
	    		return false;

	    	}
	    	return super.onKey(v,keyCode, event);
	 }

	@Override
	public boolean isAsk4QuitEnabled() {
		return false;
	}
	
	public int countWords(String sentence) {
		int words=0;
		for (int i=0;i<sentence.length();i++)
			if (sentence.charAt(i)==' ')
				words++;

		return words;
	}
	
	public int calcTime() {
		int res=pause_between_moves;
		res+=pause_betwen_moves_extra_per_word*countWords(game.getActMove().getComment());
		return res;
	}
}
