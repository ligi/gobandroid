package org.ligi.gobandroidhd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.ui.links.LinksActivity;
import org.ligi.gobandroidhd.base.BaseIntegration;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;

public class TheLinkActivity extends BaseIntegration<LinksActivity> {

    public TheLinkActivity() {
        super(LinksActivity.class);
    }

    @MediumTest
    public void testThatProjectPageEntryIsVisible() {
        Spoon.screenshot(getActivity(), "link_list");
        onView(withText("Gobandroid Project Page")).check(matches(isDisplayed()));
    }


}
