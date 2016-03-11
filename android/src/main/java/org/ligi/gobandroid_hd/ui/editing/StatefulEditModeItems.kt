package org.ligi.gobandroid_hd.ui.editing

import org.ligi.gobandroid_hd.R

class StatefulEditModeItems {

    val list: Array<EditModeItem> = arrayOf(
            EditModeItem(R.drawable.stone_black, EditGameMode.BLACK, R.string.black),
            EditModeItem(R.drawable.stone_white, EditGameMode.WHITE, R.string.white),
            EditModeItem(R.drawable.stone_circle, EditGameMode.CIRCLE, R.string.circle),
            EditModeItem(R.drawable.stone_square, EditGameMode.SQUARE, R.string.square),
            EditModeItem(R.drawable.stone_triangle, EditGameMode.TRIANGLE, R.string.triangle),
            EditModeItem(R.drawable.stone_number, EditGameMode.NUMBER, R.string.number),
            EditModeItem(R.drawable.stone_letter, EditGameMode.LETTER, R.string.letter)
    )

    var activatedItem = 0
    fun getActMode() = list[activatedItem].mode

}
