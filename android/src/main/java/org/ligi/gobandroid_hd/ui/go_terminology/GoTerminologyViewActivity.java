package org.ligi.gobandroid_hd.ui.go_terminology;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;

import org.ligi.axt.listeners.ActivityFinishingOnClickListener;
import org.ligi.gobandroid_hd.R;

import java.util.HashMap;

public class GoTerminologyViewActivity extends Activity {

    public final static HashMap<String, Integer> Term2resMap = new HashMap<String, Integer>() {{
        put("joseki", R.string.goterm_joseki);
        put("miai", R.string.goterm_miai);
        put("shape", R.string.goterm_shape);
        put("tesuji", R.string.goterm_tesuji);
        // TODO add missing mojo
    }};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.empty);
        // NaDra setBehindContentView(R.layout.empty);

        final String term = this.getIntent().getData().getLastPathSegment();

        final GoTerminologyDialog dialog = new GoTerminologyDialog(this, term);
        dialog.setPositiveButton(android.R.string.ok, new ActivityFinishingOnClickListener(this));
        dialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }

        });
        dialog.show();

    }
}
