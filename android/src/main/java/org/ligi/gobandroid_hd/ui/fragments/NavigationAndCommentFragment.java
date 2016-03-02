package org.ligi.gobandroid_hd.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.events.GameChangedEvent;
import org.ligi.gobandroid_hd.ui.go_terminology.GoTerminologyViewActivity;

public class NavigationAndCommentFragment extends GobandroidGameAwareFragment {


    private Handler gameChangeHandler = new Handler();
    private TextView myTextView;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View res = inflater.inflate(R.layout.game_extra_review, container, false);

        myTextView = (TextView) res.findViewById(R.id.comments_textview);
        myTextView.setFocusable(false);

        res.findViewById(R.id.scrollview).setFocusable(false);

        getFragmentManager().beginTransaction().replace(R.id.container_for_nav, new NavigationFragment()).commit();
        onGoGameChanged(null);
        return res;
    }

    @Override
    public void onGoGameChanged(GameChangedEvent gameChangedEvent) {
        super.onGoGameChanged(gameChangedEvent);
        gameChangeHandler.post(new Runnable() {

            @Override
            public void run() {
                if (myTextView != null) {
                    myTextView.setText(game.getActMove().getComment());
                    GoTerminologyViewActivity.Companion.linkifyTextView(myTextView);
                }
            }

        });
    }

}
