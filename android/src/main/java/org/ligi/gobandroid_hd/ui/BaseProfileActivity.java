package org.ligi.gobandroid_hd.ui;

import android.os.Bundle;
import android.widget.EditText;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BaseProfileActivity extends GobandroidFragmentActivity {

    @InjectView(R.id.username_edit)
    EditText username_et;

    @InjectView(R.id.rank_edit)
    EditText rank_et;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        setTitle(R.string.profile);

        getSupportActionBar().setDisplayShowTitleEnabled(true);

        ButterKnife.inject(this);

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
