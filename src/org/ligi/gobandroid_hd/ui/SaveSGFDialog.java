package org.ligi.gobandroid_hd.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ligi.android.common.dialogs.DialogDiscarder;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGameProvider;
import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.tracedroid.logging.Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * TODO do layout via xml
 * 
 * @author ligi
 *
 */
public class SaveSGFDialog {

	public static void show(final Activity ctx) {

		LinearLayout lin=new LinearLayout(ctx);
		lin.setOrientation(LinearLayout.VERTICAL);
		
		final EditText input = new EditText(ctx);   
		input.setText(GoPrefs.getSGFFname());

		final CheckBox box=new CheckBox(ctx);
		box.setText("share after saving?");
		lin.addView(input);
		lin.addView(box);
		
		new AlertDialog.Builder(ctx).setTitle(R.string.save_sgf).setMessage("How should the file I will write to " +GoPrefs.getSGFPath() + " be named?").setView(lin)
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
				
				
				if (box.isChecked()) {
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
