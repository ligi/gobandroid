package org.ligi.gobandroidhd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.etc.AppModule;
import org.ligi.gobandroid_hd.logic.GoGameMetadata;
import org.ligi.gobandroid_hd.model.GameProvider;
import org.ligi.gobandroid_hd.ui.review.GameReviewActivity;
import org.ligi.gobandroidhd.base.BaseIntegration;

import javax.inject.Inject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.not;

public class TheGameInfoDialog extends BaseIntegration<GameReviewActivity> {

    @Inject
    GameProvider gameProvider;

    public static final String CUSTOM_BLACK_RANK = "custom black rank";
    public static final String CUSTOM_BLACK_NAME = "custom black name";
    public static final String CUSTOM_GAME_NAME = "custom game name";
    public static final String CUSTOM_WHITE_NAME = "custom white name";
    public static final String CUSTOM_WHITE_RANK = "custom white rank";
    public static final String CUSTOM_KOMI = "4.2";

    public TheGameInfoDialog() {
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
    public void testThatTheDialogShows() {
        final GameReviewActivity activity = getActivity();
        onView(withId(R.id.menu_game_info)).perform(click());
        onView(withId(R.id.game_name_et)).check(matches(isDisplayed()));
        Spoon.screenshot(activity, "game_info_dialog");
    }

    @MediumTest
    public void testThatFieldsWork() {
        final GameReviewActivity activity = getActivity();
        onView(withId(R.id.menu_game_info)).perform(click());

        onView(withId(R.id.game_name_et)).perform(replaceText(CUSTOM_GAME_NAME));

        onView(withId(R.id.black_rank_et)).perform(replaceText(CUSTOM_BLACK_RANK));

        onView(withId(R.id.user_is_black_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.black_name_et)).perform(replaceText(CUSTOM_BLACK_NAME));
        onView(withId(R.id.user_is_black_btn)).check(matches(not(isDisplayed())));

        onView(withId(R.id.white_rank_et)).perform(scrollTo());
        onView(withId(R.id.white_rank_et)).perform(replaceText(CUSTOM_WHITE_RANK));
        onView(withId(R.id.white_name_et)).perform(replaceText(CUSTOM_WHITE_NAME));


        onView(withId(R.id.komi_et)).perform(scrollTo(),clearText() ,replaceText(CUSTOM_KOMI));

        onView(withId(R.id.game_result_et)).perform(scrollTo(),clearText() ,replaceText(CUSTOM_KOMI),closeSoftKeyboard());

        sleep(100);

        onView(withText(android.R.string.ok)).perform(scrollTo(),click());

        final GoGameMetadata metaData = gameProvider.get().getMetaData();

        assertThat(metaData.getName()).isEqualTo(CUSTOM_GAME_NAME);
        assertThat(metaData.getWhiteName()).isEqualTo(CUSTOM_WHITE_NAME);
        assertThat(metaData.getWhiteRank()).isEqualTo(CUSTOM_WHITE_RANK);
        assertThat(metaData.getBlackName()).isEqualTo(CUSTOM_BLACK_NAME);
        assertThat(metaData.getBlackRank()).isEqualTo(CUSTOM_BLACK_RANK);
        assertThat(gameProvider.get().getKomi()).isEqualTo(Float.valueOf(CUSTOM_KOMI));

        Spoon.screenshot(activity, "game_info_dialog");
    }

    @MediumTest
    public void testThatBadKomiIsRejected() {
        gameProvider.get().setKomi(Float.valueOf(CUSTOM_KOMI));
        final GameReviewActivity activity = getActivity();
        onView(withId(R.id.menu_game_info)).perform(click());
        onView(withId(R.id.komi_et)).perform(scrollTo(),clearText() ,typeText("a"),closeSoftKeyboard());

        sleep(100);

        onView(withText(android.R.string.ok)).perform(scrollTo(),click());

        onView(withText(R.string.komi_must_be_a_number)).check(matches(isDisplayed()));

        assertThat(gameProvider.get().getKomi()).isEqualTo(Float.valueOf(CUSTOM_KOMI));
        Spoon.screenshot(activity, "game_info_reject_komi");
    }

}
