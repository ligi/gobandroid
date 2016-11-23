package org.ligi.gobandroid_hd.ui.review

import org.json.JSONException
import org.json.JSONObject
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.tracedroid.logging.Log

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

/**
 * stores and gives access to metatata to an SGF file

 * @author ligi
 */
class SGFMetaData private constructor(val metaFile: File) {

    var rating: Int? = null
    var isSolved = false
    private var hints_used: Int = 0

    private var has_data = false

    constructor(fileName: String) : this(File(sanitizeFileName(fileName))) {
    }

    init {


        if (metaFile.exists()) {
            try {

                val jObject = JSONObject(metaFile.bufferedReader().readText())

                try {
                    rating = jObject.getInt("rating")
                } catch (jse: org.json.JSONException) {
                }
                // don't care if not there

                try {
                    isSolved = jObject.getBoolean("is_solved")
                } catch (jse: org.json.JSONException) {
                }
                // don't care if not there

                try {
                    hints_used = jObject.getInt("hints_used")
                } catch (jse: org.json.JSONException) {
                }
                // don't care if not there

                has_data = true
            } catch (e: Exception) {
                Log.i("got json file " + e)
            }

        }
    }

    constructor(game: GoGame) : this(game.metaData.fileName + FNAME_ENDING) {
    }

    fun hasData(): Boolean {
        return has_data
    }

    fun persist() {
        try {
            val `object` = JSONObject()
            try {
                if (rating != null) {
                    `object`.put("rating", rating!!)
                }
                `object`.put("is_solved", isSolved)
                `object`.put("hints_used", hints_used)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val json_str = `object`.toString()
            val sgf_writer = FileWriter(metaFile)
            val out = BufferedWriter(sgf_writer)

            out.write(json_str)
            out.close()
            sgf_writer.close()
        } catch (e: Exception) {
            Log.w("problem writing metadata" + e)
        }

    }

    fun incHintsUsed() {
        hints_used++
    }

    var hintsUsed: Int
        get() = hints_used
        set(hints_used) {
            this.hints_used = hints_used
        }

    companion object {

        val FNAME_ENDING = ".sgfmeta"

        fun sanitizeFileName(fileName: String): String {
            var result = fileName

            if (fileName.startsWith("file://")) {
                result = fileName.substring(8)
            }

            if (!fileName.endsWith(FNAME_ENDING)) {
                result += FNAME_ENDING
            }

            return result
        }
    }
}
