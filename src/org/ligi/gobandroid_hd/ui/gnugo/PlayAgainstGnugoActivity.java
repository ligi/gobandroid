package org.ligi.gobandroid_hd.ui.gnugo;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GTPHelper;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.GoPrefs;
import org.ligi.gobandroid_hd.ui.UndoWithVariationDialog;
import org.ligi.gobandroid_hd.ui.recording.RecordingGameExtrasFragment;
import org.ligi.gobandroidhd.ai.gnugo.IGnuGoService;
import org.ligi.tracedroid.logging.Log;
import com.actionbarsherlock.view.Menu;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import android.widget.Toast;

public class PlayAgainstGnugoActivity extends GoActivity  implements GoGameChangeListener,Runnable {

	private IGnuGoService gnu_service ;
	private ServiceConnection conn;
	private Handler myHandler;
	
	private boolean playing_black=false;
	private boolean playing_white=false;
	private byte level;
	
	private GnuGoSetupDialog dlg;
	
	public final static String INTENT_ACTION="org.ligi.gobandroidhd.ai.gnugo.GnuGoService";
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO the next line works but needs investigation - i thought more of getBoard().requestFocus(); - but that was not working ..
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		//game.setGoMover(new GnuGoMover(this,game,false,true,(byte)10));
		
		getTracker().trackPageView("/gnugo/play");
		
		dlg=new GnuGoSetupDialog(this);
		
		dlg.setPositiveButton(R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				playing_black=dlg.isBlackActive() | dlg.isBothActive();
				playing_white=dlg.isWhiteActive() | dlg.isBothActive();
				level=(byte)dlg.getStrength();
				dialog.dismiss();
			}
			
		});

		dlg.setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
			
		});
		dlg.show();
		
		myHandler=new Handler();
    }		
    
	@Override
	protected void onResume() {
		Log.i("GnuGoDebug onResume");
		conn = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				gnu_service = IGnuGoService.Stub.asInterface(service);

				try {
					Log.i("Service bound " + gnu_service.processGTP("test"));
				} catch (RemoteException e) {
				}
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.i("Service unbound ");
			}
		};

		getApplication().bindService(new Intent(INTENT_ACTION), conn,Context.BIND_AUTO_CREATE);
		

		new Thread(this).start();
		
		super.onStart();
	}

	@Override
	public void onPause() {
		stop();
		Log.i("GnuGoDebug onPause");
		super.onPause();
	}

	public void stop() {
		if (gnu_service==null)
			return;
		gnu_service=null;
		Log.i("GnuGoDebug stopping");
		try {
			getApplication().unbindService(conn);
			getApplication().stopService(new Intent(INTENT_ACTION));
		} catch (Exception e) { }
		conn=null;
	
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
	
	@Override
	public byte doMoveWithUIFeedback(byte x, byte y) {
		if ((game.isBlackToMove() && (!playing_black)))
			processMove("black",x,y);
		else if (((!game.isBlackToMove()) && (!playing_white)))
			processMove("white",x,y);
		
		return super.doMoveWithUIFeedback(x, y);
	}

	public void processMove(String color,byte x,byte y)   {
		try {
			gnu_service.processGTP(color+" " + coordinates2gtpstr(x,y));
		} catch (Exception e) {
			Log.w("problem processing " + color + " move to " + coordinates2gtpstr(x,y));
		}
	}
	
	private boolean gnugo_size_set=false;
	
	@Override
	public void run() {
		Log.i("GnuGoDebug startthread " + conn);
		while ((!game.isFinished())&&(conn!=null)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// blocker for the following steps
			if ((gnu_service==null)||game.isFinished()||(conn==null))
				continue;
			
			if (!gnugo_size_set)
				try {
					// set the size
					gnu_service.processGTP("boardsize " + game.getBoardSize());
									
					for (byte x=0;x<game.getBoardSize();x++)
						for (byte y=0;y<game.getBoardSize();y++)
							if (game.getVisualBoard().isCellBlack(x, y))
								gnu_service.processGTP("black " + coordinates2gtpstr(x,y));
							else if (game.getVisualBoard().isCellWhite(x, y))
								gnu_service.processGTP("white " + coordinates2gtpstr(x,y));
					
					Log.i("setting level " + gnu_service.processGTP("level "+level));
					gnugo_size_set=true;
				} catch (Exception e) {}
			
				
			if (game.isBlackToMove()&&playing_black) {
				try {
					String answer= gnu_service.processGTP("genmove black");
					
					if (!GTPHelper.doMoveByGTPString(answer, game)) {
						Log.w("GnuGoProblem " + answer + " board "  + gnu_service.processGTP("showboard"));
						Log.w("restarting GnuGo " + answer);
						gnugo_size_set=false; // reset
					}
					Log.i("gugoservice" + gnu_service.processGTP("showboard"));		
				} catch (Exception e) {}
			}
			
			if ((!game.isBlackToMove())&&playing_white) {
				try {
					String answer= gnu_service.processGTP("genmove white");
					
					Log.i("gugoservice" + gnu_service.processGTP("showboard"));		
					
					if (!GTPHelper.doMoveByGTPString(answer, game)) {
						Log.w("GnuGoProblem " + answer + " board "  + gnu_service.processGTP("showboard"));
						Log.w("restarting GnuGo " + answer);
						gnugo_size_set=false; // reset
					}
					Log.i("gugoservice" + gnu_service.processGTP("showboard"));		
				} catch (Exception e) {}
			}
			
		
			
		}
		stop();
	
		Log.i("a stopthread " + conn);
	}

	private String coordinates2gtpstr(byte x,byte y)  {
		if (game==null) {
			Log.w("coordinates2gtpstr called with game==null");
			return "";
		}
		if (x>=8) x++; // "I" is missing decrease human OCR-error but increase computer bugs ... 
		y=(byte)(game.getBoardSize()-(y));
		return ""+(char)('A'+x) + ""+(y);
	}
	
	/**
	 * @return if it is a move the mover has to process
	 */
	public boolean isMoversMove() {
		return
			(game.isBlackToMove()&&(playing_black))|| (!game.isBlackToMove()&&(playing_white)) ;
	}
	
	
	@Override
	public void requestUndo() {
		if (isMoversMove()) {
			Toast.makeText(this, "Please wait for GnuGo", Toast.LENGTH_LONG).show();
			return;
		}
		
		if (game.canUndo()) {  
			game.undo(GoPrefs.isKeepVariantEnabled());
		}
		
		if (game.canUndo()) {  
			game.undo(GoPrefs.isKeepVariantEnabled());
		}
		
		try {
			Log.i("gugoservice undo 1" + gnu_service.processGTP("undo"));
			Log.i("gugoservice undo 2" + gnu_service.processGTP("undo"));
			Log.i("gugoservice board after undo" + gnu_service.processGTP("showboard"));
		} catch (RemoteException e) {
			gnugo_size_set=false; // reset
		}		

	}
}
