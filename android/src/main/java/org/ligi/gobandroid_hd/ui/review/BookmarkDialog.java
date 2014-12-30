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

import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.sgf.SGFWriter;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.sgf_listing.GoLink;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Dialog to show when user wants to set a BookMark
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         <p/>
 *         License: This software is licensed with GPLv3
 */
public class BookmarkDialog extends GobandroidDialog {

    @InjectView(R.id.bookmark_name)
    EditText fileNameEdit;

    @InjectView(R.id.message)
    TextView message;

    /**
     * sometimes for saving bookmarks we need an ensured saved file e.g. when
     * coming directly from recording - this should ensure this
     *
     * @return the ensured Filename
     */
    private static String getEnsuredFilename() {

        String fname = App.getGame().getMetaData().getFileName();
        if (TextUtils.isEmpty(fname)) {
            // was not saved before - do it now ( needed for a bookmark )

            fname = getDefaultFilename();
            SGFWriter.saveSGF(App.getGame(), App.getGobandroidSettings().getSGFSavePath() + "/autosave/" + fname);
        }

        return fname;
    }

    public static String getCleanEnsuredFilename() {
        final String[] path_components = getEnsuredFilename().split("/");
        return path_components[path_components.length - 1].replace(".sgf", "");
    }

    private static String getDefaultFilename() {
        final SimpleDateFormat date_formatter = new SimpleDateFormat("dd.MMM.yyyy_HH_mm_ss");
        return date_formatter.format(new Date()) + ".sgf";
    }

    public BookmarkDialog(final GobandroidFragmentActivity context) {
        super(context);

        setTitle(R.string.bookmark);
        setIconResource(R.drawable.bookmark);
        setContentView(R.layout.save_bookmark);

        ButterKnife.inject(this);
        final String innerFileName = getCleanEnsuredFilename();

        message.setText(context.getResources().getString(R.string.bookmark_to_write_into) + " " + context.getSettings().getBookmarkPath());
        fileNameEdit.setText(innerFileName);

        class SaveBookmarkOnClickListener implements OnClickListener {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                GoLink.saveGameToGoLink(App.getGame(), context.getSettings().getBookmarkPath(), fileNameEdit.getText().toString() + ".golink");
                dialog.dismiss();
            }

        }
        setPositiveButton(android.R.string.ok, new SaveBookmarkOnClickListener());
    }

}
