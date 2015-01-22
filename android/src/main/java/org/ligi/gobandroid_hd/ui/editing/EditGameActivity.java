package org.ligi.gobandroid_hd.ui.editing;

import android.os.Bundle;
import android.view.Menu;
import android.view.WindowManager;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.Cell;
import org.ligi.gobandroid_hd.logic.GoDefinitions;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.logic.markers.CircleMarker;
import org.ligi.gobandroid_hd.logic.markers.GoMarker;
import org.ligi.gobandroid_hd.logic.markers.SquareMarker;
import org.ligi.gobandroid_hd.logic.markers.TextMarker;
import org.ligi.gobandroid_hd.logic.markers.TriangleMarker;
import org.ligi.gobandroid_hd.logic.markers.util.MarkerUtil;
import org.ligi.gobandroid_hd.ui.GoActivity;

import java.util.List;

import static org.ligi.gobandroid_hd.logic.GoDefinitions.STONE_BLACK;
import static org.ligi.gobandroid_hd.logic.GoDefinitions.STONE_WHITE;

/**
 * Activity to edit a Game
 */
public class EditGameActivity extends GoActivity implements GoGameChangeListener {

    private StatefulEditModeItems statefulEditModeItems = new StatefulEditModeItems();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO the next line works but needs investigation - i thought more of
        // getBoard().requestFocus(); - but that was not working ..
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public boolean doAutoSave() {
        return true;
    }

    @Override
    public byte doMoveWithUIFeedback(Cell cell) {
        switch (getMode()) {
            case BLACK:
                if (getGame().getHandicapBoard().isCellKind(cell, STONE_BLACK)) {
                    getGame().getHandicapBoard().setCell(cell, GoDefinitions.STONE_NONE);
                } else {
                    getGame().getHandicapBoard().setCell(cell, STONE_BLACK);
                }
                getGame().jump(getGame().getActMove()); // we need to totally refresh the board
                return GoGame.MOVE_VALID;

            case WHITE:
                if (getGame().getHandicapBoard().isCellKind(cell, STONE_WHITE)) {
                    getGame().getHandicapBoard().setCell(cell, GoDefinitions.STONE_NONE);
                } else {
                    getGame().getHandicapBoard().setCell(cell,STONE_WHITE);
                }
                getGame().jump(getGame().getActMove()); // we need to totally refresh the board
                return GoGame.MOVE_VALID;

            case TRIANGLE:
            case SQUARE:
            case CIRCLE:
            case NUMBER:
            case LETTER:
                final List<GoMarker> markers = getGame().getActMove().getMarkers();

                // remove markers with same coordinates
                for (GoMarker marker : markers) {
                    if (marker.isInCell(cell)) {
                        markers.remove(marker);
                        return GoGame.MOVE_VALID;
                    }
                }

                markers.add(removeOldAndGetNewMarker(cell, markers));

        }
        getGame().notifyGameChange();
        return GoGame.MOVE_VALID;
    }

    private GoMarker removeOldAndGetNewMarker(Cell cell, List<GoMarker> markers) {
        switch (getMode()) {
            case TRIANGLE:
                return new TriangleMarker(cell);
            case SQUARE:
                return new SquareMarker(cell);
            case CIRCLE:
                return new  CircleMarker(cell);
            case NUMBER:
                final int firstFreeNumber = MarkerUtil.findFirstFreeNumber(markers);
                return new TextMarker(cell, String.valueOf(firstFreeNumber));
            case LETTER:
                final String nextLetter = MarkerUtil.findNextLetter(markers);
                return new TextMarker(cell, nextLetter);
        }
        throw new IllegalArgumentException("unknown mode " + getMode());
    }

    private EditGameMode getMode() {
        return statefulEditModeItems.getActMode();
    }

    @Override
    public EditGameExtrasFragment getGameExtraFragment() {
        return new EditGameExtrasFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ingame_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public StatefulEditModeItems getStatefulEditModeItems() {
        return statefulEditModeItems;
    }
}
