package org.ligi.gobandroidhd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.review.GameReviewActivity;
import org.ligi.gobandroidhd.base.BaseIntegration;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

public class TheGameInfoDialog extends BaseIntegration<GameReviewActivity> {

    public TheGameInfoDialog() {
        super(GameReviewActivity.class);
    }

    @MediumTest
    public void testThatTheDialogShows() {
        final GameReviewActivity activity = getActivity();
        onView(withId(R.id.menu_game_info)).perform(click());
        onView(withId(R.id.game_name_et)).check(matches(isDisplayed()));
        Spoon.screenshot(activity, "game_info_dialog");
    }

}
