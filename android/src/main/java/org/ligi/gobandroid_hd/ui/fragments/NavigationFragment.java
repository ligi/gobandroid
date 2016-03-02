package org.ligi.gobandroid_hd.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.events.GameChangedEvent;
import org.ligi.gobandroid_hd.ui.alerts.GameForwardAlert;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NavigationFragment extends GobandroidGameAwareFragment {

    @Bind(R.id.btn_next)
    ImageView next_btn;

    @Bind(R.id.btn_prev)
    ImageView prev_btn;

    @Bind(R.id.btn_first)
    ImageView first_btn;

    @Bind(R.id.btn_last)
    ImageView last_btn;

    @OnClick(R.id.btn_next)
    public void gameNavNext() {
        GameForwardAlert.showIfNeeded(getActivity(), game);
    }

    @OnClick(R.id.btn_prev)
    public void gameNavPrev() {
        if (game.canUndo()) {
            game.undo();
        }
    }

    private Handler gameChangeHandler = new Handler();

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.nav_button_container, container, false);

        ButterKnife.bind(this, view);
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

        updateButtonStates();
        return view;
    }

    @Override
    public void onGoGameChanged(GameChangedEvent gameChangedEvent) {
        super.onGoGameChanged(gameChangedEvent);
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



}
