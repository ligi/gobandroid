package org.ligi.gobandroid_hd.logic;

public class Cell {
    public final int x;
    public final int y;

    public Cell(Cell cell) {
        this(cell.x, cell.y);
    }

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof Cell)) {
            return false;
        }

        Cell other = (Cell) o;

        return ((other.x == x) && (other.y == y));

    }

    @Override
    public String toString() {
        return "x:" + x + " y:" + y;
    }

    @Override
    public int hashCode() {
        return x << 8 | y;
    }
}
