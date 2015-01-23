package org.ligi.gobandroid_hd.ui.game_setup;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.GoBoardViewHD;
import org.ligi.gobandroid_hd.ui.GoPrefs;
import org.ligi.gobandroid_hd.ui.fragments.GobandroidFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class GameSetupFragment extends GobandroidFragment implements OnSeekBarChangeListener {

    public int act_size = 9;
    private int wanted_size;

    public int act_handicap = 0;

    private final static int size_offset = 2;

    @InjectView(R.id.size_seek)
    SeekBar size_seek;

    @InjectView(R.id.handicap_seek)
    SeekBar handicap_seek;

    @InjectView(R.id.game_size_label)
    TextView size_text;

    @InjectView(R.id.handicap_label)
    TextView handicap_text;

    @OnClick(R.id.size_button9x9)
    void setSize9x9() {
        setSize(9);
    }

    @OnClick(R.id.size_button13x13)
    void setSize13x13() {
        setSize(13);
    }

    @OnClick(R.id.size_button19x19)
    void setSize19x19() {
        setSize(19);
    }

    private void setSize(int size) {
        wanted_size = size;

        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (act_size != wanted_size) {
                    act_size += (act_size > wanted_size) ? -1 : 1;
                    uiHandler.postDelayed(this, 16);
                }
                refresh_ui();
            }
        });

    }

    private Handler uiHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        uiHandler = new Handler();
        GoPrefs.init(getActivity()); // TODO remove legacy

        final View view = inflater.inflate(R.layout.game_setup_inner, container, false);

        ButterKnife.inject(this, view);

        size_seek.setOnSeekBarChangeListener(this);
        handicap_seek.setOnSeekBarChangeListener(this);

        // set defaults
        act_size = GoPrefs.getLastBoardSize();
        act_handicap = GoPrefs.getLastHandicap();

        refresh_ui();
        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if ((seekBar == size_seek) && (act_size != (byte) (progress + size_offset)))
            setSize(progress + size_offset);
        else if ((seekBar == handicap_seek) && (act_handicap != (byte) progress))
            act_handicap = (byte) progress;

        refresh_ui();
    }

    private boolean isAnimating() {
        return act_size != wanted_size;
    }

    /**
     * refresh the ui elements with values from act_size / act_handicap
     */
    public void refresh_ui() {

        size_text.setText(getString(R.string.size) + " " + act_size + "x" + act_size);

        if (!isAnimating()) {
            // only enable handicap seeker when the size is 9x9 or 13x13 or 19x19
            handicap_seek.setEnabled((act_size == 9) || (act_size == 13) || (act_size == 19));

            if (handicap_seek.isEnabled()) {
                handicap_text.setText(getString(R.string.handicap) + " " + act_handicap);
            } else {
                handicap_text.setText(getString(R.string.handicap_only_for));
            }
        }

        // the checks for change here are important - otherwise samsung moment
        // will die here with stack overflow
        if ((act_size - size_offset) != size_seek.getProgress())
            size_seek.setProgress(act_size - size_offset);

        if (act_handicap != handicap_seek.getProgress())
            handicap_seek.setProgress(act_handicap);

        if (App.getInteractionScope().getMode() == InteractionScope.MODE_GNUGO)
            size_seek.setMax(19 - size_offset);


        GoPrefs.setLastBoardSize(act_size);
        GoPrefs.setLastHandicap(act_handicap);

        if (App.getGame().getSize() != act_size || App.getGame().getHandicap() != act_handicap) {
            App.setGame(new GoGame(act_size, act_handicap));
        }

        if (getActivity() instanceof GoActivity) {
            final GoBoardViewHD board = ((GoActivity) getActivity()).getBoard();

            if (board != null) {
                board.regenerateStoneImagesWithNewSize();
                board.invalidate();
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

}
