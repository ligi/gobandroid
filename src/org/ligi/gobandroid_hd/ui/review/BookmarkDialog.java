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
import org.ligi.gobandroid_hd.ui.GobandroidDialog;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.tracedroid.logging.Log;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.TextView;
/**
 * Dialog to show when user wants to set a BookMark
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 * License: This software is licensed with GPLv3
 * 
 **/
public class BookmarkDialog extends GobandroidDialog {

	public BookmarkDialog(final GobandroidFragmentActivity context) {
		super(context);
		
		String fname=context.getApp().getGame().getMetaData().getFileName();
		if ((fname==null)||	(fname.equals("")))
			return; // need that to make sense - todo we should do a a save with some hashed filename here for the rescue
		
		
		setTitle(R.string.menu_bookmark);
		setIconResource(R.drawable.bookmark);
		setContentView(R.layout.save_bookmark);
		

		String[] path_components=fname.split("/");
		String inner_fname=path_components[path_components.length-1].replace(".sgf", "");
		
		final EditText fname_edit=(EditText)findViewById(R.id.bookmark_name);
		((TextView)findViewById(R.id.message))
		.setText(context.getResources().getString(R.string.bookmark_to_write_into) + " " + context.getSettings().getBookmarkPath());
	    fname_edit.setText(inner_fname);
	    
	    
	    class SaveBookmarkOnClickListener implements OnClickListener {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				int move_pos=context.getApp().getGame().getActMove().getMovePos();
				
				File f = new File(context.getSettings().getBookmarkPath());
				
				if (!f.isDirectory())
					f.mkdirs();
				
				try {
					f=new File(context.getSettings().getBookmarkPath() + "/"+fname_edit.getText().toString()+".golink");
					f.createNewFile();
					
					FileWriter sgf_writer = new FileWriter(f);
					
					BufferedWriter out = new BufferedWriter(sgf_writer);
					
					out.write(context.getApp().getGame().getMetaData().getFileName()+":#"+move_pos);
					out.close();
					sgf_writer.close();
				}
				catch (IOException e) {
					Log.i(""+e);
				}
				
				dialog.dismiss();
			}
	    	
	    }
	    setOnOKClick(new SaveBookmarkOnClickListener());
	}

}
