package org.ligi.gobandroid.ui;

import org.ligi.gobandroid.logic.IgsManager;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

public class IgsConsoleActivity 
		extends Activity
{
	private TextView message_tv;
	private ScrollView message_sv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		GoPrefs.init(this);		

		message_tv = new TextView(this);
		message_sv = new ScrollView(this);
		message_sv.addView(message_tv);
		
		message_tv.setTextSize((float) 8);
		message_tv.setTypeface(Typeface.MONOSPACE);
		message_tv.setText(IgsManager.getConsoleOutput());
	    setContentView(message_sv);
	}
}