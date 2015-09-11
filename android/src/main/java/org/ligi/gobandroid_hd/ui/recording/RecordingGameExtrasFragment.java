package org.ligi.gobandroid_hd.ui.recording;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import org.ligi.axt.simplifications.SimpleTextWatcher;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.fragments.GobandroidGameAwareFragment;

public class RecordingGameExtrasFragment extends GobandroidGameAwareFragment {

    private EditText editText;
    private Handler handler = new Handler();

    @Override
    public View createView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        editText = new EditText(getActivity());

        editText.setText(game.getActMove().getComment());
        editText.setHint(R.string.enter_your_comments_here);
        editText.setGravity(Gravity.TOP);
        editText.setTextColor(this.getResources().getColor(R.color.text_color_on_board_bg));

        editText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                game.getActMove().setComment(s.toString());
            }
        });

        editText.setLayoutParams(lp);

        return editText;
    }

    @Override
    public void onGoGameChange() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if ((editText != null) && getActivity() != null) {
                    editText.setText(game.getActMove().getComment());
                }
            }

        });
    }

}
