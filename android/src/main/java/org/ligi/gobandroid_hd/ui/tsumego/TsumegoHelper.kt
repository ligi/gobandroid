package org.ligi.gobandroid_hd.ui.tsumego

import org.ligi.gobandroid_hd.logic.CellImpl
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.logic.GoMove

object TsumegoHelper {


    class Max(value: Int) {

        var value: Int = 0
            private set

        init {
            this.value = value
        }

        fun update(candidate: Int): Max {
            if (candidate > value) {
                value = candidate
            }

            return this
        }
    }

    /**
     * find how big the action is on the board - assuming it is top left corner

     * @param game
     * *
     * @return
     */
    fun calcSpan(game: GoGame, with_moves: Boolean): Int {
        val max = Max(0)

        game.calcBoard.statelessGoBoard.withAllCells {
            if (!game.handicapBoard.isCellFree(it)) {
                max.update(it.x).update(it.y)
            }
        }

        if (with_moves) {
            max.update(calcMaxMove(game.findFirstMove(), max).value)
        }

        return max.value
    }

    fun calcSpanAsPoint(game: GoGame): CellImpl {
        val maxX = Max(0)
        val maxY = Max(0)

        game.calcBoard.statelessGoBoard.withAllCells {
            if (!game.handicapBoard.isCellFree(it)) {
                maxX.update(it.x)
                maxY.update(it.y)
            }
        }

        return CellImpl(maxX.value, maxY.value)
    }


    fun calcMaxMove(move: GoMove?, act_max: Max): Max {
        if (move == null)
            return act_max

        for (variatonMove in move.nextMoveVariations)
            act_max.update(calcMaxMove(variatonMove, act_max).value)

        move.cell?.let {
            act_max.update(it.x).update(it.y)
        }

        return act_max
    }

    /**
     * calculate a Zoom factor so that all stones in handicap fit on bottom
     * right area

     * @return - the calculated Zoom factor
     */
    fun calcZoom(game: GoGame, with_moves: Boolean): Float {

        val max_span_size = calcSpan(game, with_moves)

        if (max_span_size == 0)
        // no predefined stones -> no zoom
            return 1.0f

        val calculated_zoom = game.size.toFloat() / (max_span_size + 2)

        if (calculated_zoom < 1.0f)
            return 1.0f
        else
            return calculated_zoom
    }


    fun calcPOI(game: GoGame, with_moves: Boolean): CellImpl {
        val poi = (game.size.toFloat() / 2f / calcZoom(game, with_moves)).toInt()
        return CellImpl(poi, poi)
    }

    fun calcTransform(game: GoGame): Int {
        // we count 4 quadrants to find the hot spot
        val count_h = IntArray(2)
        val count_v = IntArray(2)

        game.calcBoard.statelessGoBoard.withAllCells {
            if (!game.visualBoard.isCellFree(it)) {
                count_h[if (it.x > game.size / 2) 1 else 0]++
                count_v[if (it.y > game.size / 2) 1 else 0]++
            }
        }

        return if (count_v[0] > count_v[1]) 0 else 1 + if (count_h[0] > count_h[1]) 0 else 2
    }
}
