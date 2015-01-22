package org.ligi.gobandroid_hd.logic;

import java.util.Stack;

public class FloodFillStackStack extends Stack<BoardCell> {

    private final boolean checked_pos[][];
    private final BoardCell root;

    public FloodFillStackStack(BoardCell root) {
        this.root = root;
        checked_pos = new boolean[root.board.getSize()][root.board.getSize()];
        push(root);
    }

    public void pushWithCheck(BoardCell cell, BoardCell reference) {
        if (cell.isInGroupWith(reference) && !checked_pos[cell.x][cell.y]) {

            push(cell);
        }
    }

    @Override
    public BoardCell push(BoardCell cell) {
        checked_pos[cell.x][cell.y] = true;
        return super.push(cell);
    }

    public void pushSurroundingWithCheck(BoardCell cell) {
        if (cell.hasLeft()) {
            pushWithCheck(new BoardCell(cell.left(), cell.board), cell);
        }
        if (cell.hasRight()) {
            pushWithCheck(new BoardCell(cell.right(), cell.board), cell);
        }
        if (cell.hasUp()) {
            pushWithCheck(new BoardCell(cell.up(), cell.board), cell);
        }
        if (cell.hasDown()) {
            pushWithCheck(new BoardCell(cell.down(), cell.board), cell);
        }

    }
}
