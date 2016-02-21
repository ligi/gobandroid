package org.ligi.gobandroid_hd.logic.cell_gatherer;

import org.ligi.gobandroid_hd.logic.StatefulGoBoard;
import org.ligi.gobandroid_hd.logic.StatelessBoardCell;

public class AreaCellGatherer extends CellGatherer {

    public AreaCellGatherer(StatefulGoBoard board, StatelessBoardCell root) {
        super(board, root);
    }

    @Override
    protected void pushWithCheck(StatelessBoardCell cell) {
        final boolean unProcessed = processed.add(cell);

        if (board.areCellsTogetherInArea(root, cell)) {
            add(cell);
            if (unProcessed) {
                pushSurroundingWithCheck(cell);
            }
        }

        if (board.isCellFree(cell) && unProcessed) {
            pushSurroundingWithCheck(cell);
        }
    }


}
