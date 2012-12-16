package org.ligi.gobandroid_hd.ui.go_terminology;

import java.util.HashMap;

import org.ligi.gobandroid_beta.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class GoTerminologyViewActivity extends GobandroidFragmentActivity {

	public final static HashMap<String, Integer> getTerm2resHashMap() {
		return new HashMap<String, Integer>() {
			private static final long serialVersionUID = 6567307459292165743L;

			{
				put("joseki", R.string.goterm_joseki);
				put("miai", R.string.goterm_miai);
				put("shape", R.string.goterm_shape);
				put("tesuji", R.string.goterm_tesuji);
				// TODO add missing mojo
			}
		};
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		String term = this.getIntent().getData().getLastPathSegment();
		GoTerminologyDialog dialog = new GoTerminologyDialog(this, term);
		dialog.setPositiveButton(android.R.string.ok, new MyOnClickListener());
		dialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}

		});
		dialog.show();

		super.onCreate(savedInstanceState);
	}

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			finish();
		}

	}
}
