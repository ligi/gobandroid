package org.ligi.gobandroid_hd.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class SpinnerTriangleView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val p = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val pth = Path()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val paddedRight = (w - paddingRight).toFloat()
        val paddedBottom = (h - paddingBottom).toFloat()

        pth.reset()
        pth.moveTo(paddedRight, paddingTop.toFloat())
        pth.lineTo(paddedRight, paddedBottom)
        pth.lineTo(paddingLeft.toFloat(), paddedBottom)
        pth.lineTo(paddedRight, paddingTop.toFloat())

        p.color = Color.BLACK
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(pth, p)
    }

}
