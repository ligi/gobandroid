package org.ligi.gobandroidhd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.scoring.GameScoringActivity;
import org.ligi.gobandroidhd.base.BaseIntegration;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.ligi.gobandroidhd.base.GoViewActions.tapStone;

public class TheGameScoringActivity extends BaseIntegration<GameScoringActivity> {

    public TheGameScoringActivity() {
        super(GameScoringActivity.class);
    }

    @MediumTest
    public void testThatGoBoardIsThere() {
        final GameScoringActivity activity = getActivity();

        onView(withId(R.id.go_board)).check(matches(isDisplayed()));
        Spoon.screenshot(activity, "count");
    }

    @MediumTest
    public void testThatOneStoneOn9x9are80pt() {
        App.setGame(new GoGame((byte) 9));
        App.getGame().do_move((byte) 0, (byte) 0);

        final GameScoringActivity activity = getActivity();

        onView(withId(R.id.final_black)).check(matches(withText(containsString("80.0"))));
        onView(withId(R.id.captures_black)).check(matches(withText(containsString("0"))));
        onView(withId(R.id.result_txt)).check(matches(withText(containsString("Black"))));

        Spoon.screenshot(activity, "setup_init");
    }


    @MediumTest
    public void testThatTapToMarkWorks() {
        App.setGame(new GoGame((byte) 9));
        App.getGame().do_move((byte) 0, (byte) 0);
        App.getGame().do_move((byte) 0, (byte) 1);

        final GameScoringActivity activity = getActivity();

        onView(withId(R.id.go_board)).perform(tapStone(0, 1));

        onView(withId(R.id.final_black)).check(matches(withText(containsString("81.0"))));
        onView(withId(R.id.captures_black)).check(matches(withText(containsString("1"))));
        onView(withId(R.id.result_txt)).check(matches(withText(containsString("Black"))));

        Spoon.screenshot(activity, "setup_init");
    }

    @MediumTest
    public void testThatWhiteCanWin() {
        App.setGame(new GoGame((byte) 9));
        App.getGame().do_move((byte) 0, (byte) 0);
        App.getGame().do_move((byte) 0, (byte) 1);

        final GameScoringActivity activity = getActivity();

        onView(withId(R.id.go_board)).perform(tapStone(0, 0));

        onView(withId(R.id.final_white)).check(matches(withText(containsString("87.5"))));
        onView(withId(R.id.captures_black)).check(matches(withText(containsString("0"))));
        onView(withId(R.id.captures_white)).check(matches(withText(containsString("1"))));
        onView(withId(R.id.result_txt)).check(matches(withText(containsString("White"))));

        Spoon.screenshot(activity, "setup_init");
    }
}
