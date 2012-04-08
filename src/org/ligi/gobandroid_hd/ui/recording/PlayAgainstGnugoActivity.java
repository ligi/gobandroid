package org.ligi.gobandroid_hd.ui.recording;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GnuGoMover;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.tracedroid.logging.Log;

import com.actionbarsherlock.view.Menu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

public class PlayAgainstGnugoActivity extends GoActivity  implements GoGameChangeListener {

	private Handler myHandler;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO the next line works but needs investigation - i thought more of getBoard().requestFocus(); - but that was not working ..
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		game.setGoMover(new GnuGoMover(this,game,false,true,(byte)10));
		myHandler=new Handler();
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

	 private class RunnableOnGoGameChange implements Runnable {

		@Override
		public void run() {
			if (game.getGoMover().getProblemString()!=null)
				new AlertDialog.Builder(PlayAgainstGnugoActivity.this)
				.setTitle("GnuGo Problem")
				.setMessage("Sorry, there was a problem with gnugo - would you like to send ligi some infos to help fix the problem?")
				.setPositiveButton(R.string.yes,  new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
									final  Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
									emailIntent .setType("plain/text");
									emailIntent .putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"ligi@ligi.de"});
									emailIntent .putExtra(android.content.Intent.EXTRA_SUBJECT, "GnuGo Problem");
									emailIntent .putExtra(android.content.Intent.EXTRA_TEXT, "cmd: " +game.getGoMover().getProblemString() + "err:" + Log.getCachedLog());
									startActivity(Intent.createChooser(emailIntent, "Send mail..."));
									finish();
									}
									})
				.setNegativeButton(R.string.no,  new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									finish();
								}
								})
				
				.show();
			PlayAgainstGnugoActivity.this.invalidateOptionsMenu();		
		}
		
	}
	@Override
	public void onGoGameChange() {
		super.onGoGameChange();
		myHandler.post(new RunnableOnGoGameChange());
	}

	public Fragment getGameExtraFragment() {
		return new RecordingGameExtrasFragment();
	}
	
}
