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

    val statelessGoBoard: StatelessGoBoard = StatelessGoBoard(size)
    val calcBoard: StatefulGoBoard = StatefulGoBoard(statelessGoBoard)// the board calculations are done in

    lateinit var visualBoard: StatefulGoBoard
    lateinit var handicapBoard: StatefulGoBoard

    private var groups: Array<IntArray>? = null // array to build groups

    var capturesWhite: Int = 0
    var capturesBlack: Int = 0

    var handicap = 0

    var komi = 6.5f

    lateinit var actMove: GoMove

    lateinit var metaData: GoGameMetadata

    private lateinit var all_handicap_positions: Array<BooleanArray>

    init {
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

        // create the array for group calculations
        groups = Array(size) { IntArray(size) }

        actMove = GoMove(null)
        actMove.setIsFirstMove()
        actMove.player = if(handicap == 0) PLAYER_WHITE else PLAYER_BLACK
        reset()
    }

    /**
     * set the handicap stones on the calc board
     */
    fun apply_handicap() {
        calcBoard.applyBoardState(handicapBoard.board)
    }

    fun reset() {
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
        if(actMove.isFinalMove) {
            // game is finished - players are marking dead stones
            return MoveStatus.VALID
        }

        val nextMove: GoMove = GoMove(cell, actMove, calcBoard)
        val errorStatus = nextMove.getErrorStatus(calcBoard)
        if (errorStatus != null) {
            return errorStatus
        }

        // check if the "new" move is in the variations - to not have 2 equal
        // move as different variations
        // if there is one matching use this move and we are done
        val matching_move = actMove.getNextMoveOnCell(cell)
        if (matching_move != null) {
            redo(matching_move)
            return MoveStatus.VALID
        }

        // if we reach this point it is a valid move
        // -> do things needed to do after a valid move
        actMove = nextMove
        actMove.apply(calcBoard)
        applyCaptures()
        refreshBoards()

        // if we reached this point this move must be valid
        return MoveStatus.VALID
    }

    fun repositionActMove(cell: Cell): MoveStatus {
        Log.i("repositionActMove " + cell)
        if(cell == actMove.cell) {
            return MoveStatus.VALID
        }

        undoCaptures()
        val moveStatus = actMove.repostition(calcBoard, cell)
        applyCaptures()
        refreshBoards()
        return moveStatus
    }

    fun canRedo(): Boolean {
        return actMove.hasNextMove()
    }

    val possibleVariationCount: Int
        get() {
            return actMove.nextMoveVariationCount
        }

    fun canUndo(): Boolean {
        return !actMove.isFirstMove// &&(!getGoMover().isMoversMove());
    }

    @JvmOverloads fun undo(keep_move: Boolean = true) {
        undoCaptures()
        actMove = actMove.undo(calcBoard, keep_move)
        refreshBoards()
    }

    fun redo(pos: Int) {
        actMove.getnextMove(pos)?.let {
            redo(it)
        }
    }

    fun redo(move: GoMove) {
        actMove = actMove.redo(calcBoard, move)
        applyCaptures()
        refreshBoards()
    }

    fun applyCaptures() {
        val local_captures = actMove.captures.size
        if(local_captures > 0) {
            actMove.cell?.let {
                if (calcBoard.isCellKind(it, STONE_WHITE)) {
                    capturesWhite += local_captures
                } else {
                    capturesBlack += local_captures
                }
            }
        }
    }

    fun undoCaptures() {
        val local_captures = actMove.captures.size
        if(local_captures > 0) {
            actMove.cell?.let {
                if (calcBoard.isCellKind(it, STONE_WHITE)) {
                    capturesWhite -= local_captures
                } else {
                    capturesBlack -= local_captures
                }
            }
        }
    }

    fun nextVariationWithOffset(offset: Int): GoMove? {
        if (actMove.isFirstMove) return null
        val variations = actMove.parent!!.nextMoveVariations
        val indexOf = variations.indexOf(actMove)
        return variations.elementAtOrNull(indexOf + offset)
    }

    fun refreshBoards() {
        copyVisualBoard()
        EventBus.getDefault().post(GameChangedEvent)
    }

    fun findFollowingMove(f: (GoMove) -> Boolean): GoMove? {
        return findMove({ it.getnextMove(0) }, f)
    }

    fun findPreviousMove(condition: (GoMove) -> Boolean): GoMove? {
        return findMove({ it.parent }, condition)
    }

    fun findMove(nextMove: (GoMove) -> GoMove?, condition: (GoMove) -> Boolean): GoMove? {
        var move: GoMove? = actMove
        while (move!=null) {
            if (condition(move)) return move
            move = nextMove(move)
        }
        return null
    }

    fun findFirstMove(): GoMove {
        return findPreviousMove { it.isFirstMove }!!
    }

    fun findLastMove(): GoMove {
        return findFollowingMove { !it.hasNextMove() }!!
    }

    fun findNextJunction(): GoMove? {
        return findFollowingMove { !it.hasNextMove() || it.hasNextMoveVariations() }
    }

    fun findPrevJunction(): GoMove? {
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
            replay_moves.add(tmp_move.parent!!)
        }

        reset()
        actMove = findFirstMove()
        for (step in replay_moves.indices.reversed()) {
            actMove = actMove.redo(calcBoard, replay_moves[step])
            applyCaptures()
        }

        refreshBoards()
    }

    fun copyVisualBoard() {
        visualBoard = calcBoard.clone()
    }

    /**
     * check if a group has liberties via flood fill

     * @return boolean weather the group has liberty
     */
    fun hasGroupLiberties(cell: Cell): Boolean {
        val startCell = statelessGoBoard.getCell(cell)
        return MustBeConnectedCellGatherer(calcBoard, startCell).gatheredCells.any {
            it.neighbors.any { neighbor -> calcBoard.isCellFree(neighbor) }
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
        statelessGoBoard.withAllCells {
            groups!![it.x][it.y] = -1
        }

        statelessGoBoard.withAllCells { statelessBoardCell ->
            if (groups!![statelessBoardCell.x][statelessBoardCell.y] == -1 && !calcBoard.isCellKind(statelessBoardCell, STONE_NONE)) {
                for (groupCell in MustBeConnectedCellGatherer(calcBoard, statelessBoardCell).gatheredCells) {
                    groups!![groupCell.x][groupCell.y] = group_count.get()
                }

                group_count.incrementAndGet()
            }
        }
    }

    /**
     * return if it's a handicap stone so that the view can visualize it
     *
     * TODO: check rename ( general marker )
     */
    fun isCellHoshi(cell: Cell) = all_handicap_positions[cell.x][cell.y]

    // need at least 2 moves to finish a game ( 2 passes )
    // w passes
    val isFinished: Boolean
        get() {
            if (actMove.isFirstMove) return false
            if (actMove.parent == null) return false
            return actMove.isPassMove && actMove.parent!=null && actMove.parent!!.isPassMove
        }

    /**
     * @return who has to do the next move
     */
    val isBlackToMove: Boolean
        get() = actMove.player == PLAYER_WHITE

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
