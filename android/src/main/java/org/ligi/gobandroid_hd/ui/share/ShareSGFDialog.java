package org.ligi.gobandroid_hd.ui.share;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.RadioGroup;

import org.ligi.axt.listeners.DialogDiscardingOnClickListener;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Dialog with the intention to share the current Game
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         <p/>
 *         License: This software is licensed with GPLv3
 */
public class ShareSGFDialog extends GobandroidDialog {

    @Bind(R.id.shareTypeRadioGroup)
    RadioGroup typeSpinner;

    public ShareSGFDialog(Context context) {
        super(context);

        setContentView(R.layout.share_options);
        ButterKnife.bind(this, this);

        setTitle(R.string.share);
        setIconResource(android.R.drawable.ic_menu_share);

        setNegativeButton(R.string.cancel, new DialogDiscardingOnClickListener());
        setPositiveButton(R.string.ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                switch (typeSpinner.getCheckedRadioButtonId()) {

                    case R.id.radioButtonAsUnicode:
                        final Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, gameProvider.get().getVisualBoard().toString(true));
                        getContext().startActivity(Intent.createChooser(intent, getContext().getString(R.string.choose_how_to_send_sgf)));
                        break;

                    case R.id.radioButtonAsAttachment:
                        new ShareAsAttachmentDialog(getContext());
                        break;

                }

                dismiss();

            }
        });

    }
}
