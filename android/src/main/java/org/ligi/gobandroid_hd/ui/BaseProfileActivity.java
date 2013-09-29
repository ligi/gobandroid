package org.ligi.gobandroid_hd.ui;

import android.os.Bundle;
import android.widget.EditText;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

public class BaseProfileActivity extends GobandroidFragmentActivity {

    private EditText username_et;
    private EditText rank_et;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        username_et = (EditText) findViewById(R.id.username_edit);
        rank_et = (EditText) findViewById(R.id.rank_edit);

        rank_et.setText(getApp().getSettings().getRank());
        username_et.setText(getApp().getSettings().getUsername());

    }


    @Override
    protected void onPause() {
        getApp().getSettings().setRank(rank_et.getText().toString());
        getApp().getSettings().setUsername(username_et.getText().toString());
        super.onPause();
    }

}
