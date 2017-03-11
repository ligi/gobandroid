package org.ligi.gobandroid_hd.logic;

public interface GoBoard {
    int getSize();
    StatelessBoardCell getCell(int x, int y);
    StatelessBoardCell getCell(Cell cell);
    boolean isCellOnBoard(Cell cell);
}
