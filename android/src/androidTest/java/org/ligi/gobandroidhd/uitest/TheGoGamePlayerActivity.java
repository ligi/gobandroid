package org.ligi.gobandroidhd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.review.GoGamePlayerActivity;
import org.ligi.gobandroidhd.base.BaseIntegration;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

public class TheGoGamePlayerActivity extends BaseIntegration<GoGamePlayerActivity> {

    public TheGoGamePlayerActivity() {
        super(GoGamePlayerActivity.class);
    }

    @MediumTest
    public void testThatNoTsumegoWarningComes() {
        final GoGamePlayerActivity activity = getActivity();

        Spoon.screenshot(activity, "gogamelayer");
        onView(withId(R.id.go_board)).check(matches(isDisplayed()));
    }


}
