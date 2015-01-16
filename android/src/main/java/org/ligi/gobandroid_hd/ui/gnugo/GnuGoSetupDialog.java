package org.ligi.gobandroid_hd.ui.gnugo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GnuGoSetupDialog extends GobandroidDialog {

    private static final String SP_KEY_PLAYS_BLACK = "plays_black";
    private static final String SP_KEY_PLAYS_WHITE = "plays_white";
    private static final String SP_KEY_PLAYS_BOTH = "plays_both";

    private static final String SP_KEY_STRENGTH = "strength";

    private SharedPreferences shared_prefs;

    @InjectView(R.id.gnugo_plays_white_radio)
    RadioButton gnugo_plays_white_radio;

    @InjectView(R.id.gnugo_plays_black_radio)
    RadioButton gnugo_plays_black_radio;

    @InjectView(R.id.gnugo_plays_both_radio)
    RadioButton gnugo_plays_both_radio;

    @InjectView(R.id.gnugo_strength)
    TextView gnugo_strength_text;

    @InjectView(R.id.gnugo_strength_seek)
    SeekBar strengthSeek;

    public GnuGoSetupDialog(Context context) {
        super(context);

        shared_prefs = PreferenceManager.getDefaultSharedPreferences(context);

        setTitle(R.string.gnugo);
        setIconResource(R.drawable.preferences);

        setContentView(R.layout.setup_gnugo);

        ButterKnife.inject(this);

        if (shared_prefs.getBoolean(SP_KEY_PLAYS_BOTH, false)) {
            gnugo_plays_both_radio.setChecked(true);
        } else if (shared_prefs.getBoolean(SP_KEY_PLAYS_WHITE, false)) {
            gnugo_plays_white_radio.setChecked(true);
        } else if (shared_prefs.getBoolean(SP_KEY_PLAYS_BLACK, false)) {
            gnugo_plays_black_radio.setChecked(true);
        } else {
            // no former selection - default to black
            gnugo_plays_black_radio.setChecked(true);
        }

        final int level = shared_prefs.getInt(SP_KEY_STRENGTH, 0);
        strengthSeek.setProgress(level);
        gnugo_strength_text.setText(getContext().getString(R.string.gnugo_strength) + " " + String.valueOf(level));

        strengthSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (fromUser) {
                    gnugo_strength_text.setText(getContext().getString(R.string.gnugo_strength) + String.valueOf(progress));
                }
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
        return strengthSeek.getProgress();
    }

    public void saveRecentAsDefault() {
        final SharedPreferences.Editor edit = shared_prefs.edit();
        edit.putInt(SP_KEY_STRENGTH, getStrength());

        edit.putBoolean(SP_KEY_PLAYS_WHITE, isWhiteActive());
        edit.putBoolean(SP_KEY_PLAYS_BLACK, isBlackActive());
        edit.putBoolean(SP_KEY_PLAYS_BOTH, isBothActive());

        edit.apply();
    }

}
