package org.ligi.gobandroid_hd.logic;

import java.util.ArrayList;
import java.util.List;

public class StatelessBoardCell extends Cell {

    public final StatelessGoBoard board;

    private List<StatelessBoardCell> neighbours = new ArrayList<>();
    public StatelessBoardCell left, right, up, down;


    public StatelessBoardCell(int x, int y,StatelessGoBoard board) {
        super(x, y);
        this.board = board;
    }

    public void assignNeighbours() {
        if (x > 0) {
            left = board.getCell(x - 1, y);
            neighbours.add(left);
        }

        if (y > 0) {
            up = board.getCell(x, y - 1);
            neighbours.add(up);
        }

        if (y < board.getSize() - 1) {
            down = board.getCell(x, y + 1);
            neighbours.add(down);
        }


        if (x < board.getSize() - 1) {
            right = board.getCell(x + 1, y);
            neighbours.add(right);
        }
    }



    public List<StatelessBoardCell> getNeighbors() {
        return neighbours;
    }
}
