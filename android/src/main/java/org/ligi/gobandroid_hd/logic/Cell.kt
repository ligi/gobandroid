package org.ligi.gobandroid_hd.logic

interface Cell {
    val x: Int
    val y: Int

    fun isEqual(other: Cell?): Boolean {
        return other != null && other.x == x && other.y == y
    }
}
