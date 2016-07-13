package org.ligi.gobandroid_hd.uitest;

import android.os.SystemClock;
import android.test.suitebuilder.annotation.MediumTest;
import com.squareup.spoon.Spoon;
import javax.inject.Inject;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.TestApp;
import org.ligi.gobandroid_hd.etc.AppModule;
import org.ligi.gobandroid_hd.logic.Cell;
import org.ligi.gobandroid_hd.logic.CellImpl;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.model.GameProvider;
import org.ligi.gobandroid_hd.ui.scoring.GameScoringActivity;
import org.ligi.gobandroid_hd.base.BaseIntegration;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.ligi.gobandroid_hd.base.GoViewActions.tapStone;

public class TheGameScoringActivity extends BaseIntegration<GameScoringActivity> {

    @Inject
    GameProvider gameProvider;

    public TheGameScoringActivity() {
        super(GameScoringActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        TestApp.component().inject(this);
    }

    @MediumTest
    public void testThatGoBoardIsThere() {
        final GameScoringActivity activity = getActivity();

        onView(withId(R.id.go_board)).check(matches(isDisplayed()));
        Spoon.screenshot(activity, "count");
    }

    @MediumTest
    public void testThatOneStoneOn9x9are80pt() {
        gameProvider.set(new GoGame((byte) 9));
        gameProvider.get().do_move(new CellImpl(0, 0));

        final GameScoringActivity activity = getActivity();

        onView(withId(R.id.go_board)).perform(tapStone(new CellImpl(0, 0), gameProvider.get()));

        sleep();

        onView(withId(R.id.final_black)).perform(scrollTo());
        onView(withId(R.id.final_black)).check(matches(withText("80.0")));
        onView(withId(R.id.captures_black)).check(matches(withText("0")));
        onView(withId(R.id.result_txt)).check(matches(withText(containsString("Black"))));

        Spoon.screenshot(activity, "one_stone_count");
    }


    @MediumTest
    public void testThatTapToMarkWorks() {
        gameProvider.set(new GoGame((byte) 9));
        gameProvider.get().do_move(new CellImpl(0, 0));
        gameProvider.get().do_move(new CellImpl(0, 1));

        final GameScoringActivity activity = getActivity();

        onView(withId(R.id.go_board)).perform(tapStone(new CellImpl(0, 0), gameProvider.get()));

        sleep();

        onView(withId(R.id.final_black)).perform(scrollTo());
        onView(withId(R.id.final_black)).check(matches(withText("81.0")));
        onView(withId(R.id.captures_black)).check(matches(withText("0 + 1")));
        onView(withId(R.id.result_txt)).check(matches(withText(containsString("Black"))));

        Spoon.screenshot(activity, "count_mark_one");
    }

    public void sleep() {
        SystemClock.sleep(1000);
    }

    @MediumTest
    public void testThatWhiteCanWin() {
        gameProvider.set(new GoGame((byte) 9));
        gameProvider.get().do_move(new CellImpl(0, 0));
        gameProvider.get().do_move(new CellImpl(0, 1));

        final GameScoringActivity activity = getActivity();

        onView(withId(R.id.go_board)).perform(tapStone(new CellImpl(0, 1), gameProvider.get()));

        sleep();

        onView(withId(R.id.final_white)).perform(scrollTo());
        onView(withId(R.id.final_white)).check(matches(withText("87.5")));
        onView(withId(R.id.captures_black)).check(matches(withText("0")));
        onView(withId(R.id.captures_white)).check(matches(withText("0 + 1")));
        onView(withId(R.id.result_txt)).check(matches(withText(containsString("White"))));

        Spoon.screenshot(activity, "count_white_wins");
    }

    @MediumTest
    // https://github.com/ligi/gobandroid/issues/143
    public void testThatScoringWorksWhenTerritoryIsInsideDeadStones() {
        gameProvider.set(new GoGame((byte) 3));
        gameProvider.get().do_move(new CellImpl(0, 1));
        gameProvider.get().do_move(new CellImpl(2, 2));
        gameProvider.get().do_move(new CellImpl(1, 0));

        final GameScoringActivity activity = getActivity();

        onView(withId(R.id.go_board)).perform(tapStone(new CellImpl(2, 2), gameProvider.get()));

        onView(withId(R.id.final_white)).perform(scrollTo());
        onView(withId(R.id.final_white)).check(matches(withText("16.5")));

        Spoon.screenshot(activity, "count_scoring_territory_in_dead_stones");
    }

    @MediumTest
    public void testThatGroupMarkingWorks() {
        gameProvider.set(new GameBuilder(new GoGame(9))
                .move(new CellImpl(0, 0), new CellImpl(0, 1), new CellImpl(1, 0))
                .getGame());

        final GameScoringActivity activity = getActivity();

        onView(withId(R.id.go_board)).perform(tapStone(new CellImpl(0, 1), gameProvider.get()));

        sleep();

        onView(withId(R.id.final_white)).check(matches(withText("88.5")));

        Spoon.screenshot(activity, "count_mark_group");
    }

    private class GameBuilder {

        private final GoGame game;

        private GameBuilder(GoGame game) {
            this.game = game;
        }

        public GameBuilder move(Cell... cells) {
            for (Cell cell : cells) {
                game.do_move(cell);
            }
            return this;
        }

        public GoGame getGame() {
            return game;
        }
    }
}
