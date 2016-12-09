package org.ligi.gobandroid_hd.ui.tsumego

import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.logic.GoMove

class TsumegoController(val game: GoGame) {

    private val on_path_moves = recursive_add_on_path_moves(game.findFirstMove())
    val finishingMove = on_path_moves.find { isCorrectMove(it) }

    fun isFinishingMoveKnown() = finishingMove != null

    private fun recursive_add_on_path_moves(act: GoMove): List<GoMove>
            = mutableListOf(act).apply {
        act.nextMoveVariations.forEach {
            addAll(recursive_add_on_path_moves(it))
        }
    }

    fun isOnPath() = on_path_moves.contains(game.actMove)

    fun isCorrectMove(move: GoMove): Boolean {
        return move.comment.trim { it <= ' ' }.toUpperCase().startsWith("CORRECT") || // gogameguru style
                move.comment.toUpperCase().contains("RIGHT")
    }

}