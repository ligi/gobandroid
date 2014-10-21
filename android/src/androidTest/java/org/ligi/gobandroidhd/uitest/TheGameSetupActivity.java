package org.ligi.gobandroidhd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.game_setup.GoSetupActivity;
import org.ligi.gobandroidhd.base.BaseIntegration;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

public class TheGameSetupActivity extends BaseIntegration<GoSetupActivity> {

    public TheGameSetupActivity() {
        super(GoSetupActivity.class);
    }

    @MediumTest
    public void testThatGoBoardIsThere() {
        final GoSetupActivity activity = getActivity();

        Spoon.screenshot(activity, "setup");
        onView(withId(R.id.go_board)).check(matches(isDisplayed()));
    }

    @MediumTest
    public void testThatClickOn9x9createsA9by9game() {
        final GoSetupActivity activity = getActivity();

        onView(withId(R.id.size_button9x9)).perform(click());
        onView(withId(R.id.game_size_label)).check(matches(withText(containsString("9x9"))));

        Spoon.screenshot(activity, "setup_9x9");
    }


    @MediumTest
    public void testThatClickOn13x13createsA13by13game() {
        final GoSetupActivity activity = getActivity();

        onView(withId(R.id.size_button13x13)).perform(click());
        onView(withId(R.id.game_size_label)).check(matches(withText(containsString("13x13"))));

        Spoon.screenshot(activity, "setup_13x13");
    }

    @MediumTest
    public void testThatClickOn19x19createsA19by19game() {
        final GoSetupActivity activity = getActivity();

        onView(withId(R.id.size_button19x19)).perform(click());
        onView(withId(R.id.game_size_label)).check(matches(withText(containsString("19x19"))));
        Spoon.screenshot(activity, "setup_19x19");

    }

}
