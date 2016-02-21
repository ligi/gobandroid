package org.ligi.gobandroid_hd.logic.markers

import android.graphics.Canvas
import android.graphics.Paint
import org.ligi.gobandroid_hd.logic.Cell

open class BaseShapeMarker(cell: Cell) : GoMarker(cell) {

    internal var localPaint: Paint? = null

    override fun draw(c: Canvas, size: Float, x: Float, y: Float, paint: Paint) {
        if (localPaint == null) {
            localPaint = Paint(paint)
            localPaint!!.style = Paint.Style.STROKE
            localPaint!!.strokeWidth = 3f
            localPaint!!.isAntiAlias = true
        }
    }
}
