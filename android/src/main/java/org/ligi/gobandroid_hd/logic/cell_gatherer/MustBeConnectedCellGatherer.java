package org.ligi.gobandroid_hd.logic.cell_gatherer;

import org.ligi.gobandroid_hd.logic.StatefulGoBoard;
import org.ligi.gobandroid_hd.logic.StatelessBoardCell;

public class MustBeConnectedCellGatherer extends CellGatherer {


    public MustBeConnectedCellGatherer(StatefulGoBoard board, StatelessBoardCell root) {
        super(board, root);
    }

    @Override
    protected void pushWithCheck(StatelessBoardCell cell) {
        final boolean unProcessed = processed.add(cell);

        if (board.areCellsEqual(cell, root)) {
            add(cell);
            if (unProcessed) {
                pushSurroundingWithCheck(cell);
            }
        }
    }
}
