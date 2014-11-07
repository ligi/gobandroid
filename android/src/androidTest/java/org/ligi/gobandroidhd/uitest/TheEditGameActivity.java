package org.ligi.gobandroidhd.uitest;

import android.test.suitebuilder.annotation.MediumTest;

import com.squareup.spoon.Spoon;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.markers.GoMarker;
import org.ligi.gobandroid_hd.logic.markers.SquareMarker;
import org.ligi.gobandroid_hd.logic.markers.TriangleMarker;
import org.ligi.gobandroid_hd.ui.editing.EditGameActivity;
import org.ligi.gobandroid_hd.ui.scoring.GameScoringActivity;
import org.ligi.gobandroidhd.base.BaseIntegration;

import java.util.List;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withContentDescription;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
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
        App.setGame(new GoGame((byte) 9));

        final EditGameActivity activity = getActivity();

        onView(withContentDescription(getString(R.string.letter))).perform(click());

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {

                onView(withId(R.id.go_board)).perform(tapStone(x, y));
            }
        }


        Spoon.screenshot(activity, "letters");
    }

    private boolean hasMarker(GoMarker leftMarker) {
        final List<GoMarker> markers = App.getGame().getActMove().getMarkers();
        for (GoMarker rightMarker : markers) {
            if (rightMarker.equals(leftMarker )) {
                return true;
            }
        }
        return false;
    }

    @MediumTest
    public void testThatNumbersWork() {
        App.setGame(new GoGame((byte) 9));

        final EditGameActivity activity = getActivity();

        onView(withContentDescription(getString(R.string.number))).perform(click());

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {

                onView(withId(R.id.go_board)).perform(tapStone(x, y));
            }
        }


        for (byte x = 0; x < 9; x++) {
            for (byte y = 0; y < 3; y++) {
                assertTrue(hasMarker(new GoMarker(x, y, "" + (9 * y + x+1))));
            }
        }
        Spoon.screenshot(activity, "numbers");
    }


    @MediumTest
    public void testThatSquareWorks() {
        App.setGame(new GoGame((byte) 9));

        final EditGameActivity activity = getActivity();

        onView(withContentDescription(getString(R.string.square))).perform(click());

        onView(withId(R.id.go_board)).perform(tapStone(1, 2));

        assertTrue(hasMarker(new SquareMarker((byte)1,(byte)2)));

        Spoon.screenshot(activity, "square");
    }


    @MediumTest
    public void testThatTriangleWorks() {
        App.setGame(new GoGame((byte) 9));

        final EditGameActivity activity = getActivity();

        onView(withContentDescription(getString(R.string.triangle))).perform(click());

        onView(withId(R.id.go_board)).perform(tapStone(1, 2));

        assertTrue(hasMarker(new TriangleMarker((byte)1,(byte)2)));

        Spoon.screenshot(activity, "triangle");
    }
}
