package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_hd.R;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
/**
 * Dialog to help the user ( link to wikipedia Article and Youtube )
 *  
 * @author <a href="http://ligi.de">Marcus -LiGi- Bueschleb </a>
 * 
 *         This software is licenced with GPLv3
 */
public class HelpDialog extends GobandroidDialog {

	private Resources res;

	public HelpDialog(final Context context) {
		super(context);
		this.setIconResource(R.drawable.help);
		this.setTitle(R.string.link_tab_help);
		this.setIsSmallDialog();
		res = context.getResources();

		this.addItem(R.drawable.yt_icon, R.string.youtube_tutorial,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						context.startActivity(new Intent(
								"android.intent.action.VIEW",
								Uri.parse(res
										.getString(R.string.youtube_tutorial_link))));
					}

				});
		this.addItem(R.drawable.wp_ico, R.string.wikipedia_rules,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						context.startActivity(new Intent(
								"android.intent.action.VIEW",
								Uri.parse(res
										.getString(R.string.wikipedia_rules_link))));
					}

				});

		this.setPositiveButton(R.string.cancel);
	}
}