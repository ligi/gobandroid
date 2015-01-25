package org.ligi.gobandroid_hd.logic.cell_gatherer;

import org.ligi.gobandroid_hd.logic.BoardCell;

public class MustBeConnectedCellGatherer extends CellGatherer {

    public MustBeConnectedCellGatherer(BoardCell root) {
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
    }
}
