package org.ligi.gobandroid_hd.ui;

import java.util.HashMap;

import org.ligi.android.common.views.ActivityFinishOnViewClickListener;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.tracedroid.logging.Log;

import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.Button;
import android.widget.TextView;

public class GoTermsViewActivity extends GobandroidFragmentActivity {

	
	public final static HashMap<String,Integer> getTerm2resHashMap() {
		HashMap<String,Integer>  res=new HashMap<String,Integer>();
		res.put("joseki", R.string.goterm_joseki);
		res.put("miai", R.string.goterm_miai);
		res.put("shape", R.string.goterm_shape);
		res.put("tesuji",R.string.goterm_tesuji);
		return res;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		String term=this.getIntent().getData().getLastPathSegment();
		this.setTitle(term);
		
		this.setContentView(R.layout.go_terms_view);
		TextView tv=(TextView)this.findViewById(R.id.go_terms_text);
		
		if (getTerm2resHashMap().containsKey(term))
			tv.setText(getTerm2resHashMap().get(term));
		else {
			tv.setText("no Definition found");
			Log.w("no definition found for " + term);
		}
		
		Linkify.addLinks(tv, Linkify.ALL);

		((Button)this.findViewById(R.id.terms_ok_btn)).setOnClickListener(new ActivityFinishOnViewClickListener(this));
		
		super.onCreate(savedInstanceState);
		
	}

}
