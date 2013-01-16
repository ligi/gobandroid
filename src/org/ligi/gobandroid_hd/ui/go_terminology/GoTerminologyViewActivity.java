package org.ligi.gobandroid_hd.ui.go_terminology;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

import java.util.HashMap;

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
        super.onCreate(savedInstanceState);

        setContentView(R.layout.empty);
        setBehindContentView(R.layout.empty);

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


    }

    class MyOnClickListener implements OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            finish();
        }

    }
}
