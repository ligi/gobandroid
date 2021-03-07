package org.ligi.gobandroid_hd.ui.sgf_listing

import org.ligi.gobandroid_hd.logic.GoGame
import timber.log.Timber
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class GoLink(file: File) {


    var fileName = ""
        private set
    /**
     * returns the move Depth/pos
     * should only be used for displaying

     * @return
     */
    var moveDepth = 0
        private set

    constructor(fname: String) : this(File(fname.replace("file://", ""))) {
    }

    init {

        try {
            var go_lnk = file.bufferedReader().readText()
            go_lnk = go_lnk.replace("\n", "").replace("\r", "")
            fileName = go_lnk // backup
            val arr_content = go_lnk.split(":#".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            fileName = arr_content[0]
            fileName = fileName.replace("file://", "")
            moveDepth = Integer.parseInt(arr_content[1])
        } catch (e: Exception) {
        }

    }


    fun linksToDirectory(): Boolean {
        return File(fileName).isDirectory
    }

    /**
     * TODO care for remote content

     * @return
     */
    val sgfString: String
        get() {
            try {
                return File(fileName).bufferedReader().readText()
            } catch (e: IOException) {
                return ""
            }

        }

    companion object {

        fun isGoLink(fname: String): Boolean {
            return fname.endsWith(".golink")
        }

        fun isGoLink(file: File): Boolean {
            return file.name.endsWith(".golink")
        }

        fun saveGameToGoLink(game: GoGame, golink_path: File, golink_fname: String) {
            val move_pos = game.actMove.movePos

            if (!golink_path.isDirectory)
                golink_path.mkdirs()

            try {
                val golink_file = File(golink_path.toString() + "/" + golink_fname)

                val sgf_writer = FileWriter(golink_file)

                val out = BufferedWriter(sgf_writer)

                out.write(game.metaData.fileName + ":#" + move_pos)
                out.close()
                sgf_writer.close()
            } catch (e: IOException) {
                Timber.i("" + e)
            }

        }
    }
}
