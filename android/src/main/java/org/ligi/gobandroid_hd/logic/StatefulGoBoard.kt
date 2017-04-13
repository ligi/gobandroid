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

import org.ligi.gobandroid_hd.logic.GoDefinitions.CellStatus

import org.ligi.gobandroid_hd.logic.GoDefinitions.getStringFromCellStatus
import org.ligi.gobandroid_hd.logic.cell_gatherer.MustBeConnectedCellGatherer

/**
 * Class to represent a Go Board

 */

class StatefulGoBoard(val statelessGoBoard: StatelessGoBoard) : GoBoard by statelessGoBoard {

    val board: Array<ByteArray> = Array(size) { ByteArray(size) }

    constructor(statelessGoBoard: StatelessGoBoard, predefined_board: Array<ByteArray>) : this(statelessGoBoard) {
        applyBoardState(predefined_board)
    }

    fun applyBoardState(predefined_board: Array<ByteArray>) {
        // copy the board
        for (x in 0..size - 1) {
            System.arraycopy(predefined_board[x], 0, board[x], 0, size)
        }
    }

    /**
     * clone this board
     *
     * @return a copy of this board
     */
    fun clone(): StatefulGoBoard {
        return StatefulGoBoard(statelessGoBoard, board)
    }

    /**
     * check if two boards are equal
     */
    fun equals(other: StatefulGoBoard?): Boolean {
        // cannot be the same if board is null
        if (other == null) return false

        // if the size is not matching the boards can't be equal
        if (size != other.size) return false

        val differingBoardCell = statelessGoBoard.findCellWithCondition { board[it.x][it.y] != other.board[it.x][it.y] }

        return differingBoardCell == null
    }

    /**
     * print a visual representation of the board via Log.d
     */
    override fun toString(): String {
        return toString(false)
    }

    fun toString(unicode: Boolean): String {
        val b = StringBuilder(size * size + size)
        for (y in 0..size - 1) {
            for (x in 0..size - 1) {
                @CellStatus val cellStatus = board[x][y]
                b.append(getStringFromCellStatus(cellStatus.toInt(), unicode))
            }
            b.append('\n')
        }
        return b.toString()
    }

    fun isCellFree(cell: Cell): Boolean {
        return board[cell.x][cell.y] <= 0
    }

    fun isCellKind(cell: Cell, kind: Byte): Boolean {
        return board[cell.x][cell.y] == kind
    }

    fun isCellBlack(cell: Cell): Boolean {
        return board[cell.x][cell.y] == GoDefinitions.STONE_BLACK
    }

    fun isCellWhite(cell: Cell): Boolean {
        return board[cell.x][cell.y] == GoDefinitions.STONE_WHITE
    }

    fun isCellDeadBlack(cell: Cell): Boolean {
        return -board[cell.x][cell.y] == GoDefinitions.STONE_BLACK.toInt()
    }

    fun isCellDeadWhite(cell: Cell): Boolean {
        return -board[cell.x][cell.y] == GoDefinitions.STONE_WHITE.toInt()
    }

    fun areCellsEqual(one: Cell, other: Cell): Boolean {
        return board[one.x][one.y] == board[other.x][other.y]
    }

    fun areCellsTogetherInArea(one: Cell, other: Cell): Boolean {
        return Math.max(board[one.x][one.y].toInt(), 0) == Math.max(board[other.x][other.y].toInt(), 0)
    }

    fun getCellKind(cell: Cell): Byte {
        return board[cell.x][cell.y];
    }

    fun setCell(cell: Cell, newStatus: Byte) {
        board[cell.x][cell.y] = newStatus
    }

    fun setCellGroup(cells: Collection<Cell>, newStatus: Byte) {
        cells.forEach { setCell(it, newStatus) }
    }

    fun toggleCellDead(cell: Cell) {
        board[cell.x][cell.y] = board[cell.x][cell.y].times(-1).toByte()
    }

    fun isCellDead(cell: Cell): Boolean {
        return board[cell.x][cell.y] < 0
    }

    fun doesCellGroupHaveLiberty(cell: Cell): Boolean {
        return getCellGroup(cell).any {
            it.neighbors.any { neighbor -> isCellFree(neighbor) }
        }
    }

    fun getCellGroup(cell: Cell): Set<StatelessBoardCell> {
        return MustBeConnectedCellGatherer(this, getCell(cell)).gatheredCells
    }
}
