package org.ligi.gobandroid_hd.ui;

import java.io.File;
import java.util.Vector;

import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.review.GameReviewActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class GobanDroidTVActivity extends GobandroidFragmentActivity {

	private Vector<String> avail_file_list;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		GoInteractionProvider.setMode(GoInteractionProvider.MODE_TELEVIZE);
		Intent start_review_intent=new Intent(this,SGFLoadActivity.class);
		
		File path_to_play_from=new File("/sdcard/gobandroid/sgf/review/pro_games/");
		
		avail_file_list=new Vector<String>();
		String choosen;
		
		for (File act : path_to_play_from.listFiles()) {
			if (act.getAbsolutePath().endsWith(".sgf"))
					avail_file_list.add(act.getAbsolutePath());
		}
			 
		choosen=avail_file_list.get((int)(Math.random()*avail_file_list.size()));
		
		start_review_intent.setData(Uri.parse( "file://"+choosen));
		this.startActivity(start_review_intent);

		super.onResume();
	}

}
