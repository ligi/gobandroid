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

import org.ligi.gobandroid_hd.logic.GoDefinitions.*
import org.ligi.gobandroid_hd.logic.cell_gatherer.AreaCellGatherer
import java.util.*

/**
 * Class to calculate score a GoGame
 */
class GoGameScorer(private val game: GoGame) {

    var area_assign = Array(game.size) { ByteArray(game.size) } // cache to which player a area belongs in a  finished game

    var territory_white: Int = 0 // counter for the captures from black
    var territory_black: Int = 0 // counter for the captures from white

    var dead_white: Int = 0
    var dead_black: Int = 0

    fun calculateScore() {
        // reset groups
        val calc_board = game.calcBoard
        val statelessGoBoard = game.statelessGoBoard
        statelessGoBoard.withAllCells {
            area_assign[it.x][it.y] = 0
        }

        territory_black = 0
        territory_white = 0

        val processed = HashSet<Cell>()

        statelessGoBoard.withAllCells {
            val unprocessed = processed.add(it)
            if (unprocessed) {
                val boardCell = statelessGoBoard.getCell(it)
                val areaCellGatherer = AreaCellGatherer(calc_board, boardCell)
                if (calc_board.isCellFree(boardCell)) {
                    val assign = if (containsOneOfButNotTheOther(calc_board, areaCellGatherer.processed, PLAYER_BLACK)) {
                        territory_black += areaCellGatherer.gatheredCells.size
                        PLAYER_BLACK
                    } else if (containsOneOfButNotTheOther(calc_board, areaCellGatherer.processed, PLAYER_WHITE)) {
                        territory_white += areaCellGatherer.gatheredCells.size
                        PLAYER_WHITE
                    } else {
                        PLAYER_NONE
                    }

                    if (assign > 0)
                        for (areaBoardCell in areaCellGatherer.gatheredCells) {
                            area_assign[areaBoardCell.x][areaBoardCell.y] = assign
                        }
                    processed.addAll(areaCellGatherer.processed)
                }
            }
        }

        calculateDead()
        game.copyVisualBoard()
    }


    private fun containsOneOfButNotTheOther(board: StatefulGoBoard, set: Set<StatelessBoardCell>, kind: Byte): Boolean {
        var res = false
        for (boardCell in set) {
            if (board.isCellKind(boardCell, GoDefinitions.theOtherKind(kind))) {
                return false
            }
            res = res or board.isCellKind(boardCell, kind)
        }

        return res
    }

    private fun calculateDead() {
        dead_white = 0
        dead_black = 0
        game.statelessGoBoard.withAllCells {
            if (game.calcBoard.isCellDeadBlack(it)) {
                dead_black++
            } else if (game.calcBoard.isCellDeadWhite(it)) {
                dead_white++
            }
        }
    }

    val pointsWhite: Float
        get() = game.komi + game.capturesWhite + territory_white + dead_black

    val pointsBlack: Float
        get() = (game.capturesBlack + territory_black + dead_white).toFloat()

}