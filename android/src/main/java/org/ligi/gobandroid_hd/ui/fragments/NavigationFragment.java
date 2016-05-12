package org.ligi.gobandroid_hd.ui.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.events.GameChangedEvent;
import org.ligi.gobandroid_hd.ui.GoPrefs;
import org.ligi.gobandroid_hd.ui.alerts.GameForwardAlert;

public class NavigationFragment extends GobandroidGameAwareFragment {

    @BindView(R.id.btn_next)
    ImageView next_btn;

    @BindView(R.id.btn_prev)
    ImageView prev_btn;

    @BindView(R.id.btn_first)
    ImageView first_btn;

    @BindView(R.id.btn_last)
    ImageView last_btn;

    @BindView(R.id.btn_previous_var)
    ImageView prev_var_btn;

    @BindView(R.id.btn_next_var)
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

    @OnClick(R.id.btn_first)
    void onFirstClick() {
        game.jumpFirst();
    }

    @OnClick(R.id.btn_last)
    void onLastClick() {
        game.jumpLast();
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.nav_button_container, container, false);

        ButterKnife.bind(this, view);

        updateButtonStates();
        return view;
    }

    @Override
    public void onGoGameChanged(GameChangedEvent gameChangedEvent) {
        super.onGoGameChanged(gameChangedEvent);
        updateButtonStates();
    }

    private void updateButtonStates() {
        setImageViewState(game.canUndo(), first_btn, prev_btn, next_btn, last_btn);
        setImageViewState(game.previousVarMove() != null, prev_var_btn);
        setImageViewState(game.nextVarMove() != null, next_var_btn);
    }

    private void setImageViewState(boolean state, ImageView... views) {
        for (final ImageView view : views) {
            view.setEnabled(state);
            ViewCompat.setAlpha(view, state ? 1f : 0.4f);
        }
    }
}
