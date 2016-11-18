package org.ligi.gobandroid_hd.logic.markers

import android.graphics.Paint
import org.ligi.gobandroid_hd.logic.Cell

abstract class BaseShapeMarker(cell: Cell) : GoMarker(cell) {

    internal val localPaint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }
    }

}
