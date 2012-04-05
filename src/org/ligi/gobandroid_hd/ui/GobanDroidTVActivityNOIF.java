package org.ligi.gobandroid_hd.ui;


import android.content.Intent;
import android.os.Bundle;

public class GobanDroidTVActivityNOIF extends GobanDroidTVActivity {


	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		GoInteractionProvider.setIs_in_noif_mode(true);
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public Intent getIntent2start() {
		return new Intent(this,GobanDroidTVActivity.class);
	}
}
