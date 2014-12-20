package org.ligi.gobandroidhd.base;

import android.view.View;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;

import org.ligi.gobandroid_hd.App;

public class GoViewActions {

    public static ViewAction placeStone(final int x, final int y) {
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);
                        final int gameSize = App.getGame().getSize();

                        final float screenX = screenPos[0] + (0.5f + x) * (view.getWidth() / gameSize);
                        final float screenY = screenPos[1] + (0.5f + y) * (view.getHeight() / gameSize);

                        return new float[]{screenX, screenY};
                    }
                },
                Press.FINGER);
    }

    public static ViewAction tapStone(final int x, final int y) {
        return placeStone(x, y);
    }
}
