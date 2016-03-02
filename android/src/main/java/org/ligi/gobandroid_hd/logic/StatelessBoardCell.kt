package org.ligi.gobandroid_hd.logic

import java.util.*

class StatelessBoardCell(val cell: CellImpl, val board: StatelessGoBoard) : Cell by cell {

    var left: StatelessBoardCell? = null
    var right: StatelessBoardCell? = null
    var up: StatelessBoardCell? = null
    var down: StatelessBoardCell? = null

    val neighbors = HashSet<StatelessBoardCell>()

    fun assignNeighbours() {
        if (x > 0) {
            left = board.getCell(x - 1, y)
            neighbors.add(left!!)
        }

        if (y > 0) {
            up = board.getCell(x, y - 1)
            neighbors.add(up!!)
        }

        if (y < board.size - 1) {
            down = board.getCell(x, y + 1)
            neighbors.add(down!!)
        }

        if (x < board.size - 1) {
            right = board.getCell(x + 1, y)
            neighbors.add(right!!)
        }
    }
}
