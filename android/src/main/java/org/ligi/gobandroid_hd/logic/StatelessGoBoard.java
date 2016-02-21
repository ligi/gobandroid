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

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent a Go Board
 
 */

public class StatelessGoBoard {

    private final int size;

    private SparseArray<StatelessBoardCell> cells = new SparseArray<>();
    private List<StatelessBoardCell> allCells = new ArrayList<>(); // as we cannot iterate over

    public StatelessGoBoard(int size) {
        this.size = size;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                final StatelessBoardCell boardCell = new StatelessBoardCell(x, y, this);
                allCells.add(boardCell);
                cells.put(getKey(x, y), boardCell);
            }

        }

        for (StatelessBoardCell allCell : allCells) {
            allCell.assignNeighbours();
        }
    }

    public StatelessBoardCell getCell(Cell cell) {
        return getCell(cell.x, cell.y);
    }

    public int getKey(final int x, final int y) {
        return y * size + x;
    }

    public StatelessBoardCell getCell(final int x, final int y) {
        return cells.get(getKey(x, y));
    }

    /**
     * @param cell the cell to test
     * @return if the cell is on board
     */
    public boolean isCellOnBoard(Cell cell) {
        return cell.x < size && cell.y < size && cell.x >= 0 && cell.y >= 0;
    }


    public List<StatelessBoardCell> getAllCells() {
        return allCells;
    }


    /**
     * check if two boards are equal
     */
    public boolean equals(StatelessGoBoard other) {

        // cannot be the same if board is null
        if (other == null) return false;

        return size == other.size;

    }


    /**
     * @return the board size
     */
    public int getSize() {
        return size;
    }


}