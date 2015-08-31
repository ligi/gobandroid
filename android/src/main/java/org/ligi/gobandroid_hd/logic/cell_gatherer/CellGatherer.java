package org.ligi.gobandroid_hd.logic.cell_gatherer;

import java.util.HashSet;
import java.util.Set;
import org.ligi.gobandroid_hd.logic.BoardCell;

public abstract class CellGatherer extends HashSet<BoardCell> {

    protected final Set<BoardCell> processed;
    protected final BoardCell root;

    public CellGatherer(BoardCell root) {
        this.root = root;
        processed = new HashSet<>();
        pushWithCheck(root);
    }


    abstract protected void pushWithCheck(BoardCell cell);

    protected void pushSurroundingWithCheck(BoardCell cell) {
        for (BoardCell boardCell : cell.getNeighbors()) {
            pushWithCheck(boardCell);
        }
    }

    public Set<BoardCell> getProcessed() {
        return processed;
    }

}
