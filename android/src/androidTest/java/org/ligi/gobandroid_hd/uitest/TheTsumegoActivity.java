package org.ligi.gobandroid_hd.uitest;

import android.support.test.filters.MediumTest;
import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.TestApp;
import org.ligi.gobandroid_hd.etc.AppModule;
import org.ligi.gobandroid_hd.logic.CellImpl;
import org.ligi.gobandroid_hd.model.GameProvider;
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoActivity;
import org.ligi.gobandroid_hd.base.BaseIntegration;

import javax.inject.Inject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.ligi.gobandroid_hd.base.GoViewActions.placeStone;

public class TheTsumegoActivity extends BaseIntegration<TsumegoActivity> {

    @Inject
    GameProvider gameProvider;

    public TheTsumegoActivity() {
        super(TsumegoActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        TestApp.component().inject(this);
    }

    @MediumTest
    public void testThatNoTsumegoWarningComes() {
        gameProvider.set(readGame("default_marker"));
        final TsumegoActivity activity = getActivity();

        onView(withText(R.string.tsumego_sgf_no_solution)).check(matches(isDisplayed()));
        Spoon.screenshot(activity, "tsumego_fail");
    }

    @MediumTest
    public void testThatOffPathMessageIsNotThereInBeginning() {
        gameProvider.set(readGame("tsumego"));
        final TsumegoActivity activity = getActivity();

        onView(withId(R.id.tsumego_off_path_view)).check(matches(not(isDisplayed())));
        Spoon.screenshot(activity, "tsumego_on_path");
    }

    @MediumTest
    public void testThatOffPathMessageComes() {
        gameProvider.set(readGame("tsumego"));
        final TsumegoActivity activity = getActivity();

        onView(withId(R.id.go_board)).perform(placeStone(new CellImpl(1, 1), gameProvider.get()));

        onView(withId(R.id.tsumego_off_path_view)).check(matches(isDisplayed()));
        Spoon.screenshot(activity, "tsumego_off_path");
    }

    @MediumTest
    public void testThatCommentComesAndGoes() {
        gameProvider.set(readGame("tsumego"));
        final TsumegoActivity activity = getActivity();
        onView(withId(R.id.game_comment)).check(matches(withText("testing comment")));

        Spoon.screenshot(activity, "tsumego_comment");
        onView(withId(R.id.go_board)).perform(placeStone(new CellImpl(1, 1), gameProvider.get()));

        onView(withId(R.id.game_comment)).check(matches(not(isDisplayed())));
        Spoon.screenshot(activity, "tsumego_comment_gone");
    }



}
