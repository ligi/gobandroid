package org.ligi.gobandroid_hd.logic.cell_gatherer;

import org.ligi.gobandroid_hd.logic.BoardCell;

public class LooseConnectedCellGatherer extends CellGatherer {

    public LooseConnectedCellGatherer(BoardCell root) {
        super(root);
    }

    @Override
    protected void pushWithCheck(BoardCell cell) {
        final boolean unProcessed = processed.add(cell);

        if (cell.isInGroupWith(root)) {
            add(cell);
            if (unProcessed) {
                pushSurroundingWithCheck(cell);
            }
        }

        if (cell.board.isCellFree(cell) && unProcessed) {
            pushSurroundingWithCheck(cell);
        }
    }

}
