package org.ligi.gobandroid_hd.ui.alerts;

import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Dialog with the intention to share the current Game
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 *         License: This software is licensed with GPLv3
 * 
 **/
public class ShareSGFDialog extends GobandroidDialog {

	public ShareSGFDialog(Context context) {
		super(context);

		String fname = getApp().getSettings().getSGFSavePath() + "/game_to_share_via_action.sgf";
		if (SGFHelper.saveSGF(getApp().getGame(), fname)) { // if we could save
															// the file
			// add extra
			Intent it = new Intent(Intent.ACTION_SEND);
			it.putExtra(Intent.EXTRA_SUBJECT, "SGF created with gobandroid");
			it.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fname));
			it.setType("application/x-go-sgf");
			context.startActivity(Intent.createChooser(it, "Choose how to send the SGF"));

		}

	}

	@Override
	public void show() {
		super.show();
		dismiss();
	}

}
