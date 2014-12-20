package org.ligi.gobandroidhd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoActivity;
import org.ligi.gobandroidhd.base.BaseIntegration;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.ligi.gobandroidhd.base.GoViewActions.placeStone;

public class TheTsumegoActivity extends BaseIntegration<TsumegoActivity> {

    public TheTsumegoActivity() {
        super(TsumegoActivity.class);
    }

    @MediumTest
    public void testThatNoTsumegoWarningComes() {
        App.setGame(readGame("default_marker"));
        final TsumegoActivity activity = getActivity();

        onView(withText(R.string.tsumego_sgf_no_solution)).check(matches(isDisplayed()));
        Spoon.screenshot(activity, "tsumego_fail");
    }

    @MediumTest
    public void testThatOffPathMessageIsNotThereInBeginning() {
        App.setGame(readGame("tsumego"));
        final TsumegoActivity activity = getActivity();

        onView(withId(R.id.tsumego_off_path_view)).check(matches(not(isDisplayed())));
        Spoon.screenshot(activity, "tsumego_on_path");
    }

    @MediumTest
    public void testThatOffPathMessageComes() {
        App.setGame(readGame("tsumego"));
        final TsumegoActivity activity = getActivity();

        onView(withId(R.id.go_board)).check(matches(not(isDisplayed())));
        onView(withId(R.id.go_board)).perform(placeStone(1, 1));

        onView(withId(R.id.tsumego_off_path_view)).check(matches(isDisplayed()));
        Spoon.screenshot(activity, "tsumego_off_path");
    }

    @MediumTest
    public void testThatCommentComesAndGoes() {
        App.setGame(readGame("tsumego"));
        final TsumegoActivity activity = getActivity();
        onView(withId(R.id.game_comment)).check(matches(withText("testing comment")));

        Spoon.screenshot(activity, "tsumego_comment");
        onView(withId(R.id.go_board)).check(matches(not(isDisplayed())));
        onView(withId(R.id.go_board)).perform(placeStone(1, 1));

        onView(withId(R.id.game_comment)).check(matches(not(isDisplayed())));
        Spoon.screenshot(activity, "tsumego_comment_gone");
    }



}
