package org.ligi.gobandroid_hd.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.go_terminology.GoTerminologyViewActivity;

public class CommentAndNowPlayingFragment extends GobandroidGameAwareFragment {

    private TextView myTextView;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View res = inflater.inflate(R.layout.game_extra_review, container, false);
        myTextView = (TextView) res.findViewById(R.id.comments_textview);
        onGoGameChange();
        return res;
    }

    @Override
    public void onGoGameChange() {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (myTextView != null) {
                    myTextView.setText(game.actMove.getComment());
                    GoTerminologyViewActivity.Companion.linkifyTextView(myTextView);
                }
            }

        });
    }

}
