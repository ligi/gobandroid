package org.ligi.gobandroidhd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGameMetadata;
import org.ligi.gobandroid_hd.ui.review.GameReviewActivity;
import org.ligi.gobandroidhd.base.BaseIntegration;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.not;

public class TheGameInfoDialog extends BaseIntegration<GameReviewActivity> {

    public static final String CUSTOM_BLACK_RANK = "custom black rank";
    public static final String CUSTOM_BLACK_NAME = "custom black name";
    public static final String CUSTOM_GAME_NAME = "custom game name";
    public static final String CUSTOM_WHITE_NAME = "custom white name";
    public static final String CUSTOM_WHITE_RANK = "custom white rank";
    public static final String CUSTOM_KOMI = "4.2";

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

    @MediumTest
    public void testThatFieldsWork() {
        final GameReviewActivity activity = getActivity();
        onView(withId(R.id.menu_game_info)).perform(click());

        onView(withId(R.id.game_name_et)).perform(typeText(CUSTOM_GAME_NAME));

        onView(withId(R.id.black_rank_et)).perform(typeText(CUSTOM_BLACK_RANK));

        onView(withId(R.id.user_is_black_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.black_name_et)).perform(typeText(CUSTOM_BLACK_NAME));
        onView(withId(R.id.user_is_black_btn)).check(matches(not(isDisplayed())));

        onView(withId(R.id.white_rank_et)).perform(typeText(CUSTOM_WHITE_RANK));
        onView(withId(R.id.white_name_et)).perform(typeText(CUSTOM_WHITE_NAME));


        onView(withId(R.id.komi_et)).perform(scrollTo(),clearText() ,typeText(CUSTOM_KOMI));

        onView(withId(R.id.game_result_et)).perform(scrollTo(),clearText() ,typeText(CUSTOM_KOMI),closeSoftKeyboard());

        sleep(100);

        onView(withText(android.R.string.ok)).check(matches(isDisplayed()));
        onView(withText(android.R.string.ok)).perform(click());

        final GoGameMetadata metaData = App.getGame().getMetaData();

        assertThat(metaData.getName()).isEqualTo(CUSTOM_GAME_NAME);
        assertThat(metaData.getWhiteName()).isEqualTo(CUSTOM_WHITE_NAME);
        assertThat(metaData.getWhiteRank()).isEqualTo(CUSTOM_WHITE_RANK);
        assertThat(metaData.getBlackName()).isEqualTo(CUSTOM_BLACK_NAME);
        assertThat(metaData.getBlackRank()).isEqualTo(CUSTOM_BLACK_RANK);
        assertThat(App.getGame().getKomi()).isEqualTo(Float.valueOf(CUSTOM_KOMI));

        Spoon.screenshot(activity, "game_info_dialog");
    }

    @MediumTest
    public void testThatBadKomiIsRejected() {
        App.getGame().setKomi(Float.valueOf(CUSTOM_KOMI));
        final GameReviewActivity activity = getActivity();
        onView(withId(R.id.menu_game_info)).perform(click());
        onView(withId(R.id.komi_et)).perform(scrollTo(),clearText() ,typeText("a"),closeSoftKeyboard());

        sleep(100);

        onView(withText(android.R.string.ok)).check(matches(isDisplayed()));
        onView(withText(android.R.string.ok)).perform(click());

        onView(withText(R.string.komi_must_be_a_number)).check(matches(isDisplayed()));

        assertThat(App.getGame().getKomi()).isEqualTo(Float.valueOf(CUSTOM_KOMI));
        Spoon.screenshot(activity, "game_info_reject_komi");
    }

}
