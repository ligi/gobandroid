package org.ligi.gobandroid_hd.logic.markers

import android.graphics.Canvas
import android.graphics.Paint
import org.ligi.gobandroid_hd.logic.Cell

class SquareMarker(cell: Cell) : BaseShapeMarker(cell) {

    override fun getMarkerCode() = "SQ"

    override fun draw(c: Canvas, size: Float, x: Float, y: Float, paint: Paint) {
        val rect_size = size / 4
        c.drawRect(x - rect_size, y - rect_size, x + rect_size, y + rect_size, localPaint)
    }

    override fun equals(other: Any?) = super.equals(other) && other is SquareMarker

}
