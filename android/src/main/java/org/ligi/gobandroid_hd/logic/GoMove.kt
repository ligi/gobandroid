/**
 * gobandroid
 * by Marcus -Ligi- Bueschleb
 * http://ligi.de
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */

package org.ligi.gobandroid_hd.logic

import org.ligi.gobandroid_hd.logic.GoDefinitions.*
import org.ligi.gobandroid_hd.logic.GoGame.MoveStatus
import org.ligi.gobandroid_hd.logic.markers.GoMarker
import org.ligi.tracedroid.logging.Log
import java.util.*

/**
 * Class to represent a Go Move
 */
class GoMove(val parent: GoMove?) {

    var cell: Cell? = null
    var comment = ""

    var movePos = 0
        private set
    var player = PLAYER_BLACK
    var isPassMove = false
        private set
    var isFirstMove = false
        private set

    val nextMoveVariations: MutableList<GoMove> = ArrayList()
    val markers = ArrayList<GoMarker>()
    val captures = ArrayList<Cell>()

    init {
        if (parent != null) {
            player = if (parent.player == PLAYER_BLACK) PLAYER_WHITE else PLAYER_BLACK
            movePos = parent.movePos + 1
        }
    }

    constructor(cell: Cell, parent: GoMove) : this(parent) {
        this.cell = cell
    }

    constructor(cell: Cell, parent: GoMove, board: StatefulGoBoard) : this(cell, parent) {
        if (!board.isCellOnBoard(cell)) {
            return
        }

        buildCaptures(board)
    }

    fun apply(board: StatefulGoBoard) {
        parent!!.addNextMove(this)
        board.setCell(cell!!, cellStatus.toByte())
        board.setCellGroup(captures, STONE_NONE)
    }

    fun undo(board: StatefulGoBoard, keepMove: Boolean): GoMove {
        if (parent == null) {
            return this
        } else if (!keepMove) {
            parent.nextMoveVariations.remove(this)
        }

        if (cell != null) {
            board.setCell(cell!!, STONE_NONE)
            board.setCellGroup(captures, capturedCellStatus.toByte())
        }

        return parent
    }

    fun redo(board: StatefulGoBoard, next: GoMove): GoMove {
        if (!nextMoveVariations.contains(next)) {
            return this
        }

        next.buildCaptures(board)
        next.apply(board)
        return next
    }

    fun repostition(board: StatefulGoBoard, cell: Cell?): MoveStatus {
        if (cell == null || !board.isCellOnBoard(cell)) {
            return MoveStatus.INVALID_NOT_ON_BOARD
        }

        undo(board, false)
        val previousCell = this.cell
        this.cell = cell
        buildCaptures(board)
        val errorStatus = getErrorStatus(board)
        if (errorStatus == null) {
            apply(board)
            return MoveStatus.VALID
        } else {
            //the move is invalid, so back out the changes
            this.cell = previousCell
            buildCaptures(board)
            apply(board)
            return errorStatus
        }
    }

    fun getErrorStatus(board: StatefulGoBoard): MoveStatus? {
        // check hard preconditions
        if (!board.isCellOnBoard(cell!!)) {
            // return with INVALID if x and y are inside the board
            return MoveStatus.INVALID_NOT_ON_BOARD
        } else if (!board.isCellFree(cell!!)) {
            // can never place a stone where another is
            return MoveStatus.INVALID_CELL_NOT_FREE
        } else if (isIllegalKo) {
            Log.i("illegal move -> KO")
            return MoveStatus.INVALID_IS_KO
        } else if (isIllegalNoLiberties(board)) {
            Log.i("illegal move -> NO LIBERTIES")
            return MoveStatus.INVALID_CELL_NO_LIBERTIES
        }

        return null
    }

    private val isIllegalKo: Boolean
        get() = parent != null &&
                captures.size == 1 &&
                parent.captures.size == 1 &&
                parent.captures[0].isEqual(cell)

    private fun isIllegalNoLiberties(board: StatefulGoBoard): Boolean {
        if (!captures.isEmpty()) {
            return false
        }

        val previousStatus = board.getCellKind(cell!!)
        board.setCell(cell!!, cellStatus.toByte())
        val hasLiberties = board.doesCellGroupHaveLiberty(cell!!)
        board.setCell(cell!!, previousStatus)
        return !hasLiberties
    }

    private fun buildCaptures(board: StatefulGoBoard) {
        captures.clear()

        cell?.let {

            //temporarily apply the move in order to calculate captures
            val previousStatus = board.getCellKind(it)
            board.setCell(it, cellStatus.toByte())

            val boardCell = board.getCell(it)
            for (neighbor in boardCell.neighbors) {
                if (!captures.contains(neighbor) && !board.isCellFree(neighbor) && !board.areCellsEqual(neighbor, it) && !board.doesCellGroupHaveLiberty(neighbor)) {
                    val cellGroup = board.getCellGroup(neighbor)
                    captures.addAll(cellGroup)
                }
            }

            //reset the cell to its original position
            board.setCell(it, previousStatus)
        }

    }

    fun hasNextMove(): Boolean {
        return !nextMoveVariations.isEmpty()
    }

    fun getNextMoveOnCell(cell: Cell): GoMove? {
        for (next_move_variation in nextMoveVariations) {
            if (next_move_variation.cell != null && next_move_variation.cell == cell) {
                return next_move_variation
            }
        }
        return null
    }

    fun hasNextMoveVariations(): Boolean {
        return nextMoveVariations.size > 1
    }

    val nextMoveVariationCount: Int
        get() = nextMoveVariations.size

    fun addNextMove(move: GoMove) {
        if (!nextMoveVariations.contains(move)) {
            nextMoveVariations.add(move)
        }
    }

    fun setToPassMove() {
        cell = null
        isPassMove = true
    }

    fun setIsFirstMove() {
        isFirstMove = true
    }

    fun isOnCell(cell: Cell?): Boolean {
        return cell === this.cell || this.cell != null && this.cell == cell
    }

    fun getnextMove(pos: Int): GoMove? {
        return if (nextMoveVariations.size > pos) nextMoveVariations[pos] else null
    }

    override fun toString(): String {
        var s = "{ cell="
        if (cell != null) {
            s += cell!!.toString()
        } else {
            s += "null"
        }
        s += "; comment=" + comment
        return s + "}"
    }

    fun hasComment(): Boolean {
        return !comment.isEmpty()
    }

    fun addComment(newComment: String) {
        comment += newComment
    }

    /**
     * @return the markers - e.g. from SGF Problems
     */
    fun getMarkers(): List<GoMarker> {
        return markers
    }

    fun addMarker(marker: GoMarker) {
        markers.add(marker)
    }

    fun getCaptures(): List<Cell> {
        return captures
    }

    val goMarker: GoMarker?
        get() {
            if (parent != null) {
                for (marker in parent.getMarkers()) {
                    if (marker == cell) {
                        return marker
                    }
                }
            }
            return null
        }

    val isMarked: Boolean
        get() = goMarker == null

    fun destroy() {
        parent?.nextMoveVariations?.remove(this)
    }

    fun isContentEqual(other: GoMove?): Boolean {
        if (other==null) {
            return false
        }
        if (!other.isOnCell(cell) || comment != other.comment || markers.size != other.markers.size) {
            return false
        }

        for (marker in getMarkers()) {
            if (!other.getMarkers().contains(marker)) {
                return false
            }
        }

        if (player != other.player || movePos != other.movePos) {
            return false
        }

        // TODO check if we are complete
        return true
    }

    val isFinalMove: Boolean
        get() = !isFirstMove && parent != null && isPassMove && parent.isPassMove

    private val cellStatus: Int
        @CellStatus
        get() = (if (player == PLAYER_BLACK) STONE_BLACK else STONE_WHITE).toInt()

    private val capturedCellStatus: Int
        @CellStatus
        get() = (if (player == PLAYER_BLACK) STONE_WHITE else STONE_BLACK).toInt()

}
