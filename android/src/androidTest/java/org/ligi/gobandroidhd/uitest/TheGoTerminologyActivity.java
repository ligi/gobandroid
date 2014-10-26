package org.ligi.gobandroidhd.uitest;

import android.content.Intent;
import android.net.Uri;
import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.ui.go_terminology.GoTerminologyViewActivity;
import org.ligi.gobandroidhd.base.BaseIntegration;

public class TheGoTerminologyActivity extends BaseIntegration<GoTerminologyViewActivity> {

    public TheGoTerminologyActivity() {
        super(GoTerminologyViewActivity.class);
    }

    @MediumTest
    public void testThatGoBoardIsThere() {
        final Intent intent = new Intent();
        intent.setData(Uri.parse("goterm://miai"));
        setActivityIntent(intent);
        final GoTerminologyViewActivity activity = getActivity();

        Spoon.screenshot(activity, "go_terms");
    }


}
