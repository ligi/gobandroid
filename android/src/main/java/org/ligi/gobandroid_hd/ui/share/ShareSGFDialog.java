package org.ligi.gobandroid_hd.ui.share;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.RadioGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.io.File;
import org.ligi.axt.listeners.DialogDiscardingOnClickListener;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.GoBoardViewHD;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;

/**
 * Dialog with the intention to share the current Game
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         <p/>
 *         License: This software is licensed with GPLv3
 */
public class ShareSGFDialog extends GobandroidDialog {

    @BindView(R.id.shareTypeRadioGroup)
    RadioGroup typeSpinner;

    public ShareSGFDialog(Context context) {
        super(context);

        setContentView(R.layout.share_options);
        ButterKnife.bind(this, this);

        setTitle(R.string.share);
        setIconResource(R.drawable.ic_social_share);

        setNegativeButton(R.string.cancel, new DialogDiscardingOnClickListener());
        setPositiveButton(R.string.ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (typeSpinner.getCheckedRadioButtonId()) {
                    case R.id.radioButtonAsUnicode:
                        final Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, gameProvider.get().getVisualBoard().toString(true));
                        intent.setType("text/plain");
                        getContext().startActivity(Intent.createChooser(intent, getContext().getString(R.string.choose_how_to_send_sgf)));
                        break;

                    case R.id.radioButtonAsAttachment:
                        new ShareAsAttachmentDialog(getContext());
                        break;

                    case R.id.radioButtonAsImage:
                        final File file = new File(settings.getSGFBasePath(), "game_to_share_via_action.png");

                        final GoBoardViewHD goBoardViewHD = new GoBoardViewHD(getContext());
                        final Bitmap mutableImage = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.shinkaya)
                                                                 .copy(Bitmap.Config.ARGB_8888, true);

                        goBoardViewHD.layout(0, 0, mutableImage.getWidth(), mutableImage.getHeight());
                        goBoardViewHD.screenshot(file, mutableImage);

                        final Intent it = new Intent(Intent.ACTION_SEND);
                        it.putExtra(Intent.EXTRA_SUBJECT, "Image created with gobandroid");
                        it.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file));
                        it.setType("image/*");
                        getContext().startActivity(Intent.createChooser(it, getContext().getString(R.string.choose_how_to_send_sgf)));

                        break;
                }

                dismiss();

            }
        });

    }
}
