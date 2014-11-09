package org.ligi.gobandroid_hd.ui.gnugo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;

public class GnuGoSetupDialog extends GobandroidDialog {

    private static final String SP_KEY_PLAYS_BLACK = "plays_black";
    private static final String SP_KEY_PLAYS_WHITE = "plays_white";
    private static final String SP_KEY_PLAYS_BOTH = "plays_both";

    private static final String SP_KEY_STRENGTH = "strength";

    private SharedPreferences shared_prefs;
    private RadioButton gnugo_plays_white_radio;
    private RadioButton gnugo_plays_black_radio;
    private RadioButton gnugo_plays_both_radio;
    private TextView gnugo_strength_text;

    private SeekBar strange_seek;

    public GnuGoSetupDialog(Context context) {
        super(context);

        shared_prefs = PreferenceManager.getDefaultSharedPreferences(context);

        setTitle(R.string.gnugo);
        setIconResource(R.drawable.preferences);
        setContentFill();
        setContentView(R.layout.setup_gnugo);

        gnugo_plays_white_radio = (RadioButton) this.findViewById(R.id.gnugo_plays_white_radio);
        gnugo_plays_black_radio = (RadioButton) this.findViewById(R.id.gnugo_plays_black_radio);
        gnugo_plays_both_radio = (RadioButton) this.findViewById(R.id.gnugo_plays_both_radio);
        gnugo_strength_text = (TextView) this.findViewById(R.id.gnugo_strength);

        if (shared_prefs.getBoolean(SP_KEY_PLAYS_BOTH, false))
            gnugo_plays_both_radio.setChecked(true);
        else if (shared_prefs.getBoolean(SP_KEY_PLAYS_WHITE, false))
            gnugo_plays_white_radio.setChecked(true);
        else if (shared_prefs.getBoolean(SP_KEY_PLAYS_BLACK, false))
            gnugo_plays_black_radio.setChecked(true);
        else
            // no former selection - default to black
            gnugo_plays_black_radio.setChecked(true);

        int level = shared_prefs.getInt(SP_KEY_STRENGTH, 0);
        strange_seek = ((SeekBar) this.findViewById(R.id.gnugo_strength_seek));
        strange_seek.setProgress(level);
        gnugo_strength_text.setText(getContext().getString(R.string.gnugo_strength) + String.valueOf(level));

        strange_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if(fromUser)
                    gnugo_strength_text.setText(getContext().getString(R.string.gnugo_strength) + String.valueOf(progress));
            }
        });
    }

    public boolean isWhiteActive() {
        return gnugo_plays_white_radio.isChecked();
    }

    public boolean isBlackActive() {
        return gnugo_plays_black_radio.isChecked();
    }

    public boolean isBothActive() {
        return gnugo_plays_both_radio.isChecked();
    }

    public int getStrength() {
        return strange_seek.getProgress();
    }

    public void saveRecentAsDefault() {
        SharedPreferences.Editor edit = shared_prefs.edit();
        edit.putInt(SP_KEY_STRENGTH, getStrength());

        edit.putBoolean(SP_KEY_PLAYS_WHITE, isWhiteActive());
        edit.putBoolean(SP_KEY_PLAYS_BLACK, isBlackActive());
        edit.putBoolean(SP_KEY_PLAYS_BOTH, isBothActive());

        edit.commit();
    }

}
