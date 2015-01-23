package org.ligi.gobandroidhd.base;

import android.support.test.espresso.UiController;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.widget.SeekBar;

import org.hamcrest.Matcher;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.logic.Cell;

public class GoViewActions {

    public static ViewAction placeStone(final Cell cell) {
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);
                        final int gameSize = App.getGame().getSize();

                        final float screenX = screenPos[0] + (0.5f + cell.x) * (view.getWidth() / gameSize);
                        final float screenY = screenPos[1] + (0.5f + cell.y) * (view.getHeight() / gameSize);

                        return new float[]{screenX, screenY};
                    }
                },
                Press.FINGER);
    }

    public static ViewAction tapStone(Cell cell) {
        return placeStone(cell);
    }

    public static ViewAction setProgress(final int progress) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                seekBar.setProgress(progress);
            }
            @Override
            public String getDescription() {
                return "Set a progress on a SeekBar";
            }
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }
        };
    }
}
