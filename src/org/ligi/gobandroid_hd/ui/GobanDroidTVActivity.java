package org.ligi.gobandroid_hd.ui;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Vector;

import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * TODO subdirs
 * 
 * @author ligi
 *
 */
public class GobanDroidTVActivity extends GobandroidFragmentActivity {

	private Vector<String> avail_file_list;
	private File path_to_play_from;
	
	public Intent getIntent2start() {
		return new Intent(this,GobanDroidTVActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getApp().getInteractionScope().setMode(InteractionScope.MODE_TELEVIZE);

		getSupportActionBar().setLogo(R.drawable.gobandroid_tv);
		path_to_play_from=new File(getSettings().getReviewPath()+"/commented_games/");
		
		getTracker().trackPageView("/gtv");
		if (path_to_play_from.listFiles()==null) {
			getTracker().trackPageView("/gtv/unzip");
			UnzipSGFsDialog.show(this,getIntent2start());
		} else {
			startTV();
		}

	}

	private void startTV() {
		
		Intent start_review_intent=new Intent(this,SGFLoadActivity.class);
		
		avail_file_list=new Vector<String>();
		String choosen;
		
		for (File act : path_to_play_from.listFiles()) {
			if (act.getAbsolutePath().endsWith(".sgf"))
					avail_file_list.add(act.getAbsolutePath());
		}
			 
		choosen=avail_file_list.get((int)(Math.random()*avail_file_list.size()));
		
		start_review_intent.setData(Uri.parse( "file://"+choosen));
		try {
			getTracker().trackPageView("/gtv/play"+URLEncoder.encode(choosen,"UTF-8"));
		} catch (UnsupportedEncodingException e) {	}
		this.startActivity(start_review_intent);

		finish();

	}

	@Override
	protected void onNewIntent(Intent intent) {
		startTV();
		super.onNewIntent(intent);
	}
}
