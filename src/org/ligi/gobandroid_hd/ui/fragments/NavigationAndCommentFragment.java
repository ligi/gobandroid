package org.ligi.gobandroid_hd.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.ligi.gobandroid_hd.GobandroidApp;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;

public class NavigationAndCommentFragment extends Fragment implements GoGameChangeListener {

    private TextView myTextView;

    private GoGame game;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View res = inflater.inflate(R.layout.game_extra_review, container, false);

        myTextView = (TextView) res.findViewById(R.id.comments_textview);

        game = ((GobandroidApp) (getActivity().getApplicationContext())).getGame();
        game.addGoGameChangeListener(this);

        getFragmentManager().beginTransaction().replace(R.id.container_for_nav, new NavigationFragment()).commit();
        onGoGameChange();
        return res;
    }

    @Override
    public void onGoGameChange() {
        gameChangeHandler.post(new Runnable() {

            @Override
            public void run() {
                if (myTextView != null) {
                    myTextView.setText(game.getActMove().getComment());
                    CommentHelper.linkifyCommentTextView(myTextView);

                }
            }

        });
    }

    private Handler gameChangeHandler = new Handler();

    @Override
    public void onDestroyView() {
        game.removeGoGameChangeListener(this);
        super.onDestroyView();
    }

}
