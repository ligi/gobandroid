package org.ligi.gobandroidhd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.Cell;
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
        App.getGame().do_move(new Cell(0, 0));

        final GameScoringActivity activity = getActivity();

        onView(withId(R.id.go_board)).perform(tapStone(new Cell(0, 0)));

        onView(withId(R.id.final_black)).check(matches(withText(containsString("80.0"))));
        onView(withId(R.id.captures_black)).check(matches(withText(containsString("0"))));
        onView(withId(R.id.result_txt)).check(matches(withText(containsString("Black"))));

        Spoon.screenshot(activity, "one_stone_count");
    }


    @MediumTest
    public void testThatTapToMarkWorks() {
        App.setGame(new GoGame((byte) 9));
        App.getGame().do_move(new Cell(0, 0));
        App.getGame().do_move(new Cell(0, 1));

        final GameScoringActivity activity = getActivity();

        onView(withId(R.id.go_board)).perform(tapStone(new Cell(0, 0)));

        onView(withId(R.id.final_black)).check(matches(withText(containsString("81.0"))));
        onView(withId(R.id.captures_black)).check(matches(withText(containsString("1"))));
        onView(withId(R.id.result_txt)).check(matches(withText(containsString("Black"))));

        Spoon.screenshot(activity, "count_mark_one");
    }

    @MediumTest
    public void testThatWhiteCanWin() {
        App.setGame(new GoGame((byte) 9));
        App.getGame().do_move(new Cell(0, 0));
        App.getGame().do_move(new Cell(0, 1));

        final GameScoringActivity activity = getActivity();

        onView(withId(R.id.go_board)).perform(tapStone(new Cell(0, 1)));

        onView(withId(R.id.final_white)).check(matches(withText(containsString("87.5"))));
        onView(withId(R.id.captures_black)).check(matches(withText(containsString("0"))));
        onView(withId(R.id.captures_white)).check(matches(withText(containsString("1"))));
        onView(withId(R.id.result_txt)).check(matches(withText(containsString("White"))));

        Spoon.screenshot(activity, "count_white_wins");
    }


    @MediumTest
    public void testThatGroupMarkingWorks() {
        App.setGame(new GameBuilder(new GoGame(9))
                .move(new Cell(0, 0), new Cell(0, 1), new Cell(1, 0))
                .getGame());

        final GameScoringActivity activity = getActivity();

        onView(withId(R.id.go_board)).perform(tapStone(new Cell(0, 1)));

        onView(withId(R.id.final_white)).check(matches(withText(containsString("88.5"))));

        Spoon.screenshot(activity, "count_mark_group");
    }

    private class GameBuilder {

        private final GoGame game;

        private GameBuilder(GoGame game) {
            this.game = game;
        }

        public GameBuilder move(Cell cell) {
            game.do_move(cell);
            return this;
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
