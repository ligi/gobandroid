package org.ligi.gobandroid_hd.logic.cell_gatherer;

import org.ligi.gobandroid_hd.logic.StatefulGoBoard;
import org.ligi.gobandroid_hd.logic.StatelessBoardCell;

import java.util.HashSet;
import java.util.Set;

public abstract class CellGatherer extends HashSet<StatelessBoardCell> {

    protected final Set<StatelessBoardCell> processed;
    protected final StatelessBoardCell root;
    protected final StatefulGoBoard board;

    public CellGatherer(final StatefulGoBoard board, final StatelessBoardCell root) {
        this.board = board;
        this.root = root;
        processed = new HashSet<>();
        pushWithCheck(root);
    }


    abstract protected void pushWithCheck(StatelessBoardCell cell);

    protected void pushSurroundingWithCheck(StatelessBoardCell cell) {
        for (StatelessBoardCell boardCell : cell.getNeighbors()) {
            pushWithCheck(boardCell);
        }
    }

    public Set<StatelessBoardCell> getProcessed() {
        return processed;
    }

}
