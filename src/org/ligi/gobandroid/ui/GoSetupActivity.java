/**
 * gobandroid 
 * by Marcus -Ligi- Bueschleb 
 * http://ligi.de
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as 
 * published by the Free Software Foundation; 
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 **/

package org.ligi.gobandroid.ui;

import org.ligi.gobandroid.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;


/**
 * Activity for a Game
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 *         This software is licenced with GPLv3
 * 
 **/

public class GoSetupActivity extends Activity implements OnSeekBarChangeListener, OnClickListener{

	private byte act_size=9;
	private byte act_handicap=0;
	
	private final static int size_offset=2;
	
	private SeekBar size_seek;
	private SeekBar handicap_seek;
	
	private TextView size_text;
	private Button size_button9x9;
	private Button size_button13x13;
	private Button size_button19x19;
	private Button start_button;
	
	
	private TextView handicap_text;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		GoPrefs.init(this);
		
		this.setContentView(R.layout.game_setup);
		
		size_seek=(SeekBar)this.findViewById(R.id.size_slider);
		size_seek.setOnSeekBarChangeListener(this);
		
		size_text=(TextView)this.findViewById(R.id.game_size_label);
		
		size_button9x9=(Button)this.findViewById(R.id.size_button9x9);
		size_button9x9.setOnClickListener(this);


		size_button13x13=(Button)this.findViewById(R.id.size_button13x13);
		size_button13x13.setOnClickListener(this);

		
		size_button19x19=(Button)this.findViewById(R.id.size_button19x19);
		size_button19x19.setOnClickListener(this);
		
		
		start_button=(Button)this.findViewById(R.id.game_start_button);
		start_button.setOnClickListener(this);
		
		
		handicap_text=(TextView)this.findViewById(R.id.handicap_label);
		handicap_seek=(SeekBar)this.findViewById(R.id.handicap_seek);
		
		handicap_seek.setOnSeekBarChangeListener(this);
		
		refresh_ui();
	}
	
	public void refresh_ui() {
		size_text.setText("Size "+act_size+"x"+act_size);
		handicap_text.setText("Handicap " + act_handicap);
	
		size_seek.setProgress(act_size-size_offset);

		handicap_seek.setEnabled((act_size==9)||(act_size==13)||(act_size==19));
	}
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (seekBar==size_seek) 
			act_size=(byte)(progress+size_offset);
		else if (seekBar==handicap_seek)
			act_handicap=(byte)progress;
		
		refresh_ui();
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}
	@Override
	public void onClick(View v) {
		if (v==size_button9x9)
			act_size=9;
		if (v==size_button13x13)
			act_size=13;
		else if (v==size_button19x19)
			act_size=19;
		else if (v==start_button) {
			 Intent go_intent=new Intent(this,GoActivity.class);
             go_intent.putExtra("size",act_size );
             go_intent.putExtra("handicap",act_handicap );
             startActivity(go_intent);
		}
		
		refresh_ui();
	}
	
}