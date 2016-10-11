/**
 * gobandroid
 * by Marcus -Ligi- Bueschleb
 * http://ligi.de
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see //www.gnu.org/licenses/>.
 */

package org.ligi.gobandroid_hd.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.InteractionScope
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.Cell
import org.ligi.gobandroid_hd.logic.CellImpl
import org.ligi.gobandroid_hd.logic.GoDefinitions
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.model.GameProvider
import org.ligi.tracedroid.logging.Log
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * Class to visually represent a Go Board in Android
 */
open class GoBoardView : View {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context) : super(context)

    private val SMALL_STONE_SCALE_FACTOR = 0.6f

    @Inject
    lateinit var gameProvider: GameProvider

    @Inject
    lateinit var interactionScope: InteractionScope

    private var zoom_poi: Cell? = null

    var do_legend = true
    var do_actpos_highlight = true
    var do_actpos_highlight_ony_if_active = true
    var mark_last_stone = true
    var legend_sgf_mode = true // GoPrefs.getLegendSGFMode()

    // we need a lot of paints, but so we have a efficient onDraw with less
    // calls and the mem does not matter compared to bitmaps
    private lateinit var hoshi_paint: Paint
    private lateinit var legendPaint: Paint
    private lateinit var blackTextPaint: Paint
    private lateinit var whiteTextPaint: Paint
    private lateinit var gridPaint: Paint
    private lateinit var gridPaint_h: Paint
    private lateinit var bitmapPaint: Paint
    private lateinit var placeStonePaint: Paint
    private lateinit var opaque_paint: Paint
    private lateinit var whiteLastStoneCirclePaint: Paint
    private lateinit var blackLastStoneCirclePaint: Paint

    private var stone_size: Float = 0.toFloat()

    private var white_stone_bitmap: Bitmap? = null
    private var black_stone_bitmap: Bitmap? = null
    private var white_stone_bitmap_small: Bitmap? = null
    private var black_stone_bitmap_small: Bitmap? = null

    var move_stone_mode = false

    private var regenerate_stones_flag = true

    protected open fun enforceSquare(): Boolean {
        return true
    }

    var zoom = 1.0f
        /**
         * set the zoom factor - 1.0 ( default ) means no zoom
         */
        set(value) {
            field = value
            regenerateStoneImagesWithNewSize()
        }


    init {
        App.component().inject(this)
        init()
    }

    fun init() {

        // these paint init's should be exposed to a designer ^^

        // paint to make the stones transparent in game-result mode ( territory
        // stones ) - also used for stone move and shadow stone
        opaque_paint = Paint()
        opaque_paint.alpha = 0x77

        // for marking the last stone
        whiteLastStoneCirclePaint = Paint()
        whiteLastStoneCirclePaint.color = Color.WHITE
        whiteLastStoneCirclePaint.style = Paint.Style.STROKE
        whiteLastStoneCirclePaint.strokeWidth = 2.0f
        whiteLastStoneCirclePaint.isAntiAlias = true

        // make a black one
        blackLastStoneCirclePaint = Paint(whiteLastStoneCirclePaint)
        blackLastStoneCirclePaint.color = Color.BLACK

        // for the star or hoshi points
        hoshi_paint = Paint()
        hoshi_paint.color = Color.BLACK
        hoshi_paint.style = Paint.Style.FILL
        hoshi_paint.isAntiAlias = true

        // for drawing on the stones
        whiteTextPaint = Paint()
        whiteTextPaint.color = Color.WHITE
        whiteTextPaint.isAntiAlias = true
        whiteTextPaint.textAlign = Paint.Align.CENTER
        whiteTextPaint.setShadowLayer(2f, 1f, 1f, Color.BLACK)

        blackTextPaint = Paint(whiteTextPaint)
        blackTextPaint.color = Color.BLACK
        blackTextPaint.setShadowLayer(2f, 1f, 1f, Color.WHITE)
        blackTextPaint.textAlign = Paint.Align.CENTER

        gridPaint_h = Paint()
        gridPaint_h.color = 0xFF0000FF.toInt()
        gridPaint_h.setShadowLayer(1f, 1f, 1f, 0xFFFFFFFF.toInt())

        gridPaint = Paint()
        gridPaint.color = Color.BLACK

        legendPaint = Paint()
        legendPaint.color = Color.BLACK
        legendPaint.textAlign = Paint.Align.CENTER
        legendPaint.textSize = 12.0f
        legendPaint.isAntiAlias = true

        bitmapPaint = Paint()
        placeStonePaint = Paint()
        placeStonePaint.alpha = 127

        // defaults
        isFocusable = true
        setGridEmboss(true)
    }

    val game: GoGame
        get() = gameProvider.get()


    val zoomTranslate: PointF
        get() {
            if (zoom <= 1.0f) {
                return PointF(0f, 0f)
            }

            val act_zoom_point = calcActZoomPOI() ?: return PointF(0f, 0f)

            return PointF(-stone_size * (act_zoom_point.x - game.size.toFloat() / 2.0f / zoom),
                    -stone_size * (act_zoom_point.y - game.size.toFloat() / 2.0f / zoom))
        }

    private fun calcActZoomPOI(): Cell? {
        return if (zoom_poi != null) {
            zoom_poi
        } else if (interactionScope.touchCell != null) {
            interactionScope.touchCell
        } else {
            Log.w("zoom requested but no POI to center around")
            return null
        }
    }

    /**
     * redraw board when draw_board is ture
     */
    fun screenshot(file: File, bitmap: Bitmap) {

        draw2canvas(Canvas(bitmap))

        try {
            file.createNewFile()
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onDraw(canvas: Canvas) {
        draw2canvas(canvas)
    }

    private val gameSize: Int
        get() = game.size

    /**
     * used to make nice code around hoshi and last stone circle
     */
    private fun drawBoardCircle(canvas: Canvas, x: Float, y: Float, size: Float, paint: Paint) {
        canvas.drawCircle(stone_size / 2.0f + x * stone_size, stone_size / 2.0f + y * stone_size, size, paint)
    }

    protected fun draw2canvas(canvas: Canvas) {
        canvas.save()

        // when we have zoomed in - center translate the canvas around the POI
        if (zoom > 1.0f) {
            canvas.translate(zoomTranslate.x, zoomTranslate.y)
        }

        if (regenerate_stones_flag) {
            regenerate_images()
        }

        var actpos_highlight_condition = false

        if (!(do_actpos_highlight_ony_if_active && !isFocused)) {
            actpos_highlight_condition = do_actpos_highlight && interactionScope.hasTouchCell()
        }

        // draw semi transparent stone on current touch pos as a shadow
        if (!move_stone_mode && actpos_highlight_condition) {
            val touch_cell = interactionScope.touchCell
            if (touch_cell != null) {
                val bitmap = if (game.isBlackToMove) black_stone_bitmap else white_stone_bitmap
                canvas.drawBitmap(bitmap, touch_cell.x * stone_size, touch_cell.y * stone_size, placeStonePaint)
            }
        }

        // draw the vertical lines for the grid
        val board = game.visualBoard
        for (x in 0..gameSize - 1)
            canvas.drawLine(stone_size / 2.0f + x * stone_size,
                    stone_size / 2.0f,
                    stone_size / 2.0f + x * stone_size,
                    stone_size * (board.size - 1).toFloat() + stone_size / 2.0f,
                    if (actpos_highlight_condition && interactionScope.touchCell!!.x == x.toInt()) gridPaint_h else gridPaint)

        // draw the horizontal lines and the legend
        for (x in 0..board.size - 1) {
            canvas.drawLine(stone_size / 2.0f,
                    stone_size / 2.0f + x * stone_size,
                    stone_size * (board.size - 1).toFloat() + stone_size / 2.0f,
                    stone_size / 2.0f + x * stone_size,
                    if (actpos_highlight_condition && interactionScope.touchCell!!.x == x.toInt()) gridPaint_h else gridPaint)
            if (do_legend) {
                canvas.drawText("" + (gameSize - x), legendPaint.textSize / 2f + stone_size * (gameSize - 1).toFloat() + stone_size / 2.0f,
                        stone_size / 2.0f + x * stone_size + gridPaint.textSize / 3,
                        legendPaint)

                canvas.drawText(getLegendLetter(x),
                        stone_size / 2.0f + x * stone_size,
                        stone_size * (gameSize - 1).toFloat() + stone_size / 2.0f + 1f + legendPaint.textSize,
                        legendPaint)
            }
        }


        board.statelessGoBoard.withAllCells { cell ->
            if (game.isCellHoschi(cell)) {
                drawBoardCircle(canvas, cell.x.toFloat(), cell.y.toFloat(), 2f + stone_size / 10, hoshi_paint)
            }

            // paint the territory with alpha opaque stones
            if (game.scorer != null) {
                if (game.scorer!!.area_assign[cell.x][cell.y] == GoDefinitions.PLAYER_BLACK)
                    canvas.drawBitmap(black_stone_bitmap, cell.x * stone_size, cell.y * stone_size, opaque_paint)
                else if (game.scorer!!.area_assign[cell.x][cell.y] == GoDefinitions.PLAYER_WHITE)
                    canvas.drawBitmap(white_stone_bitmap, cell.x * stone_size, cell.y * stone_size, opaque_paint)

            }

            if (board.isCellDeadWhite(cell)) {
                canvas.drawBitmap(white_stone_bitmap_small,
                        cell.x * stone_size + (stone_size - white_stone_bitmap_small!!.width) / 2,
                        cell.y * stone_size + (stone_size - white_stone_bitmap_small!!.height) / 2,
                        bitmapPaint)
            } else if (board.isCellDeadBlack(cell)) {
                canvas.drawBitmap(black_stone_bitmap_small,
                        cell.x * stone_size + (stone_size - black_stone_bitmap_small!!.width) / 2,
                        cell.y * stone_size + (stone_size - black_stone_bitmap_small!!.height) / 2,
                        bitmapPaint)
            } else if (board.isCellWhite(cell)) {
                canvas.drawBitmap(white_stone_bitmap, cell.x * stone_size, cell.y * stone_size, getStonePaintForCell(cell))
            } else if (board.isCellBlack(cell)) {
                canvas.drawBitmap(black_stone_bitmap, cell.x * stone_size, cell.y * stone_size, getStonePaintForCell(cell))
            }

        }

        if (mark_last_stone) {
            val lastMoveCell = game.actMove.cell
            if (lastMoveCell != null) {
                val paint = if (board.isCellWhite(lastMoveCell)) blackLastStoneCirclePaint else whiteLastStoneCirclePaint
                drawBoardCircle(canvas, lastMoveCell.x.toFloat(), lastMoveCell.y.toFloat(), 2f + stone_size / 4f, paint)
            }
        }

        // paint the markers
        for (marker in game.actMove.markers) {
            val markerPaint = getTextPaintForCell(marker)
            val x = marker.x * stone_size + stone_size / 2.0f
            val y = marker.y * stone_size + stone_size / 2.0f
            marker.draw(canvas, stone_size, x, y, markerPaint)
        }

        canvas.restore()
    } // end of onDraw

    private fun getLegendLetter(x: Int): String {
        if (x > 7 && legend_sgf_mode) {
            return ('A' + (x + 1)).toChar().toString()
        }
        return ('A' + x).toChar().toString()
    }

    private fun getStonePaintForCell(cell: Cell): Paint {
        if (move_stone_mode && cell == game.actMove.cell) {
            return opaque_paint
        } else {
            return bitmapPaint
        }
    }

    private fun getTextPaintForCell(cell: Cell): Paint {
        if (game.visualBoard.isCellBlack(cell)) {
            return whiteTextPaint
        } else {
            return blackTextPaint
        }

    }

    private fun getScaledRes(size: Float, resID: Int): Bitmap {
        val unscaled_bitmap = BitmapFactory.decodeResource(this.resources, resID)
        return Bitmap.createScaledBitmap(unscaled_bitmap, size.toInt(), size.toInt(), true)
    }

    /**
     * resize the images regarding to stone_size
     */
    fun regenerate_images() {

        white_stone_bitmap = getScaledRes(stone_size, R.drawable.stone_white)
        black_stone_bitmap = getScaledRes(stone_size, R.drawable.stone_black)
        white_stone_bitmap_small = getScaledRes(stone_size * SMALL_STONE_SCALE_FACTOR, R.drawable.stone_white)
        black_stone_bitmap_small = getScaledRes(stone_size * SMALL_STONE_SCALE_FACTOR, R.drawable.stone_black)

        regenerate_stones_flag = false

        whiteTextPaint.textSize = stone_size
        blackTextPaint.textSize = stone_size

    }

    fun setGridEmboss(grid_emboss: Boolean) {
        gridPaint.setShadowLayer(1f, 1f, 1f, if (grid_emboss) 0xFFFFFFFF.toInt() else 0xFF000000.toInt())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        setSize(w, h)
    }

    fun setSize(w: Int, h: Int) {

        stone_size = zoom * (Math.min(w, h) / game.visualBoard.size.toFloat())
        legendPaint.textSize = stone_size / 4

        regenerate_stones_flag = true
    }

    fun regenerateStoneImagesWithNewSize() {
        setSize(width, height)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (enforceSquare()) {
            val parentWidth = View.MeasureSpec.getSize(widthMeasureSpec)
            val parentHeight = View.MeasureSpec.getSize(heightMeasureSpec)
            val size = Math.min(parentWidth, parentHeight)
            setMeasuredDimension(size, size)
        }
    }

    fun setZoomPOI(zoom_poi: Cell) {
        this.zoom_poi = zoom_poi
        // TODO check use-cases if we need to invalidate here
    }

    fun pixel2cell(pixelX: Float, pixelY: Float): Cell? {
        val cellX = ((pixelX - zoomTranslate.x) / stone_size).toInt()
        val cellY = ((pixelY - zoomTranslate.y) / stone_size).toInt()
        val cell = CellImpl(cellX, cellY)

        if (game.calcBoard.isCellOnBoard(cell)) {
            return cell
        }
        return null
    }

}