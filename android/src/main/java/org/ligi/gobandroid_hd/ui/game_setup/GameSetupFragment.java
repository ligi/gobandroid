package org.ligi.gobandroid_hd.ui.game_setup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class GameSetupFragment extends GobandroidFragment implements OnSeekBarChangeListener, OnClickListener {

    public byte act_size = 9;
    public byte act_handicap = 0;

    private final static int size_offset = 2;

    private SeekBar size_seek;
    private SeekBar handicap_seek;

    private TextView size_text;
    private Button size_button9x9;
    private Button size_button13x13;
    private Button size_button19x19;

    private TextView handicap_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        GoPrefs.init(getActivity()); // TODO remove legacy

        View view = inflater.inflate(R.layout.game_setup_inner, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lp);

        size_seek = findById(view, R.id.size_slider);
        size_seek.setOnSeekBarChangeListener(this);

        size_text = findById(view, R.id.game_size_label);

        size_button9x9 = findById(view, R.id.size_button9x9);
        size_button9x9.setOnClickListener(this);

        size_button13x13 = findById(view, R.id.size_button13x13);
        size_button13x13.setOnClickListener(this);

        size_button19x19 = findById(view, R.id.size_button19x19);
        size_button19x19.setOnClickListener(this);

        handicap_text = findById(view, R.id.handicap_label);
        handicap_seek = findById(view, R.id.handicap_seek);
        handicap_seek.setOnSeekBarChangeListener(this);

        // set defaults
        act_size = (byte) GoPrefs.getLastBoardSize();
        act_handicap = (byte) GoPrefs.getLastHandicap();

        refresh_ui();
        return view;
    }

    @Override
    public void onClick(View v) {

        if (v == size_button9x9)
            act_size = 9;
        if (v == size_button13x13)
            act_size = 13;
        else if (v == size_button19x19)
            act_size = 19;

        refresh_ui();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if ((seekBar == size_seek) && (act_size != (byte) (progress + size_offset)))
            act_size = (byte) (progress + size_offset);
        else if ((seekBar == handicap_seek) && (act_handicap != (byte) progress))
            act_handicap = (byte) progress;

        refresh_ui();
    }

    /**
     * refresh the ui elements with values from act_size / act_handicap
     */
    public void refresh_ui() {
        size_text.setText(getString(R.string.size) + " " + act_size + "x" + act_size);
        handicap_text.setText(getString(R.string.handicap) + " " + act_handicap);

        // the checks for change here are important - otherwise samsung moment
        // will die here with stack overflow
        if ((act_size - size_offset) != size_seek.getProgress())
            size_seek.setProgress(act_size - size_offset);

        if (act_handicap != handicap_seek.getProgress())
            handicap_seek.setProgress(act_handicap);

        if (getApp().getInteractionScope().getMode() == InteractionScope.MODE_GNUGO)
            size_seek.setMax(19 - size_offset);

        // only enable handicap seeker when the size is 9x9 or 13x13 or 19x19
        handicap_seek.setEnabled((act_size == 9) || (act_size == 13) || (act_size == 19));

        GoPrefs.setLastBoardSize(act_size);
        GoPrefs.setLastHandicap(act_handicap);

        if (App.getGame().getSize() != act_size || App.getGame().getHandicap() != act_handicap) {
            App.setGame(new GoGame(act_size, act_handicap));
        }

        if (getActivity() instanceof GoActivity) {
            final GoBoardViewHD board = ((GoActivity) getActivity()).getBoard();

            if (board != null) {
                board.regenerateStroneImagesWithNewSize();
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
