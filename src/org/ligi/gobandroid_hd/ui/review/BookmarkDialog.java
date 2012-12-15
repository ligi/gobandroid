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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.ligi.gobandroid_beta.R;
import org.ligi.gobandroid_hd.GobandroidApp;
import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.sgf_listing.GoLink;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Dialog to show when user wants to set a BookMark
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 *         License: This software is licensed with GPLv3
 * 
 **/
public class BookmarkDialog extends GobandroidDialog {

	/**
	 * sometimes for saving bookmarks we need an ensured saved file e.g. when
	 * coming directly from recording - this should ensure this
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getEnsuredFilename(Context ctx) {
		GobandroidApp app = (GobandroidApp) ctx.getApplicationContext();

		String fname = app.getGame().getMetaData().getFileName();
		if ((fname == null) || (fname.equals(""))) {
			// was not saved before - do it now ( needed for a bookmark )

			fname = getDefaultFilename();
			SGFHelper.saveSGF(app.getGame(), app.getSettings()
					.getSGFSavePath() + "/autosave/" + fname);
		}

		return fname;
	}

	public static String getCleanEnsuredFilename(Context ctx) {
		String[] path_components = getEnsuredFilename(ctx).split("/");
		return path_components[path_components.length - 1].replace(".sgf", "");
	}

	public static String getDefaultFilename() {
		SimpleDateFormat date_formatter = new SimpleDateFormat(
				"dd.MMM.yyyy_HH_mm_ss");
		return date_formatter.format(new Date()) + ".sgf";
	}

	public BookmarkDialog(final GobandroidFragmentActivity context) {
		super(context);

		setTitle(R.string.bookmark);
		setIconResource(R.drawable.bookmark);
		setContentView(R.layout.save_bookmark);

		setIsSmallDialog();

		String inner_fname = getCleanEnsuredFilename(context);

		final EditText fname_edit = (EditText) findViewById(R.id.bookmark_name);
		((TextView) findViewById(R.id.message)).setText(context.getResources()
				.getString(R.string.bookmark_to_write_into)
				+ " "
				+ context.getSettings().getBookmarkPath());
		fname_edit.setText(inner_fname);

		class SaveBookmarkOnClickListener implements OnClickListener {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				GoLink.saveGameToGoLink(getApp().getGame(), context
						.getSettings().getBookmarkPath(), fname_edit.getText()
						.toString() + ".golink");
				dialog.dismiss();
			}

		}
		setPositiveButton(android.R.string.ok,
				new SaveBookmarkOnClickListener());
	}

}
