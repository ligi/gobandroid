package org.ligi.gobandroid_hd.ui.scoring;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

import org.ligi.axt.AXT;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.Cell;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGameMetadata;
import org.ligi.gobandroid_hd.logic.StatefulGoBoard;
import org.ligi.gobandroid_hd.logic.StatelessBoardCell;
import org.ligi.gobandroid_hd.logic.cell_gatherer.LooseConnectedCellGatherer;
import org.ligi.gobandroid_hd.logic.cell_gatherer.MustBeConnectedCellGatherer;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.gnugo.PlayAgainstGnuGoActivity;
import org.ligi.gobandroid_hd.ui.recording.GameRecordActivity;

import java.util.HashSet;
import java.util.Set;

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

        for (Set<StatelessBoardCell> boardCells : getInclusiveGroups()) {
            // TODO find a nicer approach to detect dead stones - this does only cover the common cases
            if (boardCells.size() <= 4) {
                for (StatelessBoardCell boardCell : boardCells) {
                    getGame().getCalcBoard().toggleCellDead(boardCell);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.ingame_score, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private Set<Set<StatelessBoardCell>> getInclusiveGroups() {
        final Set<Cell> allProcessed = new HashSet<>();
        final Set<Set<StatelessBoardCell>> allGroups = new HashSet<>();

        for (Cell cell : getGame().getCalcBoard().getStatelessGoBoard().getAllCells()) {
            final Set<StatelessBoardCell> inGroup = new LooseConnectedCellGatherer(getGame().getCalcBoard(), getGame().getCalcBoard().getStatelessGoBoard().getCell(cell));
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
        interactionScope.setTouchPosition(touchCell);

        // calculate position on the field by position on the touchscreen

        if (event.getAction() == MotionEvent.ACTION_UP && touchCell != null) {
            doMoveWithUIFeedback(touchCell);
            interactionScope.setTouchPosition(null);
        }

    }

    @Override
    public byte doMoveWithUIFeedback(Cell cell) {
        do_score_touch(cell);
        getGame().notifyGameChange();
        return GoGame.MOVE_VALID;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_game_again:
                final GoGameMetadata metaData = getGame().getMetaData();
                gameProvider.set(new GoGame(getGame().getSize()));
                if (metaData.getBlackName().toLowerCase().equals("gnugo") || metaData.getWhiteName().toLowerCase().equals("gnugo")) {
                    AXT.at(this).startCommonIntent().activityFromClass(PlayAgainstGnuGoActivity.class);
                } else {

                    AXT.at(this).startCommonIntent().activityFromClass(GameRecordActivity.class);
                }

                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        // if we go back to other modes we want to have them alive again ( Zombies ?)
        for (Cell cell : getGame().getCalcBoard().getStatelessGoBoard().getAllCells()) {
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

        final StatefulGoBoard calcBoard = getGame().getCalcBoard();
        if ((!calcBoard.isCellFree(cell)) || calcBoard.isCellDead(cell)) { // if there is a stone/cellGathering
            final MustBeConnectedCellGatherer cellGathering = new MustBeConnectedCellGatherer(calcBoard, calcBoard.getStatelessGoBoard().getCell(cell));
            for (Cell groupCell : cellGathering) {
                calcBoard.toggleCellDead(groupCell);
            }
        }

        if (getGame().getScorer() != null) {
            getGame().getScorer().calculateScore();
        }
    }
}
