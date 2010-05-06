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

package org.ligi.gobandroid.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;

import org.ligi.gobandroid.R;
import org.ligi.gobandroid.logic.GnuGoMover;
import org.ligi.gobandroid.logic.GoGame;
import org.ligi.gobandroid.logic.Logger;
import org.ligi.gobandroid.logic.SGFHelper;
import org.ligi.gobandroid.ui.alerts.GameResultsAlert;

/**
 * Activity for a Game
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 * License: This software is licensed with GPLv3
 * 
 **/

public class GoActivity 
		extends Activity 
		implements OnClickListener, OnTouchListener, Runnable
{

	
	private static final int MENU_UNDO = 0;
	private static final int MENU_PASS = 1;
	private static final int MENU_FINISH = 2;
	private static final int MENU_WRITE_SGF = 3;
	private static final int MENU_SETTINGS = 4;
	private static final int MENU_SHOWCONTROLS= 5;
	private static final int MENU_GAMEINFO = 6;

	private GoGame game=null;
	private GoBoardView board_view;
	
	private WakeLock mWakeLock=null;
	
	private ImageButton next,back,first,last,comments;
	private boolean running=true;

	private boolean review_mode=false;
    
	private Toast info_toast=null;
	
	private String sgf="";
	
	private Uri intent_uri;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		GoPrefs.init(this);	
		
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		
		info_toast=Toast.makeText(this, "", Toast.LENGTH_LONG);
		
		if (game==null) {
			// if there is a game saved e.g. on rotation use this game
			if (getLastNonConfigurationInstance()!=null) 
				game=(GoGame)getLastNonConfigurationInstance();
			else {
				// otherwise create a new game
		
				intent_uri=getIntent().getData();
				
				if (intent_uri!=null) {
					
					try {
						
						InputStream in;
						Log.i("gobandroid","load" + intent_uri);
						if (intent_uri.toString().startsWith("content://"))
							in = getContentResolver().openInputStream(intent_uri);	
						else
						  in= new BufferedInputStream(new URL(""+intent_uri) 
			              .openStream(), 4096); 
						
						FileOutputStream file_writer =null;
					    if (intent_uri.toString().startsWith("http"))  
					    		{
					    		new File(GoPrefs.getSGFPath()+"/downloads").mkdirs();
					    							    		
					      		File f = new File(GoPrefs.getSGFPath()+"/downloads/"+intent_uri.getLastPathSegment()	);
								f.createNewFile();

								 file_writer = new FileOutputStream(f);
								
					    		}
						
					    StringBuffer out = new StringBuffer();
					    byte[] b = new byte[4096];
					    for (int n; (n = in.read(b)) != -1;) {
					        out.append(new String(b, 0, n));
					        if (file_writer!=null)
					        	file_writer.write(b, 0, n);
					    }
					    if (file_writer!=null)
				        	file_writer.close();
				    
					    sgf=out.toString();
						
						Log.i("gobandroid","got sgf" + sgf);
						game=SGFHelper.sgf2game(sgf);
						review_mode=true;
						
						
						
					} catch (Exception e) {
						Log.i("gobandroid","exception in load" + e);
						e.printStackTrace();
						
						
						new AlertDialog.Builder(this).setTitle(R.string.results)
						.setMessage(
								 "Problem Loading sgf would you like to send ligi this sgf to fix the problem?"
						).setPositiveButton(R.string.yes,  new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							final  Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
							emailIntent .setType("plain/text");
							emailIntent .putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"ligi@ligi.de"});
							emailIntent .putExtra(android.content.Intent.EXTRA_SUBJECT, "SGF Problem");
							emailIntent .putExtra(android.content.Intent.EXTRA_TEXT, "uri: " + intent_uri + "sgf:\n" + sgf);
							GoActivity.this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
							finish();
						}
					}).setNegativeButton(R.string.no,  new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							finish();
						}
					})
					
					.show();
						return;
					}
					
				}
				else {
					byte size = getIntent().getByteExtra("size", (byte) 9);
					byte handicap = getIntent().getByteExtra("handicap", (byte) 0);
		
					int white_player=getIntent().getIntExtra("white_player", 0);
					int black_player=getIntent().getIntExtra("black_player", 0);
					
					game = new GoGame(size,handicap);
					review_mode=false;
					
					game.setGoMover(new GnuGoMover(this,game,black_player!=0,white_player!=0,GoPrefs.getAILevel()));
				}
			}
		
			board_view = new GoBoardView(this, game);
			board_view.setOnTouchListener(this);
			
			
			RelativeLayout rel=new RelativeLayout(this);
			rel.addView(board_view);
			
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);

			Vector<ImageButton> control_buttons=new Vector<ImageButton>();
			
			
			first=new ImageButton(this);
			first.setImageResource(android.R.drawable.ic_media_previous);
			control_buttons.add(first);
			first.setOnClickListener(this);
			
			back=new ImageButton(this);
			back.setImageResource(android.R.drawable.ic_media_rew);
			control_buttons.add(back);
			back.setOnClickListener(this);
			
			comments=new ImageButton(this);
			comments.setImageResource(android.R.drawable.ic_dialog_email);
			control_buttons.add(comments);
			comments.setOnClickListener(this);
						
			next=new ImageButton(this);
			next.setImageResource(android.R.drawable.ic_media_ff);
			control_buttons.add(next);
			next.setOnClickListener(this);
			
			
			
			last=new ImageButton(this);
			last.setImageResource(android.R.drawable.ic_media_next);
			last.setOnClickListener(this);
			control_buttons.add(last);

			
			if (dm.heightPixels>dm.widthPixels)
			{
				TableLayout controls_table=new TableLayout(this);
				
				TableRow controls_row=new TableRow(this);
				controls_table.addView(controls_row);
				
				int btn_id=0;
				for (ImageButton btn:control_buttons) 
					{
					controls_row.addView(btn);
					controls_table.setColumnStretchable(btn_id++, true);
					}
				
				
				rel.addView(controls_table);
				
				RelativeLayout.LayoutParams bottom_nav_params = new
				RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
				bottom_nav_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

				controls_table.setLayoutParams(bottom_nav_params);

				rel.setGravity(Gravity.BOTTOM);
				
			}
			else
			{
				LinearLayout lin=new LinearLayout(this);
				for (ImageButton btn:control_buttons) 
					lin.addView(btn);
				lin.setOrientation(LinearLayout.VERTICAL);
				

				RelativeLayout.LayoutParams bottom_nav_params = new
				RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.FILL_PARENT);
				bottom_nav_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

				lin.setLayoutParams(bottom_nav_params);
			
				rel.addView(lin);
			}
			setContentView(rel);
			}
		
		
		updateControlsStatus();
		
	
		if (GoPrefs.getKeepLightEnabled())
			{
			final PowerManager pm = (PowerManager) (this.getSystemService(Context.POWER_SERVICE)); 
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "DUBwise Wakelog TAG");  
        	mWakeLock.acquire();
			}
		
		new Thread(this).start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		updateControlsStatus();
		return false;
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		// remember the game in case of e.g. a device rotation
		return(game);
	}
	
	public void updateControlsStatus() {
		

		int visible=0;
		if (review_mode)
			visible=View.VISIBLE;
		else
			visible=View.GONE;
			
		
		back.setVisibility(visible);
		first.setVisibility(visible);
		next.setVisibility(visible);
		last.setVisibility(visible);
		comments.setVisibility(visible);
		
		// prevent NPE
		if (game==null)
			{
			Logger.w("no game there when updateControlsStatus");	
			return;
			}
		
		back.setEnabled(game.canUndo());
		first.setEnabled(game.canUndo());
		next.setEnabled(game.canRedo());
		last.setEnabled(game.canRedo());
		comments.setEnabled(game.getActMove().hasComment());
		
	}
	

	@Override
	public void onResume() {
		super.onResume();
		
		if (game==null)
			return;
		
		Log.i("gobandroid ", " resuming go activity" + GoPrefs.getBoardSkinName());
		
		GOSkin.setBoardSkin(GoPrefs.getBoardSkinName());
		GOSkin.setStoneSkin(GoPrefs.getStoneSkinName());
		
		board_view.regenerate_stones_flag=true;
		
		if (GoPrefs.getFullscreenEnabled())
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		else
			this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	
		setCustomTitle(R.layout.top);
		((TopView)(this.findViewById(R.id.TopView))).setGame(game);
	}
	
	/**
	 *  Creates the menu items 
	 **/
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		if (!game.isFinished()) 
			{
			
			if (game.canUndo()&&(!game.getGoMover().isMoversMove())) 
				menu.add(0, MENU_UNDO, 0, R.string.undo).setIcon(android.R.drawable.ic_menu_revert);
						
			if (!game.getGoMover().isMoversMove())
				menu.add(0, MENU_PASS, 0,R.string.pass).setIcon(android.R.drawable.ic_menu_set_as); 
			}
		else 
			menu.add(0, MENU_FINISH, 0,R.string.results).setIcon(android.R.drawable.ic_menu_more);
			
		
		if ((!game.getGoMover().isPlayingInThisGame())||game.isFinished())
			menu.add(0, MENU_SHOWCONTROLS, 0,(review_mode?R.string.hide_review_controls:R.string.show_review_controls)).setIcon(android.R.drawable.ic_menu_view);

		menu.add(0, MENU_GAMEINFO, 0,"Game Info").setIcon(android.R.drawable.ic_menu_help);

		menu.add(0, MENU_WRITE_SGF, 0,R.string.save_as_sgf).setIcon(android.R.drawable.ic_menu_save);
		menu.add(0, MENU_SETTINGS, 0, R.string.settings).setIcon(android.R.drawable.ic_menu_preferences);
		
		return true;
	}

	

	
	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		
		case MENU_GAMEINFO:
			ScrollView scroll_view=new ScrollView(this);
			TableLayout table_gameinfo=new TableLayout(this);
			TableRow row_gameinfo=new TableRow(this);
			
			table_gameinfo.setColumnStretchable(1, true);
			
			EditText game_name_et=new EditText(this);
			game_name_et.setText(game.getMetaData().getName());
			game_name_et.setPadding(2, 0, 5, 0);
			TextView game_name_tv=new TextView(this);
			game_name_tv.setText("Game Name");
			
			row_gameinfo.addView(game_name_tv);
			row_gameinfo.addView(game_name_et);
			
			table_gameinfo.addView(row_gameinfo);

			TableRow row_black_name=new TableRow(this);
			EditText black_name_et=new EditText(this);
			black_name_et.setText(game.getMetaData().getBlackName());
			black_name_et.setPadding(2, 0, 5, 0);
			TextView black_name_tv=new TextView(this);
			black_name_tv.setPadding(2, 0, 5, 0);
			black_name_tv.setText("Black Name");
			
			row_black_name.addView(black_name_tv);
			row_black_name.addView(black_name_et);
			
			table_gameinfo.addView(row_black_name);

			TableRow row_black_rank=new TableRow(this);
			EditText black_rank_et=new EditText(this);
			black_rank_et.setText(game.getMetaData().getBlackRank());
			black_rank_et.setPadding(2, 0, 5, 0);
			TextView black_rank_tv=new TextView(this);
			black_rank_tv.setPadding(2, 0, 5, 0);
			black_rank_tv.setText("Black Rank");
			
			row_black_rank.addView(black_rank_tv);
			row_black_rank.addView(black_rank_et);
			
			table_gameinfo.addView(row_black_rank);

			TableRow row_white_name=new TableRow(this);
			EditText white_name_et=new EditText(this);
			white_name_et.setText(game.getMetaData().getWhiteName());
			white_name_et.setPadding(2, 0, 5, 0);
			TextView white_name_tv=new TextView(this);
			white_name_tv.setPadding(2, 0, 5, 0);
			white_name_tv.setText("White Name");
			
			row_white_name.addView(white_name_tv);
			row_white_name.addView(white_name_et);
			
			table_gameinfo.addView(row_white_name);

			TableRow row_white_rank=new TableRow(this);
			EditText white_rank_et=new EditText(this);
			white_rank_et.setText(game.getMetaData().getWhiteRank());
			white_rank_et.setPadding(2, 0, 5, 0);
			TextView white_rank_tv=new TextView(this);
			white_rank_tv.setPadding(2, 0, 5, 0);
			white_rank_tv.setText("White Rank");
			
			row_white_rank.addView(white_rank_tv);
			row_white_rank.addView(white_rank_et);
			
			table_gameinfo.addView(row_white_rank);

			TableRow game_result_row=new TableRow(this);
			EditText game_result_et=new EditText(this);
			game_result_et.setText(game.getMetaData().getResult());
			game_result_et.setPadding(2, 0, 5, 0);
			TextView game_result_tv=new TextView(this);
			game_result_tv.setPadding(2, 0, 5, 0);
			game_result_tv.setText("Game Result");
			
			game_result_row.addView(game_result_tv);
			game_result_row.addView(game_result_et);
			
			table_gameinfo.addView(game_result_row);

			
			scroll_view.addView(table_gameinfo);
			
			new AlertDialog.Builder(this).setTitle("Game Info").setView(scroll_view)
			.setMessage(""
					 
			).setPositiveButton(R.string.ok,  new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
			}
			}).show();
			
			break;
			
		case MENU_SHOWCONTROLS:
			review_mode=!review_mode;
			
			break;
		case MENU_FINISH:
			GameResultsAlert.show(this, game);
			break;
				
		case MENU_UNDO:
			game.getGoMover().paused=true;
			game.undo();
			
			if (game.canUndo()&&(game.getGoMover().isMoversMove()))
				game.undo();
			
			game.getGoMover().paused=false;
			
			break;

		case MENU_PASS:
			game.pass();
			break;

		case MENU_WRITE_SGF:
			final EditText input = new EditText(this);   
			input.setText(GoPrefs.getSGFFname());

			new AlertDialog.Builder(this).setTitle(R.string.save_sgf).setMessage("How should the file I will write to " +GoPrefs.getSGFPath() + " be named?").setView(input)
			.setPositiveButton(R.string.ok , new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString(); 
					
				File f = new File(GoPrefs.getSGFPath());
				
				if (!f.isDirectory())
					f.mkdirs();
				
				try {
					f=new File(GoPrefs.getSGFPath() + "/"+value+".sgf");
					f.createNewFile();
					
					FileWriter sgf_writer = new FileWriter(f);
					
					BufferedWriter out = new BufferedWriter(sgf_writer);
					
					out.write(SGFHelper.game2sgf(game));
					out.close();
					sgf_writer.close();
				} catch (IOException e) {
					Logger.i(""+e);
				}
	
			}
			}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			// Do nothing.
			}
			}).show();
						break;

		case MENU_SETTINGS:
             startActivity(new Intent(this,GoPrefsActivity.class));
             break;
		}
		 updateControlsStatus() ;
		board_view.invalidate();
		return false;
	}

	private void setCustomTitle(int view_id) {
		try {
		// retrieve value for
		//com.android.internal.R.id.title_container
		int titleContainerId = (Integer) Class.forName(
		"com.android.internal.R$id").getField
		("title_container").get(null);

		// remove all views from titleContainer
		((ViewGroup) this.getWindow().findViewById
				(titleContainerId)).removeAllViews();

		// add new custom title view
		((ViewGroup) this.getWindow().findViewById
					(titleContainerId)).setVisibility(View.VISIBLE	);
			this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, view_id);
		
		} catch(Exception ex) {}
		
		
	} // end of setCustomTitle

    
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	
    	switch (keyCode) {
    	case KeyEvent.KEYCODE_DPAD_UP:
    		board_view.prepare_keyinput();
    		if (board_view.touch_y>0) board_view.touch_y--;
    		break;
    	
    	case KeyEvent.KEYCODE_DPAD_LEFT:
    		board_view.prepare_keyinput();
    		if (board_view.touch_x>0) board_view.touch_x--;
    		break;
    	
    	case KeyEvent.KEYCODE_DPAD_DOWN:
    		board_view.prepare_keyinput();
    		if (board_view.touch_y<game.getVisualBoard().getSize()-1) board_view.touch_y++;
    		break;
    	
    	case KeyEvent.KEYCODE_DPAD_RIGHT:
    		board_view.prepare_keyinput();
    		if (board_view.touch_x<game.getVisualBoard().getSize()-1) board_view.touch_x++;
    		break;
    		
    	case KeyEvent.KEYCODE_DPAD_CENTER:
    		game.do_move(board_view.touch_x,board_view.touch_y);
    		board_view.setZoom(false);
    		break;
    		
    	case KeyEvent.KEYCODE_BACK:
    		if (board_view.isZoomed())
    			board_view.setZoom(false);
    		else
    		{
    			new AlertDialog.Builder(this).setTitle(R.string.end_game_quesstion_title)
    			.setMessage( R.string.quit_confirm
    			).setPositiveButton(R.string.yes,  new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				game.getGoMover().stop();
    				finish();
    			}
    		}).setCancelable(true).setNegativeButton(R.string.no,  new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				
    			}
    		}).show();
    				
    		}
    		return true;
    		
    	}
    	updateControlsStatus();
    	board_view.invalidate();
    	return super.onKeyDown(keyCode, event);	
    }
	
    @Override 
    public void onDestroy() {
    	super.onDestroy();
    	if (mWakeLock!=null)
    		mWakeLock.release();
    }

	@Override
	public void onClick(View btn) {
		if (btn==back)
			game.undo();
		else if (btn==next) {
						
			if (game.getPossibleVariationCount()>0)
				{
				LinearLayout lin=new LinearLayout(this);
				LinearLayout li=new LinearLayout(this);

				TextView txt =new TextView(this);
		
				// show the comment when there is one - useful for SGF game problems
				if (game.getActMove().hasComment())
					txt.setText(game.getActMove().getComment());
				else
					txt.setText("" +( game.getPossibleVariationCount()+1) + " Variations found for this move - which should we take?");
			
				txt.setPadding(10, 2, 10, 23);
				lin.addView(txt);
				lin.addView(li);
				lin.setOrientation(LinearLayout.VERTICAL);
				
				final Dialog select_dlg=new Dialog(this);
				final Boolean redoing=false;
				View.OnClickListener var_select_listener=new View.OnClickListener() {
					
					
					@Override
					public void onClick(View v) {
						if (redoing)
							return;
						select_dlg.hide();
						if (!v.isEnabled()) return;
						v.setEnabled(false	);
						
						game.redo((Integer)(v.getTag()));
					
						updateControlsStatus();
						board_view.invalidate();
					}
				};
				
				li.setWeightSum(1.0f*(game.getPossibleVariationCount()+1));
				li.setLayoutParams(new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				
				for (Integer i=0;i<game.getPossibleVariationCount()+1;i++)
					{
					Button var_btn=new Button(this);
					var_btn.setTag(i);
					var_btn.setOnClickListener(var_select_listener );
					if (game.getActMove().getnextMove(i).isMarked())
						var_btn.setText(game.getActMove().getnextMove(i).getMarkText());
					else
						var_btn.setText(""+(i+1));
						
					li.addView(var_btn);
			
					var_btn.setLayoutParams(new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
					}

				select_dlg.setTitle(R.string.variations);
				select_dlg.setContentView(lin);
				
				select_dlg.show();
			}
			else
				game.redo(0);
			
			
		}
		else if (btn==first)
			game.jumpFirst();
		else if (btn==last)
			game.jumpLast();
		else if (btn==comments) {
	
			new AlertDialog.Builder(this).setTitle(R.string.comments)
			.setMessage(
					 game.getActMove().getComment()
		).setPositiveButton(R.string.ok,  new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			
			}
		}).show();
		}
		
		updateControlsStatus();
		board_view.invalidate();
	}

	/**
	 * show a the info toast with a specified text
	 * 
	 * @param resId
	 */
	public void showInfoToast(int resId) {
		info_toast.setText(resId);
		info_toast.show();
	}
	
	public boolean onTouch( View v, MotionEvent event ) {
		
		if (!game.getGoMover().isReady())
			showInfoToast(R.string.wait_gnugo);
		else if (game.getGoMover().isMoversMove())
			showInfoToast(R.string.not_your_turn);
		else
			board_view.doTouch(event);
		
    	updateControlsStatus();
    	return true;
    }
 
    
    @Override 
    protected void onRestoreInstanceState(Bundle savedInstanceState) { 
      super.onRestoreInstanceState(savedInstanceState); 
      review_mode=savedInstanceState.getBoolean("review_mode");
      updateControlsStatus();
    } 
    
    
    @Override 
    protected void onSaveInstanceState(Bundle outState) { 
      outState.putBoolean("review_mode", review_mode);
      super.onSaveInstanceState(outState); 
    }

	@Override
	public void run() {
		Looper.prepare();
		while (running) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
					
			class UpdateViewClass implements Runnable {
				@Override
				public void run() {
					board_view.invalidate();		
				}
			}
			this.runOnUiThread(new UpdateViewClass());

		}
	} 
    
	
}