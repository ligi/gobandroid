package org.ligi.gobandroid_hd.ui.share;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioGroup;

import org.ligi.axt.listeners.DialogDiscardingOnClickListener;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.GoBoardViewHD;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.app.PendingIntent.getActivity;

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
                        String fname =  settings.getSGFBasePath() + "/game_to_share_via_action.png";

                        WindowManager wm = (WindowManager) getContext()
                                .getSystemService(Context.WINDOW_SERVICE);
                        int width = wm.getDefaultDisplay().getWidth();
                        int height = wm.getDefaultDisplay().getHeight();

                        View inflate = View.inflate(getContext(), R.layout.game, null);
                        GoBoardViewHD v = (GoBoardViewHD) inflate.findViewById(R.id.go_board);
                        v.layout(0, 0, Math.min(width,height), Math.min(width,height));
                        v.screenshot(fname, true);

                        final Intent it = new Intent(Intent.ACTION_SEND);
                        it.putExtra(Intent.EXTRA_SUBJECT, "Image created with gobandroid");
                        it.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fname));
                        it.setType("image/*");
                        getContext().startActivity(Intent.createChooser(it, getContext().getString(R.string.choose_how_to_send_sgf)));

                        break;
                }

                dismiss();

            }
        });

    }
}
