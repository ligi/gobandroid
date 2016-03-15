package org.ligi.gobandroid_hd.ui.fragments;

import android.os.Bundle;
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

    @Bind(R.id.btn_last_var)
    ImageView last_var_btn;

    @Bind(R.id.btn_next_var)
    ImageView next_var_btn;

    @OnClick(R.id.btn_next)
    public void gameNavNext() {
        game.redo(0);
    }

    @OnClick(R.id.btn_prev)
    public void gameNavPrev() {
        if (game.canUndo()) {
            game.undo();
        }
    }

    @OnClick(R.id.btn_last_var)
    public void gameVarLast() {
        game.jump(game.lastVarMove());
    }

    @OnClick(R.id.btn_next_var)
    public void gameVarNext() {
        game.jump(game.nextVarMove());
    }

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
        updateButtonStates();
    }

    private void updateButtonStates() {
        if (first_btn == null) {
            return;
        }

        first_btn.setVisibility(game.canUndo() ? View.VISIBLE : View.INVISIBLE);
        prev_btn.setVisibility(game.canUndo() ? View.VISIBLE : View.INVISIBLE);
        next_btn.setVisibility(game.canRedo() ? View.VISIBLE : View.INVISIBLE);
        last_btn.setVisibility(game.canRedo() ? View.VISIBLE : View.INVISIBLE);
        last_var_btn.setVisibility(game.lastVarMove() != null ?  View.VISIBLE : View.INVISIBLE);
        next_var_btn.setVisibility(game.nextVarMove() != null ?  View.VISIBLE : View.INVISIBLE);

    }



}
