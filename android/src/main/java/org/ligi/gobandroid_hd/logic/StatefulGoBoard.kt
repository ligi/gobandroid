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

/**
 * Class to represent a Go Board

 * @author [Marcus -Ligi- Bueschleb](http://ligi.de)
 * *
 *
 *
 * *         This software is licenced with GPLv3
 */

class StatefulGoBoard(val statelessGoBoard: StatelessGoBoard) : GoBoard by statelessGoBoard {

    @CellStatus
    val board: Array<ByteArray>

    init {
        board = Array(size) { ByteArray(size) }
    }

    /**
     * @param cell the cell to test
     * *
     * @return if the cell is on board
     */
    fun isCellOnBoard(cell: Cell): Boolean {
        return cell.x < size && cell.y < size && cell.x >= 0 && cell.y >= 0
    }

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

        // check if all stones are placed equally
        for (cell in statelessGoBoard.allCells) {
            if (board[cell.x][cell.y] != other.board[cell.x][cell.y]) return false
        }

        return true
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

    fun setCell(cell: Cell, @CellStatus newStatus: Byte) {
        board[cell.x][cell.y] = newStatus
    }

    fun toggleCellDead(cell: Cell) {

        board[cell.x][cell.y] = board[cell.x][cell.y].times(-1).toByte()
    }

    fun isCellDead(cell: Cell): Boolean {
        return board[cell.x][cell.y] < 0
    }
}