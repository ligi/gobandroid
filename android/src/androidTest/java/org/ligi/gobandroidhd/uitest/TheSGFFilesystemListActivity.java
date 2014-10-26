package org.ligi.gobandroidhd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.ui.sgf_listing.SGFFileSystemListActivity;
import org.ligi.gobandroidhd.base.BaseIntegration;

public class TheSGFFilesystemListActivity extends BaseIntegration<SGFFileSystemListActivity> {

    public TheSGFFilesystemListActivity() {
        super(SGFFileSystemListActivity.class);
    }

    @MediumTest
    public void testThatListIsThere() {
        final SGFFileSystemListActivity activity = getActivity();

        Spoon.screenshot(activity, "list");
    }


}
