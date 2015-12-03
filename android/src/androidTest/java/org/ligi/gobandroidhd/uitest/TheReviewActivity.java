package org.ligi.gobandroidhd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.etc.AppModule;
import org.ligi.gobandroid_hd.model.GameProvider;
import org.ligi.gobandroid_hd.ui.review.GameReviewActivity;
import org.ligi.gobandroidhd.base.BaseIntegration;

import javax.inject.Inject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.assertj.core.api.Assertions.assertThat;

public class TheReviewActivity extends BaseIntegration<GameReviewActivity> {

    @Inject
    GameProvider gameProvider;
    
    public TheReviewActivity() {
        super(GameReviewActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        final TestComponent testComponent = DaggerTestComponent.builder().appModule(new AppModule((App) getInstrumentation().getTargetContext().getApplicationContext())).build();
        App.setComponent(testComponent);
        testComponent.inject(this);
    }



    @MediumTest
    public void testThatGoBoardIsThere() {
        gameProvider.set(readGame("small_19x19"));

        final GameReviewActivity activity = getActivity();

        Spoon.screenshot(activity, "review");
        onView(withId(R.id.go_board)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_next)).check(matches(isDisplayed()));
    }


    @MediumTest
    public void testThatNextAndLastControlsAreThereOnBeginning() {
        gameProvider.set(readGame("small_19x19"));

        getActivity();

        onView(withId(R.id.btn_next)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_last)).check(matches(isDisplayed()));
    }


    @MediumTest
    public void testThatAllControlsAreThereInTheMiddleOfTheGame() {
        gameProvider.set(readGame("small_19x19"));

        getActivity();

        onView(withId(R.id.btn_next)).perform(click());

        onView(withId(R.id.btn_next)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_last)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_prev)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_first)).check(matches(isDisplayed()));

    }


    @MediumTest
    public void testThatNextWorks() {
        gameProvider.set(readGame("small_19x19"));
        getActivity();

        onView(withId(R.id.btn_next)).perform(click());

        assertThat(gameProvider.get().getActMove().getMovePos()).isEqualTo(1);
        onView(withId(R.id.btn_next)).perform(click());

        assertThat(gameProvider.get().getActMove().getMovePos()).isEqualTo(2);

    }


    @MediumTest
    public void testThatLastAndFirstWorks() {
        gameProvider.set(readGame("small_19x19"));
        getActivity();

        onView(withId(R.id.btn_last)).perform(click());

        assertThat(gameProvider.get().getActMove().getNextMoveVariationCount()).isLessThan(1);

        onView(withId(R.id.btn_first)).perform(click());

        assertThat(gameProvider.get().getActMove().getParent()).isEqualTo(null);

    }


}
