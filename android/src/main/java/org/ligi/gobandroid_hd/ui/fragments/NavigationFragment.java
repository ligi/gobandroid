package org.ligi.gobandroid_hd.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.events.GameChangedEvent;
import org.ligi.gobandroid_hd.ui.GoPrefs;
import org.ligi.gobandroid_hd.ui.alerts.GameForwardAlert;

public class NavigationFragment extends GobandroidGameAwareFragment {

    @Bind(R.id.btn_next)
    ImageView next_btn;

    @Bind(R.id.btn_prev)
    ImageView prev_btn;

    @Bind(R.id.btn_first)
    ImageView first_btn;

    @Bind(R.id.btn_last)
    ImageView last_btn;

    @Bind(R.id.btn_previous_var)
    ImageView prev_var_btn;

    @Bind(R.id.btn_next_var)
    ImageView next_var_btn;

    @OnClick(R.id.btn_next)
    public void gameNavNext() {
        if (GoPrefs.isShowForwardAlertWanted()) {
            GameForwardAlert.Companion.showIfNeeded(getActivity(), game);
        } else {
            game.redo(0);
        }
    }

    @OnClick(R.id.btn_prev)
    public void gameNavPrev() {
        if (game.canUndo()) {
            game.undo();
        }
    }

    @OnClick(R.id.btn_previous_var)
    public void gameVarLast() {
        game.jump(game.previousVarMove());
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
        setImageViewState(first_btn, game.canUndo());
        setImageViewState(prev_btn, game.canUndo());
        setImageViewState(next_btn, game.canRedo());
        setImageViewState(last_btn, game.canRedo());
        setImageViewState(prev_var_btn, game.previousVarMove() != null);
        setImageViewState(next_var_btn, game.nextVarMove() != null);
    }

    private void setImageViewState(ImageView view, boolean state) {
        if (view != null) {
            view.setEnabled(state);
            view.setAlpha(state ? 255 : 100);
        }
    }
}
