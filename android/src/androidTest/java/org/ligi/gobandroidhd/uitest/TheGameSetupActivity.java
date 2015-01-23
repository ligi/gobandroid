package org.ligi.gobandroidhd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.game_setup.GoSetupActivity;
import org.ligi.gobandroidhd.base.BaseIntegration;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.ligi.gobandroidhd.base.GoViewActions.setProgress;

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
    public void testThatClickOnButtonOpensCorrectGame() {
        final GoSetupActivity activity = getActivity();

        onView(withId(R.id.size_button9x9)).perform(click());
        onView(withId(R.id.game_size_label)).check(matches(withText(containsString("9x9"))));
        Spoon.screenshot(activity, "setup_9x9");

        onView(withId(R.id.size_button13x13)).perform(click());
        onView(withId(R.id.game_size_label)).check(matches(withText(containsString("13x13"))));
        Spoon.screenshot(activity, "setup_13x13");

        onView(withId(R.id.size_button19x19)).perform(click());
        onView(withId(R.id.game_size_label)).check(matches(withText(containsString("19x19"))));
        Spoon.screenshot(activity, "setup_19x19");
    }

    @MediumTest
    public void testThatSeekBarSetsSizeCorrectlyAndHandicapControlsBehaveAsWanted() {
        final GoSetupActivity activity = getActivity();

        onView(withId(R.id.size_seek)).perform(setProgress(1));
        onView(withId(R.id.game_size_label)).check(matches(withText(endsWith("3x3"))));
        onView(withId(R.id.handicap_seek)).check(matches(not(isEnabled())));
        onView(withId(R.id.handicap_label)).check(matches(withText(R.string.handicap_only_for)));
        Spoon.screenshot(activity, "setup_3x3_slider");

        onView(withId(R.id.size_seek)).perform(setProgress(0));
        onView(withId(R.id.game_size_label)).check(matches(withText(endsWith("2x2"))));
        onView(withId(R.id.handicap_seek)).check(matches(not(isEnabled())));
        onView(withId(R.id.handicap_label)).check(matches(withText(R.string.handicap_only_for)));
        Spoon.screenshot(activity, "setup_2x2_slider");

        onView(withId(R.id.size_seek)).perform(setProgress(21));
        onView(withId(R.id.game_size_label)).check(matches(withText(containsString("23x23"))));
        onView(withId(R.id.handicap_seek)).check(matches(not(isEnabled())));
        onView(withId(R.id.handicap_label)).check(matches(withText(R.string.handicap_only_for)));
        Spoon.screenshot(activity, "setup_23x23_slider");

        onView(withId(R.id.size_seek)).perform(setProgress(7));
        onView(withId(R.id.game_size_label)).check(matches(withText(containsString("9x9"))));
        onView(withId(R.id.handicap_seek)).check(matches(isEnabled()));
        onView(withId(R.id.handicap_label)).check(matches(not(withText(R.string.handicap_only_for))));
        Spoon.screenshot(activity, "setup_9x9_slider");
    }

}
