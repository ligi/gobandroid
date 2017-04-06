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

package org.ligi.gobandroid_hd.logic

import android.util.SparseArray

/**
 * Class to represent a Go Board

 */

class StatelessGoBoard(override val size: Int) : GoBoard {

    private val cells = SparseArray<StatelessBoardCell>()

    init {
        for (x in 0..size - 1) {
            for (y in 0..size - 1) {
                val boardCell = StatelessBoardCell(CellImpl(x, y), this)
                cells.put(getKey(x, y), boardCell)
            }
        }

        withAllCells(StatelessBoardCell::assignNeighbours)
    }

    override fun getCell(cell: Cell): StatelessBoardCell {
        return getCell(cell.x, cell.y)
    }

    fun getKey(x: Int, y: Int): Int {
        return y * size + x
    }

    override fun getCell(x: Int, y: Int): StatelessBoardCell {
        return cells.get(getKey(x, y))
    }

    /**
     * @param cell the cell to test
     * *
     * @return if the cell is on board
     */
    override fun isCellOnBoard(cell: Cell): Boolean {
        return cell.x < size && cell.y < size && cell.x >= 0 && cell.y >= 0
    }

    fun withAllCells(func: (cell: StatelessBoardCell) -> Unit) {
        (0..(cells.size() - 1)).forEach {
            func(cells.valueAt(it))
        }
    }

    fun findCellWithCondition(condition: (cell: StatelessBoardCell) -> Boolean): StatelessBoardCell? {
        (0..(cells.size() - 1)).forEach {
            val cell = cells.valueAt(it)
            if (condition(cell)) return cell
        }
        return null
    }

    /**
     * check if two boards are equal
     */
    fun equals(other: StatelessGoBoard?): Boolean {

        // cannot be the same if board is null
        if (other == null) return false

        return size == other.size

    }

}