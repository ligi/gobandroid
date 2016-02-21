package org.ligi.gobandroid_hd.logic

import java.util.*

class StatelessBoardCell(cell: CellImpl, val board: StatelessGoBoard) : Cell by cell {

    private val neighbours = ArrayList<StatelessBoardCell>()
    lateinit var left: StatelessBoardCell
    lateinit var right: StatelessBoardCell
    lateinit var up: StatelessBoardCell
    lateinit var down: StatelessBoardCell

    fun assignNeighbours() {
        if (x > 0) {
            left = board.getCell(x - 1, y)
            neighbours.add(left)
        }

        if (y > 0) {
            up = board.getCell(x, y - 1)
            neighbours.add(up)
        }

        if (y < board.size - 1) {
            down = board.getCell(x, y + 1)
            neighbours.add(down)
        }


        if (x < board.size - 1) {
            right = board.getCell(x + 1, y)
            neighbours.add(right)
        }
    }


    val neighbors: List<StatelessBoardCell>
        get() = neighbours
}
