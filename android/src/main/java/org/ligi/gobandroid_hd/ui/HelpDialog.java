package org.ligi.gobandroid_hd.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;

import org.ligi.gobandroid_hd.R;

/**
 * Dialog to help the user ( link to wikipedia Article and Youtube )
 *
 * @author <a href="http://ligi.de">Marcus -LiGi- Bueschleb </a>
 *         <p/>
 *         This software is licenced with GPLv3
 */
public class HelpDialog extends GobandroidDialog {

    public HelpDialog(final Context context) {
        super(context);
        setIconResource(R.drawable.help);
        setTitle(R.string.help);

        final Resources res=context.getResources();

        this.addItem(R.drawable.yt_icon, R.string.youtube_tutorial, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(res.getString(R.string.youtube_tutorial_link))));
            }

        });
        this.addItem(R.drawable.wp_ico, R.string.wikipedia_rules, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(res.getString(R.string.wikipedia_rules_link))));
            }

        });

        this.setPositiveButton(R.string.cancel);
    }
}