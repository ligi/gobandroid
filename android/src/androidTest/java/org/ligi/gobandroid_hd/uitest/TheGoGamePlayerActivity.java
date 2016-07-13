package org.ligi.gobandroid_hd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.TestApp;
import org.ligi.gobandroid_hd.etc.AppModule;
import org.ligi.gobandroid_hd.model.GameProvider;
import org.ligi.gobandroid_hd.ui.review.GoGamePlayerActivity;
import org.ligi.gobandroid_hd.base.BaseIntegration;

import javax.inject.Inject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class TheGoGamePlayerActivity extends BaseIntegration<GoGamePlayerActivity> {

    @Inject
    GameProvider gameProvider;

    public TheGoGamePlayerActivity() {
        super(GoGamePlayerActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        TestApp.component().inject(this);
    }

    @MediumTest
    public void testThatGamePlayerStartsWithNoGame() {
        final GoGamePlayerActivity activity = getActivity();

        onView(withId(R.id.go_board)).check(matches(isDisplayed()));

        Spoon.screenshot(activity, "gogameplayer_empty");
    }


    @MediumTest
    public void testThatGamePlayerStartsWithCommentedGame() {
        gameProvider.set(readGame("commented"));
        final GoGamePlayerActivity activity = getActivity();

        Spoon.screenshot(activity, "gogameplayer_commented");
        onView(withId(R.id.go_board)).check(matches(isDisplayed()));
    }


}
