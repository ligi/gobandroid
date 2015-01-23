package org.ligi.gobandroid_hd.logic;

public class BoardCell extends Cell {

    public final GoBoard board;

    public BoardCell(int x, int y, GoBoard board) {
        super(x, y);
        this.board = board;
    }

    public BoardCell(Cell cell, GoBoard board) {
        super(cell);
        this.board = board;
    }

    public BoardCell left() {
        return new BoardCell(new Cell(x - 1, y), board);
    }

    public BoardCell right() {
        return new BoardCell(new Cell(x + 1, y), board);
    }

    public BoardCell up() {
        return new BoardCell(new Cell(x, y - 1), board);
    }

    public BoardCell down() {
        return new BoardCell(new Cell(x, y + 1), board);
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
        return board.areCellsEqual(this, cell);
    }

    public boolean isOnBoard() {
        return x < board.getSize() && y < board.getSize() && x >= 0 && y >= 0;
    }

    public boolean is(byte kind) {
        return board.isCellKind(this, kind);
    }

}
