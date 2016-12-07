package org.ligi.gobandroid_hd.ui.editing

import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.CellImpl
import org.ligi.gobandroid_hd.logic.markers.CircleMarker
import org.ligi.gobandroid_hd.logic.markers.SquareMarker
import org.ligi.gobandroid_hd.logic.markers.TextMarker
import org.ligi.gobandroid_hd.logic.markers.TriangleMarker
import org.ligi.gobandroid_hd.ui.editing.model.EditGameMode
import org.ligi.gobandroid_hd.ui.editing.model.IconEditModeItem
import org.ligi.gobandroid_hd.ui.editing.model.MarkerEditModeItem

class StatefulEditModeItems {

    val cell = CellImpl(0, 0)

    val list = arrayOf(
            IconEditModeItem(R.drawable.stone_black, EditGameMode.BLACK, R.string.black),
            IconEditModeItem(R.drawable.stone_white, EditGameMode.WHITE, R.string.white),
            MarkerEditModeItem(CircleMarker(cell), EditGameMode.CIRCLE, R.string.circle),
            MarkerEditModeItem(SquareMarker(cell), EditGameMode.SQUARE, R.string.square),
            MarkerEditModeItem(TriangleMarker(cell), EditGameMode.TRIANGLE, R.string.triangle),
            MarkerEditModeItem(TextMarker(cell, "1"), EditGameMode.NUMBER, R.string.number),
            MarkerEditModeItem(TextMarker(cell, "A"), EditGameMode.LETTER, R.string.letter)
    )

    var mode = EditGameMode.BLACK

    fun isPositionMode(position: Int) = (list[position].mode == mode)

    fun setModeByPosition(position: Int) {
        mode = list[position].mode
    }
}
