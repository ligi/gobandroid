package org.ligi.gobandroid_hd.ui.recording;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGameMetadata;
import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.tracedroid.logging.Log;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Dialog to save a game to SGF file and ask the user about how in here
 * 
 * TODO check if file exists
 * 
 * @author ligi
 *
 */
public class SaveSGFDialog extends GobandroidDialog {

	public SaveSGFDialog(final GobandroidFragmentActivity context) {
		super(context);
		setContentView(R.layout.save_sgf_dialog);
		
		setIconResource(R.drawable.save);
		TextView intro_text=(TextView)findViewById(R.id.intro_txt);
		intro_text.setText(String.format(context.getResources().getString(R.string.save_sgf_question), context.getSettings().getSGFSavePath()));
		
		final EditText input = (EditText)findViewById(R.id.sgf_name_edittext);
		
		String old_fname=getApp().getGame().getMetaData().getFileName();
		
		if ((old_fname!=null)&&(!old_fname.equals(""))) {
			input.setText(old_fname.replace(".sgf",""));
		}
			
		
		final CheckBox share_checkbox=(CheckBox)findViewById(R.id.share_checkbox);
		final GoGameMetadata game_meta=getApp().getGame().getMetaData();

		/**
		 * this is a OnClickListener  to add Stuff to the FileName like date/gamename/...
		 */
		class FileNameAdder implements View.OnClickListener {
			
			private String getTextByButtonId(int btn_resId) {
				switch (btn_resId) {
				case R.id.button_add_date:
					SimpleDateFormat date_formatter = new SimpleDateFormat("yyyy.MM.dd");
					return date_formatter.format(new Date());
				case R.id.button_add_time:
					SimpleDateFormat time_formatter = new SimpleDateFormat("H'h'm'm'");
					return time_formatter.format(new Date());
				case R.id.button_add_gamename:
					return game_meta.getName();
				case R.id.button_add_players:
					return game_meta.getBlackName()+"_vs_"+game_meta.getWhiteName();
			
				default:
					return null;
				}
			}

			@Override
			public void onClick(View v) {
				String toAdd = getTextByButtonId(v.getId());
				if(toAdd != null) {
					String text = input.getText().toString();
					int cursorPos = input.getSelectionStart();
					StringBuilder sb = new StringBuilder();
					sb.append(text.substring(0, cursorPos)).append(toAdd).append(text.substring(cursorPos,input.length()));
					input.setText(sb.toString());
					input.setSelection(cursorPos + toAdd.length());
				}
			}
			
		}
		FileNameAdder adder=new FileNameAdder();
		
		((Button)(findViewById(R.id.button_add_date))).setOnClickListener(adder);
		((Button)(findViewById(R.id.button_add_time))).setOnClickListener(adder);
		Button add_name_btn=((Button)(findViewById(R.id.button_add_gamename)));
		Button players_name_btn=((Button)(findViewById(R.id.button_add_players)));
		

		if (game_meta.getName().equals("") )
			add_name_btn.setVisibility(View.GONE);
		else
			add_name_btn.setOnClickListener(adder);
		
		if (game_meta.getBlackName().equals("") && game_meta.getWhiteName().equals("") )
			players_name_btn.setVisibility(View.GONE);
		else
			players_name_btn.setOnClickListener(adder);
		
		setTitle(R.string.save_sgf);
		
		class SaveSGFOnClickListener implements DialogInterface.OnClickListener {
			public void onClick(DialogInterface dialog, int whichButton) {
				String fname = input.getText().toString()+".sgf"; 
					
				if (saveSGF(getApp().getGame(),context.getSettings().getSGFSavePath() ,fname)&&
					(share_checkbox.isChecked())) {
						//add extra
						Intent it = new Intent(Intent.ACTION_SEND);   
						it.putExtra(Intent.EXTRA_SUBJECT, "SGF created with gobandroid");   
						it.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+context.getSettings().getSGFSavePath() + "/"+fname));   
						it.setType("application/x-go-sgf");   
						context.startActivity(Intent.createChooser(it, "Choose how to send the SGF"));

					}
				
				dialog.dismiss();
			}
			
		}
		
		setPositiveButton(android.R.string.ok,new SaveSGFOnClickListener());
	
	}
	
	
	public static boolean saveSGF(GoGame game,String path,String fname) {

		File f = new File(path);
		
		if (!f.isDirectory())
			f.mkdirs();
		
		try {
			f=new File(path+ "/"+fname);
			f.createNewFile();
			
			FileWriter sgf_writer = new FileWriter(f);
			
			BufferedWriter out = new BufferedWriter(sgf_writer);
			
			out.write(SGFHelper.game2sgf(game));
			out.close();
			sgf_writer.close();

		} catch (IOException e) {
			Log.i(""+e);
			return false;
		}
		
		game.getMetaData().setFileName(path+"/"+fname);
		return true;
		
	}
}
