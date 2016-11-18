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
 * along with this program. If not, see //www.gnu.org/licenses/>.
 */

package org.ligi.gobandroid_hd.logic

import org.greenrobot.eventbus.EventBus
import org.ligi.gobandroid_hd.events.GameChangedEvent
import org.ligi.gobandroid_hd.logic.GoDefinitions.*
import org.ligi.gobandroid_hd.logic.cell_gatherer.MustBeConnectedCellGatherer
import org.ligi.tracedroid.logging.Log
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Class to represent a Go Game with its rules
 */

class GoGame @JvmOverloads constructor(size: Int, handicap: Int = 0) {

    enum class MoveStatus {
        VALID,
        INVALID_NOT_ON_BOARD,
        INVALID_CELL_NOT_FREE,
        INVALID_CELL_NO_LIBERTIES,
        INVALID_IS_KO
    }

    val statelessGoBoard: StatelessGoBoard
    val calcBoard: StatefulGoBoard // the board calculations are done in

    lateinit var visualBoard: StatefulGoBoard

    private var last_board: StatefulGoBoard? = null // board to detect KO situations
    private var pre_last_board: StatefulGoBoard? = null // board to detect KO situations
    lateinit var handicapBoard: StatefulGoBoard

    private var groups: Array<IntArray>? = null // array to build groups


    var capturesWhite: Int = 0
    var capturesBlack: Int = 0

    var handicap = 0

    var komi = 6.5f

    lateinit var actMove: GoMove

    lateinit var metaData: GoGameMetadata

    private lateinit var all_handicap_positions: Array<BooleanArray>

    private var local_captures = 0

    init {
        statelessGoBoard = StatelessGoBoard(size)
        calcBoard = StatefulGoBoard(statelessGoBoard)
        init(size, handicap)
    }


    var scorer: GoGameScorer? = null
        private set

    fun removeScorer() {
        scorer = null
    }

    fun initScorer() {
        scorer = GoGameScorer(this)
        scorer!!.calculateScore()
    }

    private fun init(size: Int, handicap: Int) {

        this.handicap = handicap

        // create the boards

        metaData = GoGameMetadata()

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        metaData.date = format.format(Date())

        handicapBoard = calcBoard.clone()

        all_handicap_positions = Array(size) { BooleanArray(size) }

        if (handicap > 0) komi = 0.5f

        if (getHandicapArray(size) != null) {
            val handicapArray = getHandicapArray(size)!!
            for (i in 0..8) {
                if (i < handicap) {
                    handicapBoard.setCell(CellImpl(handicapArray[i][0].toInt(), handicapArray[i][1].toInt()), STONE_BLACK)
                    if (i == 5 || i == 7) {
                        handicapBoard.setCell(CellImpl(handicapArray[4][0].toInt(), handicapArray[4][1].toInt()), STONE_NONE)
                        handicapBoard.setCell(CellImpl(handicapArray[i + 1][0].toInt(), handicapArray[i + 1][1].toInt()), STONE_BLACK)
                    } else if (i == 6 || i == 8) handicapBoard.setCell(CellImpl(handicapArray[4][0].toInt(), handicapArray[4][1].toInt()), STONE_BLACK)
                }
                all_handicap_positions[handicapArray[i][0].toInt()][handicapArray[i][1].toInt()] = true
            }
        }

        apply_handicap()

        copyVisualBoard()
        last_board = calcBoard.clone()
        pre_last_board = null

        // create the array for group calculations
        groups = Array(size) { IntArray(size) }

        actMove = GoMove(null)
        actMove.setIsFirstMove()
        actMove.setIsBlackToMove(handicap != 0) // if handicap==null set black
        // to move next - else set
        // white to move next
        reset()
    }

    /**
     * set the handicap stones on the calc board
     */
    fun apply_handicap() {
        calcBoard.applyBoardState(handicapBoard.board)
    }

    fun reset() {
        pre_last_board = null

        capturesBlack = 0
        capturesWhite = 0

    }

    fun pass() {
        if (actMove.isPassMove) {
            // finish game if both passed
            actMove = GoMove(actMove)
            actMove.setToPassMove()

            buildGroups()
        } else {
            actMove = GoMove(actMove)
            actMove.setToPassMove()
        }

        EventBus.getDefault().post(GameChangedEvent)
    }

    /**
     * place a stone on the board
     */
    fun do_move(cell: Cell): MoveStatus {
        Log.i("do_move " + cell)

        // check hard preconditions
        if (!calcBoard.isCellOnBoard(cell)) {
            // return with INVALID if x and y are inside the board
            return MoveStatus.INVALID_NOT_ON_BOARD
        }

        if (isFinished) {
            // game is finished - players are marking dead stones
            return MoveStatus.VALID
        }

        if (!calcBoard.isCellFree(cell)) {
            // can never place a stone where another is
            return MoveStatus.INVALID_CELL_NOT_FREE
        }

        // check if the "new" move is in the variations - to not have 2 equal
        // move as different variations
        val matching_move = actMove.getNextMoveOnCell(cell)

        // if there is one matching use this move and we are done
        if (matching_move != null) {
            jump(matching_move)
            return MoveStatus.VALID
        }

        val bak_board = calcBoard.clone()

        calcBoard.setCell(cell, if (isBlackToMove) STONE_BLACK else STONE_WHITE)

        remove_dead(cell)

        // move is a KO -> Invalid
        if (calcBoard.equals(pre_last_board)) {
            Log.i("illegal move -> KO")
            calcBoard.applyBoardState(bak_board.board)
            return MoveStatus.INVALID_IS_KO
        }

        if (!hasGroupLiberties(cell)) {
            Log.i("illegal move -> NO LIBERTIES")
            calcBoard.applyBoardState(bak_board.board)
            return MoveStatus.INVALID_CELL_NO_LIBERTIES
        }

        // if we reach this point it is a valid move
        // -> do things needed to do after a valid move

        pre_last_board = last_board!!.clone()
        last_board = calcBoard.clone()
        copyVisualBoard()

        actMove = GoMove(cell, actMove)

        if (!calcBoard.isCellKind(cell, STONE_WHITE))
            capturesBlack += local_captures
        else
            capturesWhite += local_captures

        actMove.setDidCaptures(local_captures > 0)
        EventBus.getDefault().post(GameChangedEvent)

        // if we reached this point this move must be valid
        return MoveStatus.VALID
    }

    fun canRedo(): Boolean {
        return actMove.hasNextMove()
    }

    val possibleVariationCount: Int
        get() {
            return actMove.nextMoveVariationCount
        }

    /**
     * moving without checks useful e.g. for undo / recorded games where we can
     * be sure that the move is valid and so be faster
     */
    fun do_internal_move(move: GoMove) {

        actMove = move
        if (move.isFirstMove || move.isPassMove) return

        calcBoard.setCell(move.cell, if (move.isBlackToMove) STONE_BLACK else STONE_WHITE)

        if (move.didCaptures()) {
            buildGroups()
            remove_dead(move.cell)

            if (calcBoard.isCellKind(move.cell, STONE_BLACK))
                capturesBlack += local_captures
            else
                capturesWhite += local_captures

        }
    }

    fun canUndo(): Boolean {
        return !actMove.isFirstMove// &&(!getGoMover().isMoversMove());
    }

    @JvmOverloads fun undo(keep_move: Boolean = true) {
        val mLastMove = actMove
        jump(mLastMove.parent)
        if (!keep_move) mLastMove.destroy()
    }


    fun redo(pos: Int) {
        Log.i("redoing " + actMove.getnextMove(pos).toString())
        jump(actMove.getnextMove(pos))
    }


    fun nextVariationWithOffset(offset: Int): GoMove? {
        if (actMove.isFirstMove) return null
        val variations = actMove.parent.nextMoveVariations
        val indexOf = variations.indexOf(actMove)
        return variations.elementAtOrNull(indexOf + offset)
    }

    fun refreshBoards() {
        jump(actMove)
    }

    fun findFollowingMove(f: (GoMove) -> Boolean): GoMove {
        return findMove({ it.getnextMove(0) }, f)
    }

    fun findPreviousMove(condition: (GoMove) -> Boolean): GoMove {
        return findMove({ it.parent }, condition)
    }

    fun findMove(nextMove: (GoMove) -> GoMove, condition: (GoMove) -> Boolean): GoMove {
        var move = actMove
        while (true) {
            if (condition(move)) return move
            move = nextMove(move)
        }
    }

    fun findFirstMove(): GoMove {
        return findPreviousMove { it.isFirstMove }
    }

    fun findLastMove(): GoMove {
        return findFollowingMove { !it.hasNextMove() }
    }

    fun findNextJunction(): GoMove {
        return findFollowingMove { !it.hasNextMove() || it.hasNextMoveVariations() }
    }

    fun findPrevJunction(): GoMove {
        return findPreviousMove { it.isFirstMove || (it.hasNextMoveVariations() && !it.isContentEqual(actMove.parent) && !it.isContentEqual(actMove)) }
    }

    fun jump(move: GoMove?) {

        if (move == null) {
            Log.w("move is null #shouldnothappen")
            return
        }

        clear_calc_board()

        val replay_moves = ArrayList<GoMove>()

        replay_moves.add(move)
        var tmp_move: GoMove
        while (true) {

            tmp_move = replay_moves.last()

            if (tmp_move.isFirstMove || tmp_move.parent == null) break

            replay_moves.add(tmp_move.parent)
        }

        reset()
        actMove = findFirstMove()

        for (step in replay_moves.indices.reversed())
            do_internal_move(replay_moves[step])

        copyVisualBoard()
        EventBus.getDefault().post(GameChangedEvent)
    }

    fun copyVisualBoard() {
        visualBoard = calcBoard.clone()
    }

    fun cell_has_neighbour(board: StatefulGoBoard, boardCell: StatelessBoardCell, kind: Byte): Boolean {
        return boardCell.neighbors.any { board.isCellKind(it, kind) }
    }

    /**
     * check if a group has liberties via flood fill

     * @return boolean weather the group has liberty
     */
    fun hasGroupLiberties(cell: Cell): Boolean {

        val startCell = calcBoard.statelessGoBoard.getCell(cell)

        return MustBeConnectedCellGatherer(calcBoard, startCell).gatheredCells.any() {
            cell_has_neighbour(calcBoard, it, STONE_NONE)
        }
    }

    fun clear_calc_board() {
        apply_handicap()
    }

    /**
     * group the stones
     *
     *
     * the result is written in groups[][]
     */
    fun buildGroups() {
        val group_count = AtomicInteger(0)

        // reset groups
        calcBoard.statelessGoBoard.withAllCells {
            groups!![it.x][it.y] = -1
        }

        calcBoard.statelessGoBoard.withAllCells { statelessBoardCell ->
            if (groups!![statelessBoardCell.x][statelessBoardCell.y] == -1 && !calcBoard.isCellKind(statelessBoardCell, STONE_NONE)) {

                for (groupCell in MustBeConnectedCellGatherer(calcBoard, statelessBoardCell).gatheredCells) {
                    groups!![groupCell.x][groupCell.y] = group_count.get()
                }

                group_count.incrementAndGet()
            }
        }
    }

    /**
     * remove dead groups from the board - e.g. after a move
     * the cell with ignore_x and ignore_y is ignored - e.g. last move
     */
    private fun remove_dead(where: Cell) {
        local_captures = 0

        val boardWhere = calcBoard.statelessGoBoard.getCell(where)

        for (boardCell in boardWhere.neighbors)
            if (!hasGroupLiberties(boardCell) && !calcBoard.areCellsEqual(boardWhere, boardCell)) remove_group(boardCell)
    }

    private fun remove_group(where: Cell) {

        if (calcBoard.isCellFree(where))
        // this is no "group" in the sense we want
            return

        val cellGathering = MustBeConnectedCellGatherer(calcBoard, calcBoard.statelessGoBoard.getCell(where)).gatheredCells

        cellGathering.forEach {
            local_captures++
            calcBoard.setCell(it, STONE_NONE)
        }
    }

    /**
     * return if it's a handicap stone so that the view can visualize it
     *
     *
     * TODO: check rename ( general marker )
     *
     */
    fun isCellHoschi(cell: Cell) = all_handicap_positions[cell.x][cell.y]


    // need at least 2 moves to finish a game ( 2 passes )
    // w passes
    val isFinished: Boolean
        get() {
            if (actMove.isFirstMove) return false
            if (actMove.parent == null) return false
            return actMove.isPassMove && actMove.parent.isPassMove
        }

    /**
     * @return who has to do the next move
     */
    // the opposite of wo was to move
    // before
    val isBlackToMove: Boolean
        get() = !actMove.isBlackToMove

    // TODO cache?
    val boardSize: Int
        get() = calcBoard.size

    fun setMetadata(metadata: GoGameMetadata) {
        this.metaData = metadata
    }

    val size: Int
        get() = visualBoard.size

    /**
     * just content as state is not checked ( position in game )
     *
     *
     * checks:
     * - size
     * - moves
     * - metadata ( TODO )
     */
    fun isContentEqualTo(other: GoGame): Boolean {
        return other.boardSize == boardSize && compareMovesRecursive(findFirstMove(), other.findFirstMove())
    }

    fun hasNextMove(move1: GoMove, expected: GoMove): Boolean {
        return move1.nextMoveVariations.any { it.isContentEqual(expected) }
    }

    private fun compareMovesRecursive(move1: GoMove, move2: GoMove): Boolean {
        if (!move1.isContentEqual(move2)) {
            return false
        }

        if (move1.hasNextMove() != move2.hasNextMove()) {
            return false
        }

        return !move1.nextMoveVariations.any { !hasNextMove(move1, it) }
    }


}
