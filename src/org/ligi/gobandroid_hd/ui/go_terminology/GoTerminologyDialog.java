package org.ligi.gobandroid_hd.ui.go_terminology;

import java.util.HashMap;

import org.ligi.gobandroid_beta.R;
import org.ligi.gobandroid_hd.ui.GobandroidDialog;
import org.ligi.tracedroid.logging.Log;

import android.app.Activity;
import android.text.util.Linkify;
import android.widget.TextView;

public class GoTerminologyDialog extends GobandroidDialog {

	public final static HashMap<String, Integer> getTerm2resHashMap() {
		return new HashMap<String, Integer>() {
			private static final long serialVersionUID = -8206448225904656388L;
			{
				put("joseki", R.string.goterm_joseki);
				put("miai", R.string.goterm_miai);
				put("shape", R.string.goterm_shape);
				put("tesuji", R.string.goterm_tesuji);
				// missing mojo
			}
		};
	}

	public GoTerminologyDialog(Activity context, String term) {
		super(context);

		setTitle(term);
		setIconResource(R.drawable.info);
		setContentView(R.layout.go_terms_view);

		TextView tv = (TextView) this.findViewById(R.id.go_terms_text);

		if (getTerm2resHashMap().containsKey(term))
			tv.setText(getTerm2resHashMap().get(term));
		else {
			tv.setText("no Definition found");
			Log.w("no definition found for " + term);
		}

		Linkify.addLinks(tv, Linkify.ALL);

		// ((Button)this.findViewById(R.id.terms_ok_btn)).setOnClickListener(new
		// ActivityFinishOnViewClickListener(this));

	}

}
