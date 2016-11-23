package org.ligi.gobandroid_hd.ui.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.io.File;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.sgf.SGFWriter;
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

        setContentView(R.layout.share_options);
        final File file = new File(getSettings().getSGFSavePath(), "game_to_share_via_action.sgf");

        if (SGFWriter.INSTANCE.saveSGF(getGameProvider().get(), file)) { // if we could save
            // the file add extra
            init(context, file);
        }

    }

    public ShareAsAttachmentDialog(Context context, File file) {
        super(context);
        init(context, file);
    }

    public void init(Context context, File file) {
        final Intent it = new Intent(Intent.ACTION_SEND);
        it.putExtra(Intent.EXTRA_SUBJECT, "SGF created with gobandroid");
        it.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
        it.setType("application/x-go-sgf");
        context.startActivity(Intent.createChooser(it, context.getString(R.string.choose_how_to_send_sgf)));

    }

    @Override
    public void show() {
        super.show();
        dismiss();
    }

}
