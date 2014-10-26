package org.ligi.gobandroidhd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.App;
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
    public void testThatGamePlayerStartsWithNoGame() {
        final GoGamePlayerActivity activity = getActivity();

        onView(withId(R.id.go_board)).check(matches(isDisplayed()));

        Spoon.screenshot(activity, "gogameplayer_empty");
    }


    @MediumTest
    public void testThatGamePlayerStartsWithCommentedGame() {
        App.setGame(readGame("commented"));
        final GoGamePlayerActivity activity = getActivity();

        Spoon.screenshot(activity, "gogameplayer_commented");
        onView(withId(R.id.go_board)).check(matches(isDisplayed()));
    }


}
