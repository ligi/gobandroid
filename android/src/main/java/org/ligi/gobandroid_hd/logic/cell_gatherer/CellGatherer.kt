package org.ligi.gobandroid_hd.logic.cell_gatherer

import org.ligi.gobandroid_hd.logic.StatefulGoBoard
import org.ligi.gobandroid_hd.logic.StatelessBoardCell
import java.util.*

abstract class CellGatherer(protected val board: StatefulGoBoard, protected val root: StatelessBoardCell) {

    val gatheredCells = HashSet<StatelessBoardCell>()
    val processed = HashSet<StatelessBoardCell>()

    init {
        process(root)
    }

    open protected fun process(cell: StatelessBoardCell) {
        if (processed.add(cell) && board.areCellsEqual(cell, root)) {
            gatheredCells.add(cell)
            processNeighbors(cell)
        }
    }


    open protected fun processNeighbors(cell: StatelessBoardCell) {
        cell.neighbors.forEach {
            process(it)
        }
    }

}
