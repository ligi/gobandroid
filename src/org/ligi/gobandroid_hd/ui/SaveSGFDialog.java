package org.ligi.gobandroid_hd.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ligi.android.common.dialogs.DialogDiscarder;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGameMetadata;
import org.ligi.gobandroid_hd.logic.GoGameProvider;
import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.tracedroid.logging.Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
public class SaveSGFDialog {

	public static void show(final Activity ctx) {
		ContextThemeWrapper themed_ctx=new ContextThemeWrapper(ctx, R.style.dialog_theme);
		View form=LayoutInflater.from(themed_ctx).inflate(R.layout.save_sgf_dialog, null);
		
		TextView intro_text=(TextView)form.findViewById(R.id.intro_txt);
		intro_text.setText(String.format(themed_ctx.getResources().getString(R.string.save_sgf_question),GoPrefs.getSGFPath()));
		
		final EditText input = (EditText)form.findViewById(R.id.sgf_name_edittext);
		final CheckBox share_checkbox=(CheckBox)form.findViewById(R.id.share_checkbox);
		final GoGameMetadata game_meta=GoGameProvider.getGame().getMetaData();
		class FileNameAdder implements OnClickListener {

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
		
		((Button)(form.findViewById(R.id.button_add_date))).setOnClickListener(adder);
		Button add_name_btn=((Button)(form.findViewById(R.id.button_add_gamename)));
		Button players_name_btn=((Button)(form.findViewById(R.id.button_add_players)));
		

		if (game_meta.getName().equals("") )
			add_name_btn.setVisibility(View.GONE);
		else
			add_name_btn.setOnClickListener(adder);
		
		if (game_meta.getBlackName().equals("") && game_meta.getWhiteName().equals("") )
			players_name_btn.setVisibility(View.GONE);
		else
			players_name_btn.setOnClickListener(adder);
		
		new AlertDialog.Builder(themed_ctx).setTitle(R.string.save_sgf).setView(form)
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
				
				out.write(SGFHelper.game2sgf(GoGameProvider.getGame()));
				out.close();
				sgf_writer.close();
				
				
				if (share_checkbox.isChecked()) {
					//add extra
					Intent it = new Intent(Intent.ACTION_SEND);   
					it.putExtra(Intent.EXTRA_SUBJECT, "SGF created with gobandroid");   
					it.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+GoPrefs.getSGFPath() + "/"+value+".sgf"));   
					it.setType("application/x-go-sgf");   
					ctx.startActivity(Intent.createChooser(it, "Choose how to send the SGF"));

				}
			} catch (IOException e) {
				Log.i(""+e);
			}

		}
		}).setNegativeButton(R.string.cancel, new DialogDiscarder()).show();

	}
}
