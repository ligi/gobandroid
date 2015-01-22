package org.ligi.gobandroid_hd.logic;

public class BoardCell extends Cell {

    public final GoBoard board;

    public BoardCell(int x, int y, GoBoard board) {
        super(x, y);
        this.board = board;
    }

    public BoardCell(Cell cell, GoBoard board) {
        super(cell.x, cell.y);
        this.board = board;
    }

    public boolean hasLeft() {
        return x > 0;
    }

    public boolean hasRight() {
        return x < board.getSize() - 1;
    }

    public boolean hasUp() {
        return y > 0;
    }

    public boolean hasDown() {
        return y < board.getSize() - 1;
    }

    public boolean isInGroupWith(Cell cell) {
        return board.areCellsEqual(x, y, cell.x, cell.y);
    }

}
