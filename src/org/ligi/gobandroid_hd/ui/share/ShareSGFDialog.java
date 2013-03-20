package org.ligi.gobandroid_hd.ui.share;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Spinner;
import org.ligi.android.common.dialogs.DialogDiscarder;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.online.UploadGameAndShareIntent;
import org.ligi.gobandroid_hd.ui.online.UploadGameAndShareToGPplus;

/**
 * Dialog with the intention to share the current Game
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         <p/>
 *         License: This software is licensed with GPLv3
 */
public class ShareSGFDialog extends GobandroidDialog {

    private Spinner typeSpinner;
    private CheckBox publicCheckBox;
    private RadioGroup shareTypeRG;
    private Context mContext;

    /**
     * when no fname used in constructor -> use the current game
     *
     * @param context
     */
    public ShareSGFDialog(Context context) {
        super(context);
        mContext = context;
        setContentView(R.layout.share_options);
        setTitle(R.string.share);
        setIconResource(android.R.drawable.ic_menu_share);

        shareTypeRG = (RadioGroup) findViewById(R.id.shareTypeRadiGroup);

        publicCheckBox = (CheckBox) findViewById(R.id.public_cb);

        setPositiveButton(R.string.ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        setNegativeButton(R.string.cancel, new DialogDiscarder());

        typeSpinner = (Spinner) findViewById(R.id.type_spinner);
        final String[] strings = {
                context.getString(R.string.invite),
                context.getString(R.string.recorded_game),
                context.getString(R.string.commented_game),
                context.getString(R.string.tsumego),
                context.getString(R.string.situation),
                context.getString(R.string.other)
        };

        final String[] game_type_keys = {
                "public_invite",
                "kifu",
                "commented_game",
                "tsumego_to_solve",
                "situation",
                "other"
        };

        final String[] game_anounce_strings = {
                "please join the game",
                "a kifu for you",
                "have a look at the comments in this game",
                "try to solve this Tsumego",
                "have a look at this Situation",
                "here you have something to do with GO/Baduk/Weiqi"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, strings);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        typeSpinner.setAdapter(adapter);

        setIsSmallDialog();

        shareTypeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setItemVisibilityByRadioButtonId(checkedId);
            }
        });

        setItemVisibilityByRadioButtonId(shareTypeRG.getCheckedRadioButtonId());


        getPositiveButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type = game_type_keys[typeSpinner.getSelectedItemPosition()];
                String introText = game_anounce_strings[typeSpinner.getSelectedItemPosition()];

                switch (shareTypeRG.getCheckedRadioButtonId()) {

                    case R.id.radioButtonGPlus:
                        new UploadGameAndShareToGPplus((GobandroidFragmentActivity) mContext, type, introText).execute();
                        break;

                    case R.id.radioButtonLink:
                        new UploadGameAndShareIntent((GobandroidFragmentActivity) mContext, type, introText).execute();
                        break;

                    case R.id.radioButtonAsAttachment:
                        new ShareAsAttachmentDialog(getContext());
                        break;

                }
                dismiss();
            }
        });
    }

    public ShareSGFDialog(Context context, String fname) {
        this(context);
    }

    private void setItemVisibilityByRadioButtonId(int checkedId) {
        switch (checkedId) {
            case R.id.radioButtonGPlus:
                typeSpinner.setVisibility(View.VISIBLE);
                publicCheckBox.setVisibility(View.GONE);
                break;

            case R.id.radioButtonLink:
                typeSpinner.setVisibility(View.VISIBLE);
                publicCheckBox.setVisibility(View.GONE);
                break;

            case R.id.radioButtonAsAttachment:
                typeSpinner.setVisibility(View.GONE);
                publicCheckBox.setVisibility(View.GONE);
                break;

        }
    }


}
