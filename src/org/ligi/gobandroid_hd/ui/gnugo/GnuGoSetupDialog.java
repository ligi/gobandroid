package org.ligi.gobandroid_hd.ui.gnugo;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;
import android.content.Context;
import android.widget.RadioButton;
import android.widget.SeekBar;

public class GnuGoSetupDialog extends GobandroidDialog {

	public GnuGoSetupDialog(Context context) {
		super(context);
		setTitle(R.string.gnugo);
		setIconResource(R.drawable.preferences);
		setContentFill();
		setContentView(R.layout.setup_gnugo);
		
		((RadioButton)this.findViewById(R.id.gnugo_plays_black_radio)).setChecked(true);
	}
	
	
	public boolean isWhiteActive() {
		return ((RadioButton)this.findViewById(R.id.gnugo_plays_white_radio)).isChecked();
	}
	
	public boolean isBlackActive() {
		return ((RadioButton)this.findViewById(R.id.gnugo_plays_black_radio)).isChecked();
	}
	
	public boolean isBothActive() {
		return ((RadioButton)this.findViewById(R.id.gnugo_plays_both_radio)).isChecked();
	}
	
	public int getStrength() {
		return ((SeekBar)this.findViewById(R.id.gnugo_strength_seek)).getProgress();
	}

}
