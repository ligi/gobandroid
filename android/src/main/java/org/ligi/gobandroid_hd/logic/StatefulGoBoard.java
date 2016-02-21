/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 **/

package org.ligi.gobandroid_hd.logic;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import org.ligi.gobandroid_hd.logic.GoDefinitions.CellStatus;

import java.util.ArrayList;
import java.util.List;

import static org.ligi.gobandroid_hd.logic.GoDefinitions.getStringFromCellStatus;

/**
 * Class to represent a Go Board
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         <p/>
 *         This software is licenced with GPLv3
 */

public class StatefulGoBoard {

    private final StatelessGoBoard statelessGoBoard;
    private final int size;

    @CellStatus
    public final byte[][] board;

    private SparseArray<BoardCell> cells = new SparseArray<>();
    private List<BoardCell> allCells = new ArrayList<>(); // as we cannot iterate over

    public StatefulGoBoard(final StatelessGoBoard statelessGoBoard) {
        this.statelessGoBoard = statelessGoBoard;
        this.size = statelessGoBoard.getSize();

        board = new byte[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                final BoardCell boardCell = new BoardCell(x, y, this);
                allCells.add(boardCell);
                cells.put(getKey(x, y), boardCell);
            }

        }

        for (BoardCell allCell : allCells) {
            allCell.assignNeighbours();
        }

    }

    public BoardCell getCell(Cell cell) {
        return getCell(cell.x, cell.y);
    }

    public int getKey(final int x, final int y) {
        return y * size + x;
    }

    public BoardCell getCell(final int x, final int y) {
        return cells.get(getKey(x, y));
    }

    /**
     * @param cell the cell to test
     * @return if the cell is on board
     */
    public boolean isCellOnBoard(Cell cell) {
        return cell.x < size && cell.y < size && cell.x >= 0 && cell.y >= 0;
    }


    public StatefulGoBoard(StatelessGoBoard statelessGoBoard, byte[][] predefined_board) {
        this(statelessGoBoard);

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
    public StatefulGoBoard clone() {
        return new StatefulGoBoard(statelessGoBoard, board);
    }

    /**
     * check if two boards are equal
     */
    public boolean equals(StatefulGoBoard other) {

        // cannot be the same if board is null
        if (other == null) return false;

        // if the size is not matching the boards can't be equal
        if (size != other.size) return false;

        // check if all stones are placed equally
        for (Cell cell : getAllCells()) {
            if (board[cell.x][cell.y] != other.board[cell.x][cell.y]) return false;
        }

        return true;
    }

    /**
     * print a visual representation of the board via Log.d
     */
    public String toString() {
        return toString(false);
    }

    public String toString(final boolean unicode) {
        final StringBuilder b = new StringBuilder(size * size + size);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                @CellStatus final byte cellStatus = board[x][y];
                b.append(getStringFromCellStatus(cellStatus, unicode));
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

    public boolean isCellFree(@NonNull Cell cell) {
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

    public boolean areCellsTogetherInArea(Cell one, Cell other) {
        return Math.max(board[one.x][one.y], 0) == Math.max(board[other.x][other.y], 0);
    }

    public void setCell(Cell cell, @CellStatus byte newStatus) {
        board[cell.x][cell.y] = newStatus;
    }

    public void toggleCellDead(Cell cell) {
        board[cell.x][cell.y] *= -1;
    }

    public boolean isCellDead(Cell cell) {
        return (board[cell.x][cell.y] < 0);
    }

}