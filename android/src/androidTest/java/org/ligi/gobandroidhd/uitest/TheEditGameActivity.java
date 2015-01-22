package org.ligi.gobandroidhd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.Cell;
import org.ligi.gobandroid_hd.logic.CellFactory;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.markers.SquareMarker;
import org.ligi.gobandroid_hd.logic.markers.TextMarker;
import org.ligi.gobandroid_hd.logic.markers.TriangleMarker;
import org.ligi.gobandroid_hd.ui.editing.EditGameActivity;
import org.ligi.gobandroidhd.base.BaseIntegration;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.ligi.gobandroidhd.base.GoViewActions.tapStone;

public class TheEditGameActivity extends BaseIntegration<EditGameActivity> {

    public TheEditGameActivity() {
        super(EditGameActivity.class);
    }

    @MediumTest
    public void testThatGoBoardIsThere() {
        final EditGameActivity activity = getActivity();

        onView(withId(R.id.go_board)).check(matches(isDisplayed()));
        Spoon.screenshot(activity, "edit");
    }

    @MediumTest
    public void testThatLettersWork() {
        App.setGame(new GoGame(9));

        final EditGameActivity activity = getActivity();

        onView(withContentDescription(getString(R.string.letter))).perform(click());

        tap9x3Field();

        Spoon.screenshot(activity, "letters");
    }

    private void tap9x3Field() {
        for (Cell cell : CellFactory.getAllCellsForRect(9, 3)) {
            onView(withId(R.id.go_board)).perform(tapStone(cell));
        }
    }

    @MediumTest
    public void testThatNumbersWork() {
        App.setGame(new GoGame(9));

        final EditGameActivity activity = getActivity();

        onView(withContentDescription(getString(R.string.number))).perform(click());

        tap9x3Field();

        for (Cell cell : CellFactory.getAllCellsForRect(9, 3)) {
            assertThat(App.getGame().getActMove().getMarkers()).contains(new TextMarker(cell, "" + (9 * cell.y + cell.x + 1)));
        }

        Spoon.screenshot(activity, "numbers");
    }


    @MediumTest
    public void testThatSquareWorks() {
        App.setGame(new GoGame(9));

        final EditGameActivity activity = getActivity();

        onView(withContentDescription(getString(R.string.square))).perform(click());

        final Cell cell = new Cell(1, 2);
        onView(withId(R.id.go_board)).perform(tapStone(cell));

        assertThat(App.getGame().getActMove().getMarkers()).contains(new SquareMarker(cell));

        Spoon.screenshot(activity, "square");
    }


    @MediumTest
    public void testThatTriangleWorks() {
        App.setGame(new GoGame(9));

        final EditGameActivity activity = getActivity();

        onView(withContentDescription(getString(R.string.triangle))).perform(click());

        onView(withId(R.id.go_board)).perform(tapStone(new Cell(1, 2)));

        assertThat(App.getGame().getActMove().getMarkers()).contains(new TriangleMarker(new Cell(1, 2)));

        Spoon.screenshot(activity, "triangle");
    }
}
