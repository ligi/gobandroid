package org.ligi.gobandroid_hd.logic.markers

import android.graphics.Canvas
import android.graphics.Paint
import org.ligi.gobandroid_hd.logic.Cell

class TriangleMarker(cell: Cell) : BaseShapeMarker(cell) {

    override fun getMarkerCode() = "TR"

    override fun draw(c: Canvas, size: Float, x: Float, y: Float, paint: Paint) {
        c.drawLine(x, y - size / 3, x + size / 4, y + size / 4, localPaint)
        c.drawLine(x, y - size / 3, x - size / 4, y + size / 4, localPaint)
        c.drawLine(x - size / 4, y + size / 4, x + size / 4, y + size / 4, localPaint)
    }

    override fun equals(other: Any?) = super.equals(other) && other is TriangleMarker

}
