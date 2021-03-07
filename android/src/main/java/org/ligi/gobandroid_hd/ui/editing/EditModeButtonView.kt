package org.ligi.gobandroid_hd.ui.editing

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import android.view.View
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.markers.GoMarker
import org.ligi.gobandroid_hd.ui.editing.model.EditModeItem
import org.ligi.gobandroid_hd.ui.editing.model.IconEditModeItem
import org.ligi.gobandroid_hd.ui.editing.model.MarkerEditModeItem

internal class EditModeButtonView(context: Context, val item: EditModeItem, val isActive: Boolean) : View(context) {

    private var drawable: Drawable? = null
    private var marker: GoMarker? = null
    private val paint by lazy {
        val newPaint = Paint()
        newPaint.textAlign = Paint.Align.CENTER
        newPaint.isAntiAlias = true
        newPaint
    }

    init {
        contentDescription = context.getString(item.contentDescriptionResId)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = w / 7

        if (item is MarkerEditModeItem) {
            if (item.marker != null) {
                marker = item.marker
                paint.textSize = (w - 2 * padding).toFloat()
            }
        } else if (item is IconEditModeItem) {
            drawable = ContextCompat.getDrawable(context, item.iconResId!!)
            drawable?.setBounds(padding, padding, width - padding, height - padding)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (isActive) {
            canvas.drawColor(ContextCompat.getColor(context, R.color.dividing_color))
        }

        marker?.draw(canvas, width.toFloat(), width / 2f, height / 2f, paint)
        drawable?.draw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = context.resources.getDimensionPixelSize(R.dimen.edit_mode_item_size)
        val measureSpec = View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY)
        super.onMeasure(measureSpec, measureSpec)
    }
}
