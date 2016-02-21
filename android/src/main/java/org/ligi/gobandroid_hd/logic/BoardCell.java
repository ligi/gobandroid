package org.ligi.gobandroid_hd.logic;

import java.util.ArrayList;
import java.util.List;

public class BoardCell extends Cell {

    public final StatefulGoBoard board;
    private List<BoardCell> neighbours = new ArrayList<>();
    public BoardCell left, right, up, down;


    public BoardCell(int x, int y, StatefulGoBoard board) {
        super(x, y);
        this.board = board;
    }

    public boolean is(byte kind) {
        return board.isCellKind(this, kind);
    }

    public boolean isFree() {
        return board.isCellFree(this);
    }

    public boolean isInHomogeneousGroupWith(Cell cell) {
        return board.areCellsEqual(this, cell);
    }

    public boolean isInAreaGroupWith(Cell cell) {
        return board.areCellsTogetherInArea(this, cell);
    }

    public List<BoardCell> getNeighbors() {
        return neighbours;
    }
}
