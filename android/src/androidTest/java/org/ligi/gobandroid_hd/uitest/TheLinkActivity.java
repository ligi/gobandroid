package org.ligi.gobandroid_hd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.ui.links.LinksActivity;
import org.ligi.gobandroid_hd.base.BaseIntegration;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

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
