package org.ligi.gobandroid_hd.logic.cell_gatherer

import org.ligi.gobandroid_hd.logic.StatefulGoBoard
import org.ligi.gobandroid_hd.logic.StatelessBoardCell
import java.util.*

class LooseConnectedCellGatherer(board: StatefulGoBoard, root: StatelessBoardCell) : CellGatherer(board, root) {
    override fun processAndGetFollowup(toProcess: Set<StatelessBoardCell>): HashSet<StatelessBoardCell> {
        val followUp = HashSet<StatelessBoardCell>()
        toProcess.forEach {
            val unProcessed = processed.add(it)

            if (board.areCellsEqual(it, root)) {
                gatheredCells.add(it)
                if (unProcessed) {
                    followUp.addAll(it.neighbors)
                }
            } else if (board.isCellFree(it) && unProcessed) {
                followUp.addAll(it.neighbors)
            }

        }

        return followUp
    }


}
