package org.ligi.gobandroid_hd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.ui.sgf_listing.SGFFileSystemListActivity;
import org.ligi.gobandroid_hd.base.BaseIntegration;

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
