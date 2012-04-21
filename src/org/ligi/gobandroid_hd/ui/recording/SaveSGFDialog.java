package org.ligi.gobandroid_hd.ui.recording;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.ligi.gobandroid_hd.R;
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
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.button_add_date:
					SimpleDateFormat date_formatter = new SimpleDateFormat("dd.MMM.yyyy");
					input.setText(input.getText()+date_formatter.format(new Date()));
					break;
				case R.id.button_add_gamename:
					input.setText(input.getText()+game_meta.getName());
					break;
				case R.id.button_add_players:
					input.setText(input.getText()+game_meta.getBlackName()+"_vs_"+game_meta.getWhiteName());
					break;
				}
				
			}
			
		}
		FileNameAdder adder=new FileNameAdder();
		
		((Button)(findViewById(R.id.button_add_date))).setOnClickListener(adder);
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
				String value = input.getText().toString(); 
					
				File f = new File(context.getSettings().getSGFSavePath());
				
				if (!f.isDirectory())
					f.mkdirs();
				
				try {
					f=new File(context.getSettings().getSGFSavePath() + "/"+value+".sgf");
					f.createNewFile();
					
					FileWriter sgf_writer = new FileWriter(f);
					
					BufferedWriter out = new BufferedWriter(sgf_writer);
					
					out.write(SGFHelper.game2sgf(getApp().getGame()));
					out.close();
					sgf_writer.close();

					
					getApp().getGame().getMetaData().setFileName(value+".sgf");
					if (share_checkbox.isChecked()) {
						//add extra
						Intent it = new Intent(Intent.ACTION_SEND);   
						it.putExtra(Intent.EXTRA_SUBJECT, "SGF created with gobandroid");   
						it.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+context.getSettings().getSGFSavePath() + "/"+value+".sgf"));   
						it.setType("application/x-go-sgf");   
						context.startActivity(Intent.createChooser(it, "Choose how to send the SGF"));

					}
				} catch (IOException e) {
					Log.i(""+e);
				}
				
				dialog.dismiss();
			}
			
		}
		
		setOnOKClick(new SaveSGFOnClickListener());
	
	}
	
}
