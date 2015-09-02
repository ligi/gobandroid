package org.ligi.gobandroid_hd.ui.scoring;

import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.WindowManager;
import java.util.HashSet;
import java.util.Set;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.logic.BoardCell;
import org.ligi.gobandroid_hd.logic.Cell;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.cell_gatherer.LooseConnectedCellGatherer;
import org.ligi.gobandroid_hd.logic.cell_gatherer.MustBeConnectedCellGatherer;
import org.ligi.gobandroid_hd.ui.GoActivity;

/**
 * Activity to score a Game
 */
public class GameScoringActivity extends GoActivity {

    private GameScoringExtrasFragment gameScoringExtrasFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO the next line works but needs investigation - i thought more of
        // getBoard().requestFocus(); - but that was not working ..
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        for (Set<BoardCell> boardCells : getInclusiveGroups()) {
            // TODO find a nicer approach to detect dead stones - this does only cover the common cases
            if (boardCells.size() <= 4) {
                for (BoardCell boardCell : boardCells) {
                    boardCell.board.toggleCellDead(boardCell);
                }
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getGame().initScorer();
        if (gameScoringExtrasFragment != null) {
            gameScoringExtrasFragment.refresh();
        }
    }


    private Set<Set<BoardCell>> getInclusiveGroups() {
        final Set<Cell> allProcessed = new HashSet<>();
        final Set<Set<BoardCell>> allGroups = new HashSet<>();

        for (Cell cell : getGame().getCalcBoard().getAllCells()) {
            final Set<BoardCell> inGroup = new LooseConnectedCellGatherer(getGame().getCalcBoard().getCell(cell));
            allProcessed.addAll(inGroup);
            if (!getGame().getCalcBoard().isCellFree(cell)) {
                allGroups.add(inGroup);
            }

            if (allProcessed.size() == getGame().getSize() * getGame().getSize()) {
                return allGroups;
            }
        }
        return allGroups;
    }

    @Override
    public void doTouch(MotionEvent event) {
        //super.doTouch(event); - Do not call! Not needed and breaks marking dead stones

        eventForZoomBoard(event);
        final Cell touchCell = getBoard().pixel2cell(event.getX(), event.getY());
        App.getInteractionScope().setTouchPosition(touchCell);

        // calculate position on the field by position on the touchscreen

        if (event.getAction() == MotionEvent.ACTION_UP && touchCell != null) {
            doMoveWithUIFeedback(touchCell);
            App.getInteractionScope().setTouchPosition(null);
        }

    }

    @Override
    public byte doMoveWithUIFeedback(Cell cell) {
        do_score_touch(cell);
        getGame().notifyGameChange();
        return GoGame.MOVE_VALID;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }


    @Override
    public void onPause() {
        super.onPause();
        // if we go back to other modes we want to have them alive again ( Zombies ?)
        for (Cell cell : getGame().getCalcBoard().getAllCells()) {
            if (getGame().getCalcBoard().isCellDead(cell)) {
                getGame().getCalcBoard().toggleCellDead(cell);
            }
        }

        getGame().copyVisualBoard();
        getGame().removeScorer();
    }

    @Override
    public GameScoringExtrasFragment getGameExtraFragment() {
        if (gameScoringExtrasFragment == null) {
            gameScoringExtrasFragment = new GameScoringExtrasFragment();
        }
        return gameScoringExtrasFragment;
    }


    public void do_score_touch(Cell cell) {

        if ((!getGame().getCalcBoard().isCellFree(cell)) || getGame().getCalcBoard().isCellDead(cell)) { // if there is a stone/cellGathering
            final MustBeConnectedCellGatherer cellGathering = new MustBeConnectedCellGatherer(getGame().getCalcBoard().getCell(cell));
            for (Cell groupCell : cellGathering) {
                getGame().getCalcBoard().toggleCellDead(groupCell);
            }
        }

        if (getGame().getScorer() != null) {
            getGame().getScorer().calculateScore();
        }
    }
}
