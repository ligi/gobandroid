package org.ligi.gobandroid_hd.logic.cell_gatherer

import org.ligi.gobandroid_hd.logic.StatefulGoBoard
import org.ligi.gobandroid_hd.logic.StatelessBoardCell

open class MustBeConnectedCellGatherer(board: StatefulGoBoard, root: StatelessBoardCell) : CellGatherer(board, root) {


    override fun process(cell: StatelessBoardCell) {
        if (processed.add(cell) && board.areCellsEqual(cell, root)) {
            gatheredCells.add(cell)

            cell.neighbors.forEach {
                process(it)
            }

        }
    }

}
