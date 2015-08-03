package org.ligi.gobandroid_hd.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.alerts.GameForwardAlert;

public class NavigationFragment extends GobandroidGameAwareFragment {

    private ImageView next_btn, prev_btn, first_btn, last_btn;
    private Handler gameChangeHandler = new Handler();

    @Override
    View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View res = inflater.inflate(R.layout.nav_button_container, container, false);
        first_btn = findById(res, R.id.btn_first);
        last_btn = findById(res, R.id.btn_last);
        next_btn = findById(res, R.id.btn_next);
        prev_btn = findById(res, R.id.btn_prev);

        first_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                game.jumpFirst();
            }

        });

        last_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                game.jumpLast();
            }

        });

        next_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                gameNavNext();
            }

        });

        prev_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                gameNavPrev();
            }

        });

        updateButtonStates();
        return res;
    }

    @Override
    public void onGoGameChange() {
        gameChangeHandler.post(new Runnable() {

            @Override
            public void run() {
                updateButtonStates();
            }

        });

    }

    private void updateButtonStates() {
        if (first_btn == null) {
            return;
        }

        first_btn.setVisibility(game.canUndo() ? View.VISIBLE : View.INVISIBLE);
        prev_btn.setVisibility(game.canUndo() ? View.VISIBLE : View.INVISIBLE);

        next_btn.setVisibility(game.canRedo() ? View.VISIBLE : View.INVISIBLE);
        last_btn.setVisibility(game.canRedo() ? View.VISIBLE : View.INVISIBLE);
    }


    public void gameNavNext() {
        GameForwardAlert.showIfNeeded(getActivity(), game);
    }

    public void gameNavPrev() {
        if (!game.canUndo()) return;

        // don't do it if the mover has to move at the moment
        if (game.getGoMover().isMoversMove()) return;

        game.getGoMover().paused = true;
        game.undo();

        // undo twice if there is a mover
        if (game.canUndo() && (game.getGoMover().isMoversMove())) game.undo();

        game.getGoMover().paused = false;
    }

}
