package org.ligi.gobandroid_hd.logic;

import java.util.ArrayList;
import java.util.List;

public class BoardCell extends Cell {

    public final GoBoard board;
    private List<BoardCell> neighbours = new ArrayList<>();
    public BoardCell left, right, up, down;


    public BoardCell(int x, int y, GoBoard board) {
        super(x, y);
        this.board = board;
    }

    public void assignNeighbours() {
        if (x > 0) {
            left = board.getCell(x - 1, y);
            neighbours.add(left);
        }

        if (y > 0) {
            up = board.getCell(x , y-1);
            neighbours.add(up);
        }

        if (y < board.getSize() - 1) {
            down = board.getCell(x,y+1);
            neighbours.add(down);
        }


        if (x < board.getSize() - 1) {
            right = board.getCell(x+1,y);
            neighbours.add(right);
        }
    }


    public boolean isInGroupWith(Cell cell) {
        return board.areCellsEqual(this, cell);
    }

    public boolean is(byte kind) {
        return board.isCellKind(this, kind);
    }

    public List<BoardCell> getNeighbors() {
        return neighbours;
    }
}
