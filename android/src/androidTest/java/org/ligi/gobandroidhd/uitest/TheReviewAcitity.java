package org.ligi.gobandroidhd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.review.GameReviewActivity;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isEnabled;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;

public class TheReviewAcitity extends BaseIntegration<GameReviewActivity> {

    public TheReviewAcitity() {
        super(GameReviewActivity.class);
    }

    @MediumTest
    public void testThatGoBoardIsThere() {
        final GameReviewActivity activity = getActivity();

        Spoon.screenshot(activity, "review");
        onView(withId(R.id.go_board)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_next)).check(matches(isDisplayed()));
    }


    @MediumTest
    public void testThatControlsAreThere() {
        getActivity();

        onView(withId(R.id.btn_next)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_prev)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_last)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_first)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_prev)).check(matches(not(isEnabled())));
    }


}
