package org.ligi.gobandroid_hd.logic;

public class Cell {
    public final int x;
    public final int y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Cell left() {
        return new Cell(x - 1, y);
    }

    public Cell right() {
        return new Cell(x + 1, y);
    }

    public Cell up() {
        return new Cell(x, y - 1);
    }

    public Cell down() {
        return new Cell(x, y + 1);
    }
}
