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

package org.ligi.gobandroid_hd.ui;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GnuGoMover;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGameProvider;
import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.ingame_common.SwitchModeHelper;
import org.ligi.tracedroid.logging.Log;

/**
 * Activity to load a SGF with a ProgressDialog showing the Progress
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 * License: This software is licensed with GPLv3
 * 
 **/

public class SGFLoadActivity 
		extends GobandroidFragmentActivity 
		implements Runnable, SGFHelper.ISGFLoadProgressCallback
{

	private GoGame game=null;
	private Uri intent_uri;
	private String sgf;
	private ProgressBar progress;
	private int act_progress;
	private int max_progress;
	private Handler handler=new Handler();
	private AlertDialog alert_dlg;
	private TextView message_tv;
	private String act_message;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		GoPrefs.init(this);	

		progress=new ProgressBar(this,null, android.R.attr.progressBarStyleHorizontal);
		progress.setMax(100);
		progress.setProgress(10);

		LinearLayout lin =new LinearLayout(this);
		
		ImageView img=new ImageView(this);
		img.setImageResource(R.drawable.icon);
		img.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		lin.setOrientation(LinearLayout.VERTICAL);
		
		lin.addView(img);
		
		FrameLayout frame=new FrameLayout(this);
		frame.addView(progress);
		message_tv=new TextView(this);
		message_tv.setText("starting");
		message_tv.setTextColor(0xFF000000);
		message_tv.setPadding(7, 0, 0, 0);
		frame.addView(message_tv);
		
		lin.addView(frame);
		
		alert_dlg=new AlertDialog.Builder(this).setCancelable(false).setTitle("Loading SGF").setView(lin).show();

		getTracker().trackPageView("/load/"+ getIntent().getData().toString().replace(":", "").replace("///","/"));
		getTracker().trackEvent("SGFLoadActivity", "load", getIntent().getData().toString(), -1);
		new Thread(this).start();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void run() {
		Looper.prepare();
		String src="";
		if (game==null) {
			/* if there is a game saved in LastNonConfigurationInstance
			 * e.g. on rotation -> use the game from there */
			if (getLastNonConfigurationInstance()!=null) 
				game=(GoGame)getLastNonConfigurationInstance();
			else { // otherwise create a new game based on the Intent Data
				
				intent_uri=getIntent().getData(); // extract the uri from the intent
				
				if (intent_uri!=null) {
					
					try {
						
						InputStream in;
						Log.i("load" + intent_uri);
						if (intent_uri.toString().startsWith("content://"))
							in = getContentResolver().openInputStream(intent_uri);	
						else
							in= new BufferedInputStream(new URL(""+intent_uri).openStream(), 4096); 
						
						FileOutputStream file_writer =null;

						// if it comes from network
						if (intent_uri.toString().startsWith("http")) { // https catched also
					    	new File(GoPrefs.getSGFPath()+"/downloads").mkdirs();
					    	File f = new File(GoPrefs.getSGFPath()+"/downloads/"+intent_uri.getLastPathSegment()	);
							f.createNewFile();
							file_writer = new FileOutputStream(f);
							}
					
						src=intent_uri.toString();
						
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
						
						Log.i("got sgf content:" + sgf);
						game=SGFHelper.sgf2game(sgf,this);
						
						if (!src.startsWith("file://")) // educated guess on what the user wants ;-9 - file:// means we come from intern gobandroid
							GoInteractionProvider.setMode(GoInteractionProvider.MODE_REVIEW);
					} catch (Exception e) {
						Log.w("exception in load", e);
						
						handler.post(new Runnable() {
							
							@Override
							/** if the sgf loading fails - give the user the option to send this SGF to me - to perhaps fix the 
							 * parser to load more SGF's - TODO remove this block if all SGF's load fine ;-) */
							public void run() {
						
								
								alert_dlg.hide();
								new AlertDialog.Builder(SGFLoadActivity.this).setTitle(R.string.results)
								.setMessage(
										 "Problem Loading sgf would you like to send ligi this sgf to fix the problem?"
								).setPositiveButton(R.string.yes,  new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
									final  Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
									emailIntent .setType("plain/text");
									emailIntent .putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"ligi@ligi.de"});
									emailIntent .putExtra(android.content.Intent.EXTRA_SUBJECT, "SGF Problem");
									emailIntent .putExtra(android.content.Intent.EXTRA_TEXT, "uri: " + intent_uri + "sgf:\n" + sgf + "err:" + Log.getCachedLog());
									SGFLoadActivity.this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
									finish();
									}
									}).setNegativeButton(R.string.no,  new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									finish();
								}
								}).show();
							}}
						);
						
						
					
						return;
					}
					
				}
				else {
					byte size = getIntent().getByteExtra("size", (byte) 9);
					byte handicap = getIntent().getByteExtra("handicap", (byte) 0);
		
					int white_player=getIntent().getIntExtra("white_player", 0);
					int black_player=getIntent().getIntExtra("black_player", 0);
					
					game = new GoGame(size,handicap);
					
					game.setGoMover(new GnuGoMover(this,game,black_player!=0,white_player!=0,GoPrefs.getAILevel()));
				}
			}
		
		}
		int move_num = getIntent().getIntExtra("move_num", -1 );
		
		if (move_num!=-1)
			for (int i=0;i<move_num;i++)
				game.jump(game.getActMove().getnextMove(0));
		
		GoGameProvider.setGame(game);
		game.getMetaData().setFileName(src);
		
		handler.post(new Runnable() {
			@Override
			public void run() {
				alert_dlg.hide();
				finish();
			}}
		);

		SwitchModeHelper.startGameWithCorrectMode(this);
	}
	
	
	@Override
	public void progress(int act, int max, String Message) {
		act_progress=act;
		max_progress=max;
		act_message=Message;
		
		handler.post(new Runnable() {

			@Override
			public void run() {
				progress.setProgress(act_progress);
				progress.setMax(max_progress);
				message_tv.setText(act_message);
			}});
	}
}