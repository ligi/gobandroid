/**
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

package org.ligi.gobandroid_hd.ui.review;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGameProvider;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.tracedroid.logging.Log;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
/**
 * Dialog to show when user wants to set a BookMark
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 * License: This software is licensed with GPLv3
 * 
 **/
public class BookmarkDialog {

	public static void show(final GobandroidFragmentActivity ctx) {
		final EditText fname_edit=new EditText(ctx);
		
		String fname=GoGameProvider.getGame().getMetaData().getFileName();
		if ((fname==null)||	(fname.equals("")))
			return; // need that to make sense
		
		String[] path_components=fname.split("/");
		String inner_fname=path_components[path_components.length-1].replace(".sgf", "");
	    fname_edit.setText(inner_fname);
		
		new AlertDialog.Builder(ctx).setTitle(R.string.menu_bookmark)
		.setMessage(R.string.bookmark_to_write_into + " " + ctx.getSettings().getBookmarkPath())
		.setView(fname_edit)
		.setPositiveButton(android.R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				int move_pos=GoGameProvider.getGame().getActMove().getMovePos();
			
				File f = new File(ctx.getSettings().getBookmarkPath());
				
				if (!f.isDirectory())
					f.mkdirs();
				
				try {
					f=new File(ctx.getSettings().getBookmarkPath() + "/"+fname_edit.getText().toString()+".golink");
					f.createNewFile();
					
					FileWriter sgf_writer = new FileWriter(f);
					
					BufferedWriter out = new BufferedWriter(sgf_writer);
					
					out.write(GoGameProvider.getGame().getMetaData().getFileName()+":#"+move_pos);
					out.close();
					sgf_writer.close();
				}
				catch (IOException e) {
					Log.i(""+e);
				}
			}
			
		})
		.show();
	}
}
