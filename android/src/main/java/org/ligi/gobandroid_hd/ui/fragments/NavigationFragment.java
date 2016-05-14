package org.ligi.gobandroid_hd.ui.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.events.GameChangedEvent;
import org.ligi.gobandroid_hd.logic.GoMove;
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
        if (GoPrefs.INSTANCE.isShowForwardAlertWanted()) {
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

    @OnClick(R.id.btn_first)
    void onFirstClick() {
        final GoMove nextJunction = game.findPrevJunction();
        if (nextJunction.isFirstMove()) {
            showJunctionInfoSnack(R.string.found_junction_snack_for_first);
            game.jump(nextJunction);
        } else {
            game.jump(nextJunction.getNextMoveVariations().get(0));
        }
    }

    @OnLongClick(R.id.btn_first)
    boolean onFirstLongClick() {
        game.jump(game.findFirstMove());
        return true;
    }

    @OnClick(R.id.btn_last)
    void onLastClick() {
        final GoMove nextJunction = game.findNextJunction();
        if (nextJunction.hasNextMove()) {
            showJunctionInfoSnack(R.string.found_junction_snack_for_last);
            game.jump(nextJunction.getNextMoveVariations().get(0));
        } else {
            game.jump(nextJunction);
        }

    }

    private void showJunctionInfoSnack(final int found_junction_snack_for_last) {
        if (!GoPrefs.INSTANCE.getHasAcknowledgedJunctionInfo()) {
            Snackbar.make(last_btn, found_junction_snack_for_last, Snackbar.LENGTH_LONG).setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    GoPrefs.INSTANCE.setHasAcknowledgedJunctionInfo(true);
                }
            }).show();
        }
    }

    @OnLongClick(R.id.btn_last)
    boolean onLastLongClick() {
        game.jump(game.findLastMove());
        return true;
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
        setImageViewState(game.canUndo(), first_btn, prev_btn);
        setImageViewState(game.canRedo(), next_btn, last_btn);
        bindButtonToMove(game.nextVariationWithOffset(-1), prev_var_btn);
        bindButtonToMove(game.nextVariationWithOffset(1), next_var_btn);
    }

    private void bindButtonToMove(final GoMove move, ImageView button) {
        setImageViewState(move != null, button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                game.jump(move);
            }
        });
    }

    private void setImageViewState(boolean state, ImageView... views) {
        for (final ImageView view : views) {
            view.setEnabled(state);
            ViewCompat.setAlpha(view, state ? 1f : 0.4f);
        }
    }
}
