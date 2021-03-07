/**
 * gobandroid
 * by Marcus -Ligi- Bueschleb
 * http://ligi.de

 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.

 */

package org.ligi.gobandroid_hd.logic.sgf

import android.text.TextUtils
import org.ligi.gobandroid_hd.logic.*
import org.ligi.gobandroid_hd.logic.GoGame.MoveStatus.VALID
import org.ligi.gobandroid_hd.logic.markers.CircleMarker
import org.ligi.gobandroid_hd.logic.markers.SquareMarker
import org.ligi.gobandroid_hd.logic.markers.TextMarker
import org.ligi.gobandroid_hd.logic.markers.TriangleMarker
import timber.log.Timber
import java.util.*

/**
 * class for load games in SGF File-Format
 */
class SGFReader private constructor(private val sgf: String, private val callback: SGFReader.ISGFLoadProgressCallback?, private val breakon: Int, private val transform: Int) {

    private var act_param = ""
    private var act_cmd = ""
    private var last_cmd = ""
    private var game: GoGame? = null
    private var size: Byte = -1
    private var predef_count_b = 0
    private var predef_count_w = 0
    private var break_pulled = false

    private val metadata: GoGameMetadata = GoGameMetadata()
    private val variationList: MutableList<GoMove> = ArrayList()

    interface ISGFLoadProgressCallback {
        fun progress(act: Int, max: Int, progress_val: Int)
    }

    private fun getGame(): GoGame {
        var opener: Byte = 0
        var escape = false
        var consuming_param = false

        var p = 0
        while (p < sgf.length && !break_pulled) {
            val act_char = sgf[p]
            if (!consuming_param) {
                // non-consuming command
                when (act_char) {
                    '\r', '\n', ';', '\t', ' ' -> {
                        if (!act_cmd.isEmpty()) {
                            last_cmd = act_cmd
                        }
                        act_cmd = ""
                    }

                    '[' -> {
                        if (act_cmd.isEmpty()) {
                            act_cmd = last_cmd
                        }

                        // for files without SZ - e.g. ggg-intermediate-11.sgf
                        val toCheck = Arrays.asList(Marker.ADD_BLACK, Marker.ADD_WHITE, Marker.MARK_X, Marker.MARK_TRIANGLE, Marker.MARK_SQUARE, Marker.MARK_POINT)
                        if (game == null && toCheck.contains(Marker.withCode(act_cmd))) {
                            size = 19
                            game = GoGame(size.toInt())
                        }
                        consuming_param = true
                        act_param = ""
                    }

                    '(' -> if (!consuming_param) {
                        // for files without SZ
                        if (opener.toInt() == 1 && game == null) {
                            size = 19
                            game = GoGame(19.toByte().toInt())
                            variationList.add(orCreateGame().actMove)
                        }

                        opener++

                        // push the move we where to the stack to return
                        // here after the variation
                        // if (param_level!=0) break;
                        Timber.i("   !!! opening variation" + game)
                        if (game != null) {
                            variationList.add(orCreateGame().actMove)
                        }

                        last_cmd = ""
                        act_cmd = ""
                    }
                    ')' -> {
                        if (variationList.isEmpty()) {
                            Timber.w("variation vector underrun!!")
                        } else {
                            val lastMove = variationList[variationList.size - 1]
                            orCreateGame().jump(lastMove)
                            variationList.remove(lastMove)
                            Timber.w("popping variaton from stack")
                        }

                        last_cmd = ""
                        act_cmd = ""
                    }
                    else -> act_cmd += Character.toString(sgf[p])
                }
            } else {
                // consuming param
                when (act_char) {
                    ']' // closing command parameter -> can process command now
                    -> {
                        if (game != null && callback != null) {
                            callback.progress(p, sgf.length, orCreateGame().actMove.movePos)
                        }
                        if (!escape) {
                            consuming_param = false
                            processCommand()
                        }
                    }
                    '\\' -> if (escape) {
                        act_param += Character.toString(act_char)
                        escape = false
                    } else
                        escape = true
                    else -> {
                        act_param += Character.toString(act_char)
                        escape = false
                    }
                }

            }
            p++
        }

        if (game != null) {
            game!!.setMetadata(metadata)
            if (game!!.actMove.isFirstMove && predef_count_w == 0 && predef_count_b > 0) {
                game!!.actMove.player = GoDefinitions.PLAYER_BLACK // probably handicap - so  make white
                // to  move - very  important for cloud game and handicap
            }
        }

        //if (game.getFirstMove())
        return orCreateGame()
    }

    private fun processCommand() {
        var paramX = 0
        var paramY = 0

        // if we have a minimum of 2 chars in param - could be coords - so parse
        if (act_param.length >= 2) {
            paramX = act_param[if (transform and 4 == 0) 0 else 1] - 'a'
            paramY = act_param[if (transform and 4 == 0) 1 else 0] - 'a'

            if (size > -1) {
                if (transform and 1 > 0)
                    paramY = (size.toInt() - 1 - paramY).toByte().toInt()

                if (transform and 2 > 0)
                    paramX = (size.toInt() - 1 - paramX).toByte().toInt()
            }
        }

        val cell = CellImpl(paramX, paramY)
        // if command is empty -> use the last command
        if (act_cmd.isEmpty()) {
            act_cmd = last_cmd
        }

        // marker section - info here http://www.red-bean.com/sgf/properties.html
        val marker = Marker.withCode(act_cmd)
        when (marker) {
            SGFReader.Marker.BLACK_MOVE, SGFReader.Marker.WHITE_MOVE -> {
                // if still no game open -> open one with default size
                if (game == null) {
                    game = GoGame(19.toByte().toInt())
                    variationList.add(game!!.actMove)
                }

                if (game!!.actMove.isFirstMove) {
                    val lastPlayer = if (marker == Marker.WHITE_MOVE) GoDefinitions.PLAYER_BLACK else GoDefinitions.PLAYER_WHITE;
                    game!!.actMove.player = lastPlayer
                }

                if (breakon and BREAKON_FIRSTMOVE > 0) {
                    break_pulled = true
                }

                if (act_param.length == 0) {
                    game!!.pass()
                } else {
                    val moveStatus = game!!.do_move(cell)
                    if (moveStatus !== VALID) {
                        Timber.w("There was a problem in this game")
                    }
                }
            }
            SGFReader.Marker.ADD_BLACK, SGFReader.Marker.ADD_WHITE -> {
                Timber.i("Adding stone $act_cmd $act_param at $cell")
                if (game == null) { // create a game if it is not there yet
                    game = GoGame(19.toByte().toInt())
                    variationList.add(game!!.actMove)
                }

                if (act_param.length != 0) {
                    if (game!!.isBlackToMove && marker == Marker.ADD_BLACK) {
                        predef_count_b++
                        game!!.handicapBoard.setCell(cell, GoDefinitions.STONE_BLACK)
                        game!!.calcBoard.setCell(cell, GoDefinitions.STONE_BLACK)
                    } else if (game!!.isBlackToMove && marker == Marker.ADD_WHITE) {
                        predef_count_w++
                        game!!.handicapBoard.setCell(cell, GoDefinitions.STONE_WHITE)
                        game!!.calcBoard.setCell(cell, GoDefinitions.STONE_WHITE)
                    }
                } else {
                    Timber.w("AB / AW command without param")
                }
            }
            SGFReader.Marker.WRITE_TEXT -> {
                val inner = act_param.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val txt = if (inner.size > 1) inner[1] else "X"
                orCreateGame().actMove.addMarker(TextMarker(cell, txt))
            }
            SGFReader.Marker.MARK_X -> orCreateGame().actMove.addMarker(TextMarker(cell, "X"))
            SGFReader.Marker.MARK_TRIANGLE -> orCreateGame().actMove.addMarker(TriangleMarker(cell))
            SGFReader.Marker.MARK_SQUARE -> orCreateGame().actMove.addMarker(SquareMarker(cell))
            SGFReader.Marker.MARK_CIRCLE -> orCreateGame().actMove.addMarker(CircleMarker(cell))
            SGFReader.Marker.MARK_POINT -> orCreateGame().actMove.addMarker(TextMarker(cell, "+"))
            SGFReader.Marker.GAME_NAME -> metadata.name = act_param
            SGFReader.Marker.DIFFICULTY -> metadata.difficulty = act_param
            SGFReader.Marker.WHITE_NAME -> metadata.whiteName = act_param
            SGFReader.Marker.BLACK_NAME -> metadata.blackName = act_param
            SGFReader.Marker.WHITE_RANK -> metadata.whiteRank = act_param
            SGFReader.Marker.BLACK_RANK -> metadata.blackRank = act_param
            SGFReader.Marker.GAME_RESULT -> metadata.result = act_param
            SGFReader.Marker.DATE -> metadata.date = act_param
            SGFReader.Marker.SOURCE -> metadata.source = act_param
            SGFReader.Marker.KOMI -> try {
                orCreateGame().komi = java.lang.Float.parseFloat(act_param)
            } catch (ignored: NumberFormatException) {
                // catch a bad komi-statement like seen KM[] - would now even catch KM[crashme]
            }

            SGFReader.Marker.SIZE -> {
                act_param = act_param.replace("[^0-9]".toRegex(), "")
                if (!act_param.isEmpty()) { // got a SGF with SZ[]
                    size = java.lang.Byte.parseByte(act_param)
                    if (game == null || game!!.boardSize != size.toInt()) {
                        game = GoGame(size.toInt())
                        variationList.add(game!!.actMove)
                    }
                }
            }
            SGFReader.Marker.COMMENT -> if (game != null) {
                game!!.actMove.comment = act_param
            }
            SGFReader.Marker.UNKNOWN, SGFReader.Marker.NONE -> {
            }
        }

        last_cmd = act_cmd
        act_cmd = ""
        act_param = ""
    }

    private fun orCreateGame(): GoGame {
        if (game == null) {
            size = 19
            game = GoGame(size.toInt())
        }

        return game!!
    }


    internal enum class Marker {
        //Move Properties
        BLACK_MOVE("B", "Black"),
        WHITE_MOVE("W", "White"),

        //Setup Properties
        ADD_BLACK("AB", "AddBlack"),
        ADD_WHITE("AW", "AddWhite"),

        //Node Annotation Properties
        COMMENT("C", "Comment"),

        //Markup Properties
        WRITE_TEXT("LB"),
        MARK_X("MA", "Mark"), MARK_TRIANGLE("TR"),
        MARK_SQUARE("SQ"), MARK_CIRCLE("CR"), MARK_POINT("SL"),

        //Game Info Properties
        GAME_NAME("GN"),
        DIFFICULTY("DI"), WHITE_NAME("PW"),
        BLACK_NAME("PB"), WHITE_RANK("WR"), BLACK_RANK("BR"),
        GAME_RESULT("RE"), DATE("DT"), SOURCE("SO"),
        KOMI("KM"), SIZE("SZ", "SiZe"),

        UNKNOWN("?"),
        NONE("");

        private var code: String? = null
        private var alternateCode: String? = null

        private constructor(code: String) {
            this.code = code
        }

        private constructor(code: String, alternateCode: String) {
            this.code = code
            this.alternateCode = alternateCode
        }

        companion object {

            fun withCode(code: String): Marker {
                if (TextUtils.isEmpty(code)) {
                    return NONE
                }

                for (marker in values()) {
                    if (code == marker.code || code == marker.alternateCode) {
                        return marker
                    }
                }

                return UNKNOWN
            }
        }
    }

    companion object {

        val BREAKON_NOTHING = 0
        val BREAKON_FIRSTMOVE = 1

        val DEFAULT_SGF_TRANSFORM = 0

        /**
         * @param sgf
         * *
         * @param callback
         * *
         * @param breakon
         * *
         * @param transform - bit 1 => mirror y ; bit 2 => mirror x ; bit 3 => swap x/y
         * *
         * @return
         */
        @JvmOverloads fun sgf2game(sgf: String?, callback: ISGFLoadProgressCallback?, breakon: Int = BREAKON_NOTHING, transform: Int = DEFAULT_SGF_TRANSFORM): GoGame? {
            try {
                return SGFReader(sgf!!, callback, breakon, transform).getGame()
            } catch (e: Exception) { // some weird sgf - we want to catch to not FC
                // and have the chance to send the sgf to analysis
                e.printStackTrace()
                Timber.w("Problem parsing SGF " + e)
                return null
            }

        }
    }
}
