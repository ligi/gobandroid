package org.ligi.gobandroid_hd.test_helper_functions

import org.ligi.gobandroid_hd.logic.CellImpl

/**
 * The Petri dish
 */
fun getAllCellsForRect(sizeX: Int, sizeY: Int)
        = (0..sizeX * sizeY - 1).map { CellImpl(it % sizeY, it / sizeY) }
