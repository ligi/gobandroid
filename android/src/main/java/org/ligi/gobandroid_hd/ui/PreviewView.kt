package org.ligi.gobandroid_hd.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.CellImpl
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoHelper

/**
 * A lighter Board-View to use as preview ( e.g. in game listings

 * @author [Marcus -LiGi- Bueschleb ](http://ligi.de)
 * *
 *
 *
 * *         This software is licenced with GPLv3
 */
class PreviewView : View {

    var game: GoGame? = null
        set(game) {
            field = game
            span = TsumegoHelper.calcSpanAsPoint(game!!)
            white_stone_bitmap = null
            requestLayout()
        }
    private var white_stone_bitmap: Bitmap? = null
    private var black_stone_bitmap: Bitmap? = null
    private var span: CellImpl? = null
    private var stone_size: Float = 0F
    private var black_line_paint: Paint? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        black_line_paint = Paint()
        black_line_paint!!.color = Color.BLACK
    }

    private fun getScaledRes(size: Float, resID: Int): Bitmap {
        val unscaled_bitmap = BitmapFactory.decodeResource(resources, resID)
        return Bitmap.createScaledBitmap(unscaled_bitmap, size.toInt(), size.toInt(), true)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        white_stone_bitmap = null
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (span != null) {
            val parentWidth = View.MeasureSpec.getSize(widthMeasureSpec)
            val ratio = (span!!.y + 1) / (span!!.x + 1).toFloat()
            this.setMeasuredDimension(parentWidth, (parentWidth * ratio).toInt())
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (span == null) {
            return
        }

        if (white_stone_bitmap == null) {
            stone_size = width.toFloat() / (span!!.x + 1)
            white_stone_bitmap = getScaledRes(stone_size, R.drawable.stone_white)
            black_stone_bitmap = getScaledRes(stone_size, R.drawable.stone_black)
        }

        for (x in 0..span!!.y) {
            canvas.drawLine(0.5f * stone_size, (0.5f + x) * stone_size,
                    (0.5f + span!!.x) * stone_size, (0.5f + x) * stone_size,
                    black_line_paint)
        }

        for (y in 0..span!!.x) {
            canvas.drawLine((0.5f + y) * stone_size, 0.5f * stone_size,
                    (0.5f + y) * stone_size, (.5f + span!!.y) * stone_size,
                    black_line_paint)
        }

        game!!.statelessGoBoard.withAllCells { cell ->
            if (cell.x <= span!!.x && cell.y <= span!!.y) {
                if (game!!.visualBoard.isCellBlack(cell))
                    canvas.drawBitmap(black_stone_bitmap, cell.x * stone_size, cell.y * stone_size, null)
                if (game!!.visualBoard.isCellWhite(cell))
                    canvas.drawBitmap(white_stone_bitmap, cell.x * stone_size, cell.y * stone_size, null)
            }
        }

    }

}
