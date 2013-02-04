package org.ligi.gobandroid_hd.ui.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;

/**
 * Dialog with the intention to share the current Game
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         <p/>
 *         License: This software is licensed with GPLv3
 */
public class ShareAsAttachmentDialog extends GobandroidDialog {

    public ShareAsAttachmentDialog(Context context) {
        super(context);

        String fname = getApp().getSettings().getSGFSavePath() + "/game_to_share_via_action.sgf";
        if (SGFHelper.saveSGF(getApp().getGame(), fname)) { // if we could save
            // the file add extra
            init(context, fname);
        }

    }

    public ShareAsAttachmentDialog(Context context, String fname) {
        super(context);
        init(context, fname);
    }

    public void init(Context context, String fname) {
        Intent it = new Intent(Intent.ACTION_SEND);
        it.putExtra(Intent.EXTRA_SUBJECT, "SGF created with gobandroid");
        it.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fname));
        it.setType("application/x-go-sgf");
        context.startActivity(Intent.createChooser(it, context.getString(R.string.choose_how_to_send_sgf)));

    }

    @Override
    public void show() {
        super.show();
        dismiss();
    }

}
