package org.ligi.gobandroidhd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.review.GameReviewActivity;
import org.ligi.gobandroidhd.base.BaseIntegration;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isEnabled;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.not;

public class TheReviewActivity extends BaseIntegration<GameReviewActivity> {

    public TheReviewActivity() {
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

    @MediumTest
    public void testThatNextWorks() {
        App.setGame(readGame("small_19x19"));
        getActivity();

        onView(withId(R.id.btn_next)).perform(click());

        assertThat(App.getGame().getActMove().getMovePos()).isEqualTo(1);
        onView(withId(R.id.btn_next)).perform(click());

        assertThat(App.getGame().getActMove().getMovePos()).isEqualTo(2);

    }


    @MediumTest
    public void testThatLastAndFirstWorks() {
        App.setGame(readGame("small_19x19"));
        getActivity();

        onView(withId(R.id.btn_last)).perform(click());

        assertThat(App.getGame().getActMove().getNextMoveVariationCount()).isLessThan(1);

        onView(withId(R.id.btn_first)).perform(click());

        assertThat(App.getGame().getActMove().getParent()).isEqualTo(null);

    }



}
