package org.ligi.gobandroid_hd.logic

data class CellImpl(override val x: Int, override val y: Int) : Cell {

    override fun toString() = "x:$x y:$y"

}
