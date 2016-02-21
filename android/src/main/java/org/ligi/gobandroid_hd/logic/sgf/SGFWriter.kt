package org.ligi.gobandroid_hd.logic.sgf

import org.ligi.gobandroid_hd.logic.Cell
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.logic.GoMove
import org.ligi.gobandroid_hd.logic.markers.CircleMarker
import org.ligi.gobandroid_hd.logic.markers.SquareMarker
import org.ligi.gobandroid_hd.logic.markers.TextMarker
import org.ligi.gobandroid_hd.logic.markers.TriangleMarker
import org.ligi.tracedroid.logging.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * logic to save SGF files ( serialize )
 */
object SGFWriter {

    fun escapeSGF(txt: String): String {
        return txt.replace("]", "\\]")
                .replace(")", "\\)")
                .replace("\\", "\\\\")
    }

    private fun getSGFSnippet(cmd: String, param: String): String {
        if (param.isEmpty() || cmd.isEmpty()) {
            return ""
        }
        return cmd + "[" + escapeSGF(param) + "]"
    }

    fun game2sgf(game: GoGame): String {
        val res = StringBuilder("(;FF[4]GM[1]AP[gobandroid:0]") // header
        res.append(getSGFSnippet("SZ", "" + game.boardSize)) // board_size;
        res.append(getSGFSnippet("GN", escapeSGF(game.metaData.name)))
        res.append(getSGFSnippet("DT", escapeSGF(game.metaData.date)))
        res.append(getSGFSnippet("PB", escapeSGF(game.metaData.blackName)))
        res.append(getSGFSnippet("PW", escapeSGF(game.metaData.whiteName)))
        res.append(getSGFSnippet("BR", escapeSGF(game.metaData.blackRank)))
        res.append(getSGFSnippet("WR", escapeSGF(game.metaData.whiteRank)))
        res.append(getSGFSnippet("KM", escapeSGF(java.lang.Float.toString(game.komi))))
        res.append(getSGFSnippet("RE", escapeSGF(game.metaData.result)))
        res.append(getSGFSnippet("SO", escapeSGF(game.metaData.source)))
        res.append("\n")

        game.calcBoard.statelessGoBoard.withAllCells {
            if (game.handicapBoard.isCellWhite(it)) {
                res.append("AW").append(SGFWriter.coords2SGFFragment(it)).append("\n")
            } else if (game.handicapBoard.isCellBlack(it)) {
                res.append("AB").append(SGFWriter.coords2SGFFragment(it)).append("\n")
            }
        }

        res.append(SGFWriter.moves2string(game.firstMove)).append(")")

        return res.toString()
    }

    /**
     * convert tree of moves to a string to use in SGF next moves are processed
     * recursive

     * @param move - the start move
     * *
     * @return
     */

    internal fun moves2string(move: GoMove): String {
        val res = StringBuilder()

        var act_move: GoMove? = move

        while (act_move != null) {

            // add the move
            if (!act_move.isFirstMove) {
                res.append(";").append(if (act_move.isBlackToMove) "B" else "W")
                if (act_move.isPassMove) {
                    res.append("[]")
                } else {
                    res.append(coords2SGFFragment(act_move.cell)).append("\n")
                }
            }

            // add the comment
            if (!act_move.comment.isEmpty()) {
                res.append("C[").append(act_move.comment).append("]\n")
            }

            // add markers
            for (marker in act_move.markers) {
                if (marker is SquareMarker) {
                    res.append("SQ").append(coords2SGFFragment(marker))
                } else if (marker is TriangleMarker) {
                    res.append("TR").append(coords2SGFFragment(marker))
                } else if (marker is CircleMarker) {
                    res.append("CR").append(coords2SGFFragment(marker))
                } else if (marker is TextMarker) {
                    res.append("LB")
                    res.append(coords2SGFFragment(marker).replace("]", ":" + marker.text + "]"))
                }
            }

            var next_move: GoMove? = null

            if (act_move.hasNextMove()) {
                if (act_move.hasNextMoveVariations()) {
                    for (`var` in act_move.nextMoveVariations) {
                        res.append("(").append(moves2string(`var`)).append(")")
                    }
                } else {
                    next_move = act_move.getnextMove(0)
                }
            }

            act_move = next_move
        }
        return res.toString()
    }

    private fun coords2SGFFragment(cell: Cell): String {
        return "[" + ('a' + cell.x).toChar() + ('a' + cell.y).toChar() + "]"
    }

    fun saveSGF(game: GoGame, fname: String): Boolean {

        val f = File(fname)

        if (f.isDirectory) {
            throw IllegalArgumentException("cannot write - fname is a directory")
        }

        if (f.parentFile == null) {
            // not really sure when this can be the
            // case ( perhaps only / ) - but the doc says it can be null and I would get NPE then
            throw IllegalArgumentException("bad filename " + fname)
        }

        if (!f.parentFile.isDirectory) {
            // if  the  path is not there yet
            f.parentFile.mkdirs()
        }

        try {
            f.createNewFile()

            val sgf_writer = FileWriter(f)

            val out = BufferedWriter(sgf_writer)

            out.write(game2sgf(game))
            out.close()
            sgf_writer.close()

        } catch (e: IOException) {
            Log.i("" + e)
            return false
        }

        game.metaData.fileName = fname
        return true
    }
}
