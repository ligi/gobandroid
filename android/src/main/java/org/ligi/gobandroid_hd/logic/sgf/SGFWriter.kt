package org.ligi.gobandroid_hd.logic.sgf

import org.ligi.gobandroid_hd.logic.Cell
import org.ligi.gobandroid_hd.logic.GoDefinitions
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.logic.GoMove
import org.ligi.gobandroid_hd.logic.markers.TextMarker
import timber.log.Timber
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * logic to save SGF files ( serialize )
 */
object SGFWriter {

    private fun escapeSGF(txt: String): String {
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
        res.append(getSGFSnippet("KM", escapeSGF(game.komi.toString())))
        res.append(getSGFSnippet("RE", escapeSGF(game.metaData.result)))
        res.append(getSGFSnippet("SO", escapeSGF(game.metaData.source)))
        res.append("\n")

        game.statelessGoBoard.withAllCells {
            if (game.handicapBoard.isCellWhite(it)) {
                res.append("AW").append(SGFWriter.coords2SGFFragment(it)).append("\n")
            } else if (game.handicapBoard.isCellBlack(it)) {
                res.append("AB").append(SGFWriter.coords2SGFFragment(it)).append("\n")
            }
        }

        res.append(SGFWriter.moves2string(game.findFirstMove())).append(")")

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
        var actMove: GoMove? = move
        while (actMove != null) {
            // add the move
            if (!actMove.isFirstMove) {
                res.append(";").append(if (actMove.player == GoDefinitions.PLAYER_BLACK) "B" else "W")
                if (actMove.isPassMove) {
                    res.append("[]")
                } else {
                    res.append(coords2SGFFragment(actMove.cell!!)).append("\n")
                }
            }

            // add the comment
            if (!actMove.comment.isEmpty()) {
                res.append("C[").append(actMove.comment).append("]\n")
            }

            // add markers
            for (marker in actMove.markers) {
                res.append(marker.getMarkerCode())
                if (marker is TextMarker) {
                    res.append(coords2SGFFragment(marker).replace("]", ":" + marker.text + "]"))
                } else {
                    res.append(coords2SGFFragment(marker))
                }
            }

            var nextMove: GoMove? = null
            if (actMove.hasNextMove()) {
                if (actMove.hasNextMoveVariations()) {
                    for (variation in actMove.nextMoveVariations) {
                        res.append("(").append(moves2string(variation)).append(")")
                    }
                } else {
                    nextMove = actMove.getnextMove(0)
                }
            }

            actMove = nextMove
        }
        return res.toString()
    }

    private fun coords2SGFFragment(cell: Cell): String {
        return "[" + ('a' + cell.x) + ('a' + cell.y) + "]"
    }

    fun saveSGF(game: GoGame, file: File): Boolean {


        if (file.isDirectory) {
            throw IllegalArgumentException("cannot write - fname is a directory")
        }

        val parentFile = file.parentFile
                ?: // not really sure when this can be the
                // case ( perhaps only / ) - but the doc says it can be null and I would get NPE then
                throw IllegalArgumentException("bad filename " + file.absolutePath)

        if (!parentFile.isDirectory) {
            // if  the  path is not there yet
            parentFile.mkdirs()
        }

        try {
            file.createNewFile()
            val sgfWriter = FileWriter(file)
            val out = BufferedWriter(sgfWriter)

            out.write(game2sgf(game))
            out.close()
            sgfWriter.close()

        } catch (e: IOException) {
            Timber.i(e)
            return false
        }

        game.metaData.fileName = file.absolutePath
        return true
    }
}
