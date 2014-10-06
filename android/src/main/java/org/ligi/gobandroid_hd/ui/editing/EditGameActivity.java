package org.ligi.gobandroid_hd.ui.editing;

import android.os.Bundle;
import android.view.Menu;
import android.view.WindowManager;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.logic.markers.CircleMarker;
import org.ligi.gobandroid_hd.logic.markers.GoMarker;
import org.ligi.gobandroid_hd.logic.markers.SquareMarker;
import org.ligi.gobandroid_hd.logic.markers.TriangleMarker;
import org.ligi.gobandroid_hd.logic.markers.util.MarkerUtil;
import org.ligi.gobandroid_hd.ui.GoActivity;

import java.util.List;

/**
 * Activity to record a Game - or play on one device
 *
 * @author ligi
 */
public class EditGameActivity extends GoActivity implements GoGameChangeListener {

    private EditModeItemPool editModePool = new EditModeItemPool();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO the next line works but needs investigation - i thought more of
        // getBoard().requestFocus(); - but that was not working ..
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public boolean doAutosave() {
        return true;
    }

    @Override
    public byte doMoveWithUIFeedback(byte x, byte y) {

        // if it is a marker we have to check if there is one already and
        // remove
        final List<GoMarker> markers = getGame().getActMove().getMarkers();
        switch (getMode()) {
            case TRIANGLE:
            case SQUARE:
            case CIRCLE:
            case NUMBER:
            case LETTER:
                // remove markers with same coordinates
                GoMarker marker2remove = null;
                for (GoMarker marker : markers) {
                    if (marker.getX() == x && marker.getY() == y) {
                        marker2remove = marker;
                    }
                }

                if (marker2remove != null) {
                    markers.remove(marker2remove);
                    return GoGame.MOVE_VALID;
                }
        }

        switch (getMode()) {
            case BLACK:
                if (getGame().getHandicapBoard().isCellBlack(x, y)) {
                    getGame().getHandicapBoard().setCellFree(x, y);
                } else {
                    getGame().getHandicapBoard().setCellBlack(x, y);
                }
                getGame().jump(getGame().getActMove()); // we need to totally
                // refresh the board
                break;
            case WHITE:
                if (getGame().getHandicapBoard().isCellWhite(x, y)) {
                    getGame().getHandicapBoard().setCellFree(x, y);
                } else {
                    getGame().getHandicapBoard().setCellWhite(x, y);
                }
                getGame().jump(getGame().getActMove()); // we need to totally
                // refresh the board
                break;
            case TRIANGLE:
                markers.add(new TriangleMarker(x, y));
                break;
            case SQUARE:
                markers.add(new SquareMarker(x, y));
                break;
            case CIRCLE:
                markers.add(new CircleMarker(x, y));
                break;

            case NUMBER:
                final int firstFreeNumber = MarkerUtil.findFirstFreeNumber(markers);
                markers.add(new GoMarker(x, y, "" + firstFreeNumber));
                break;

            case LETTER:
                final String nextLetter = MarkerUtil.findNextLetter(markers);
                markers.add(new GoMarker(x, y, nextLetter));
                break;

        }
        getGame().notifyGameChange();
        return GoGame.MOVE_VALID;
    }

    private EditGameMode getMode() {
        return editModePool.getActMode();
    }

    /**
     * crashes with share and imho not needed any more - but investigate
     *
     * @Override public void onGoGameChange() {
     * super.onGoGameChange();
     * this.invalidateOptionsMenu();
     * }
     */

    @Override
    public EditGameExtrasFragment getGameExtraFragment() {
        return new EditGameExtrasFragment(editModePool);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.ingame_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
