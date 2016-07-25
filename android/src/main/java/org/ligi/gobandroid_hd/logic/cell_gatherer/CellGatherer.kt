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

    protected fun process(cell: StatelessBoardCell) {

        var toProcess = hashSetOf(cell)

        while (!toProcess.isEmpty()) {
            toProcess = processAndGetFollowup(toProcess)
        }

    }

    open fun processAndGetFollowup(toProcess: Set<StatelessBoardCell>): HashSet<StatelessBoardCell> {
        val followUp = HashSet<StatelessBoardCell>()
        toProcess.forEach {
            if (processed.add(it) && board.areCellsEqual(it, root)) {
                gatheredCells.add(it)
                followUp.addAll(it.neighbors)
            }
        }
        return followUp
    }



}
