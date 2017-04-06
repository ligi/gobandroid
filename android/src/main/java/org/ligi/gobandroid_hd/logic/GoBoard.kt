package org.ligi.gobandroid_hd.logic

interface GoBoard {
    val size: Int
    fun getCell(x: Int, y: Int): StatelessBoardCell
    fun getCell(cell: Cell): StatelessBoardCell
    fun isCellOnBoard(cell: Cell): Boolean
}
