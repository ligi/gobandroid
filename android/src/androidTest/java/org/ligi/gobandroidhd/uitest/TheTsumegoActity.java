package org.ligi.gobandroidhd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoActivity;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;

public class TheTsumegoActity extends BaseIntegration<TsumegoActivity> {

    public TheTsumegoActity() {
        super(TsumegoActivity.class);
    }

    @MediumTest
    public void testThatNoTsumegoWarningComes() {
        final TsumegoActivity activity = getActivity();

        Spoon.screenshot(activity, "tsumego");
        onView(withText(R.string.tsumego_sgf_no_solution)).check(matches(isDisplayed()));
    }


}
