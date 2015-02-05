/**
 * gobandroid 
 * by Marcus -Ligi- Bueschleb 
 * http://ligi.de
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as 
 * published by the Free Software Foundation; 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. 
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 **/

package org.ligi.gobandroid_hd.logic;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent a Go Board
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         <p/>
 *         This software is licenced with GPLv3
 */

public class GoBoard {

    private final int size;
    public final byte[][] board;
    private SparseArray<BoardCell> cells = new SparseArray<>();
    private List<BoardCell> allCells = new ArrayList<>();

    public GoBoard(int size) {
        this.size = size;
        board = new byte[size][size];
        for (int x=0 ; x<size;x++)
            for (int y=0 ; y<size;y++) {
                final int key = y * size + x;
                final BoardCell boardCell = new BoardCell(x, y, this);
                allCells.add(boardCell);
                cells.put(key,boardCell);
            }

        for (BoardCell allCell : allCells)
            allCell.assignNeighbours();
    }

    public BoardCell getCell(Cell cell) {
        return getCell(cell.x,cell.y);
    }

    public BoardCell getCell(final int x, final int y) {
        final int key = y * size + x;
        return cells.get(key);
    }

    public boolean isCellOnBoard(Cell cell) {
        return getCell(cell)!=null;
    }



    public GoBoard(int size, byte[][] predefined_board) {
        this(size);

        // copy the board
        for (int x = 0; x < size; x++) {
            System.arraycopy(predefined_board[x], 0, board[x], 0, size);
        }
    }

    public List<BoardCell> getAllCells() {
        return allCells;
    }

    /**
     * clone this board
     *
     * @return a copy of this board
     */
    public GoBoard clone() {
        return new GoBoard(size, board);
    }

    /**
     * check if two boards are equal
     */
    public boolean equals(GoBoard other) {

        // cannot be the same if board is null
        if (other == null)
            return false;

        // if the size is not matching the boards can't be equal
        if (size != other.size)
            return false;

        // check if all stones are placed equally
        for (Cell cell : getAllCells())
            if (board[cell.x][cell.y] != other.board[cell.x][cell.y])
                return false;

        return true;
    }

    /**
     * print a visual representation of the board via Log.d
     */
    public String toString() {
        final StringBuilder b = new StringBuilder(size * size + size);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (board[x][y] == GoDefinitions.STONE_NONE)
                    b.append('.');
                else if (board[x][y] == GoDefinitions.STONE_BLACK)
                    b.append('B');
                else if (board[x][y] == GoDefinitions.STONE_WHITE)
                    b.append('W');
                else if (board[x][y] == -GoDefinitions.STONE_BLACK)
                    b.append('b');
                else if (board[x][y] == -GoDefinitions.STONE_WHITE)
                    b.append('w');
            }
            b.append('\n');
        }
        return b.toString();
    }

    /**
     * @return the board size
     */
    public int getSize() {
        return size;
    }

    public boolean isCellFree(Cell cell) {
        return board[cell.x][cell.y] <= 0;
    }

    public boolean isCellKind(Cell cell, byte kind) {
        return board[cell.x][cell.y] == kind;
    }

    public boolean isCellBlack(Cell cell) {
        return board[cell.x][cell.y] == GoDefinitions.STONE_BLACK;
    }

    public boolean isCellWhite(Cell cell) {
        return board[cell.x][cell.y] == GoDefinitions.STONE_WHITE;
    }

    public boolean isCellDeadBlack(Cell cell) {
        return -board[cell.x][cell.y] == GoDefinitions.STONE_BLACK;
    }

    public boolean isCellDeadWhite(Cell cell) {
        return -board[cell.x][cell.y] == GoDefinitions.STONE_WHITE;
    }

    public boolean areCellsEqual(Cell one, Cell other) {
        return board[one.x][one.y] == board[other.x][other.y];
    }

    public void setCell(Cell cell, byte newStatus) {
        board[cell.x][cell.y] = newStatus;
    }

    public void toggleCellDead(Cell cell) {
        board[cell.x][cell.y] *= -1;
    }

    public boolean isCellDead(Cell cell) {
        return (board[cell.x][cell.y] < 0);
    }

}