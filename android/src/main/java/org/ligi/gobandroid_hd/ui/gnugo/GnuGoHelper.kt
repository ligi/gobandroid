package org.ligi.gobandroid_hd.ui.gnugo

import android.content.Context
import android.content.Intent
import org.ligi.gobandroid_hd.logic.CellImpl
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.logic.StatefulGoBoard
import org.ligi.kaxt.isServiceAvailable

object GnuGoHelper {

    val INTENT_ACTION_NAME = "org.ligi.gobandroidhd.ai.gnugo.GnuGoService"

    fun isGnuGoAvail(ctx: Context)= Intent(INTENT_ACTION_NAME).isServiceAvailable(ctx.packageManager, 0)

    fun checkGnuGoSync(board_str: String, game: GoGame): Boolean {
        val b = StatefulGoBoard(game.statelessGoBoard)
        val split_board = board_str.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for (gnugo_y in 2..b.size + 1) {
            val act_line = split_board[gnugo_y].replace(" ", "").replace("" + (game.boardSize - (gnugo_y - 2)), "")
            for (gnugo_x in 0..b.size - 1) {
                val cell = CellImpl(gnugo_x, gnugo_y - 2)

                if (act_line.length < gnugo_x + 1 ||
                        act_line[gnugo_x] == '.' && !game.visualBoard.isCellFree(cell) ||
                        act_line[gnugo_x] == 'X' && !game.visualBoard.isCellBlack(cell) ||
                        act_line[gnugo_x] == 'O' && !game.visualBoard.isCellWhite(cell)) {
                    return false
                }

            }

        }

        return true
    }
}
