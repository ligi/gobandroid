/**
 * gobandroid 
 * by Marcus -Ligi- Bueschleb 
 * http://ligi.de
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as 
 * published by the Free Software Foundation; 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. 
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 **/

package org.ligi.gobandroid_hd.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.BoardCell;
import org.ligi.gobandroid_hd.logic.Cell;
import org.ligi.gobandroid_hd.logic.GoBoard;
import org.ligi.gobandroid_hd.logic.GoDefinitions;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.markers.GoMarker;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Class to visually represent a Go Board in Android
 */
public class GoBoardViewHD extends View {

    private final static float SMALL_STONE_SCALE_FACTOR = 0.6f;
    private Cell zoom_poi = null;

    public boolean do_legend = true;
    public boolean do_actpos_highlight = true;
    public boolean do_actpos_highlight_ony_if_active = true;
    public boolean mark_last_stone = true;
    public boolean legend_sgf_mode = true; // GoPrefs.getLegendSGFMode()
    public boolean show_area_stones = false;

    // we need a lot of paints, but so we have a efficient onDraw with less
    // calls and the mem does not matter compared to bitmaps
    private Paint hoshi_paint, legendPaint, blackTextPaint, whiteTextPaint, gridPaint, gridPaint_h, bitmapPaint, placeStonePaint, opaque_paint, whiteLastStoneCirclePaint, blackLastStoneCirclePaint;

    private float stone_size;

    private Bitmap white_stone_bitmap = null;
    private Bitmap black_stone_bitmap = null;
    private Bitmap white_stone_bitmap_small = null;
    private Bitmap black_stone_bitmap_small = null;

    public boolean move_stone_mode = false;

    private boolean regenerate_stones_flag = true;

    protected boolean enforceSquare() {
        return true;
    }

    protected float zoom = 1.0f;

    public GoBoardViewHD(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public App getApp() {
        return (App) getContext().getApplicationContext();
    }

    public void init() {

        // these paint init's should be exposed to a designer ^^

        // paint to make the stones transparent in game-result mode ( terretory
        // stones ) - also used for stone move and shadow stone
        opaque_paint = new Paint();
        opaque_paint.setAlpha(0x77);

        // for marking the last stone
        whiteLastStoneCirclePaint = new Paint();
        whiteLastStoneCirclePaint.setColor(Color.WHITE);
        whiteLastStoneCirclePaint.setStyle(Paint.Style.STROKE);
        whiteLastStoneCirclePaint.setStrokeWidth(2.0f);
        whiteLastStoneCirclePaint.setAntiAlias(true);

        // make a black one
        blackLastStoneCirclePaint = new Paint(whiteLastStoneCirclePaint);
        blackLastStoneCirclePaint.setColor(Color.BLACK);

        // for the star or hoshi points
        hoshi_paint = new Paint();
        hoshi_paint.setColor(Color.BLACK);
        hoshi_paint.setStyle(Paint.Style.FILL);
        hoshi_paint.setAntiAlias(true);

        // for drawing on the stones
        whiteTextPaint = new Paint();
        whiteTextPaint.setColor(Color.WHITE);
        whiteTextPaint.setAntiAlias(true);
        whiteTextPaint.setTextAlign(Paint.Align.CENTER);
        whiteTextPaint.setShadowLayer(2, 1, 1, Color.BLACK);

        blackTextPaint = new Paint(whiteTextPaint);
        blackTextPaint.setColor(Color.BLACK);
        blackTextPaint.setShadowLayer(2, 1, 1, Color.WHITE);
        blackTextPaint.setTextAlign(Paint.Align.CENTER);

        gridPaint_h = new Paint();
        gridPaint_h.setColor(0xFF0000FF);
        gridPaint_h.setShadowLayer(1, 1, 1, 0xFFFFFFFF);

        gridPaint = new Paint();
        gridPaint.setColor(Color.BLACK);

        legendPaint = new Paint();
        legendPaint.setColor(Color.BLACK);
        legendPaint.setTextAlign(Paint.Align.CENTER);
        legendPaint.setTextSize(12.0f);
        legendPaint.setAntiAlias(true);

        bitmapPaint = new Paint();
        placeStonePaint = new Paint();
        placeStonePaint.setAlpha(127);

        // defaults
        setFocusable(true);
        setGridEmboss(true);
    }

    public GoGame getGame() {
        return App.getGame();
    }

    /**
     * set the zoom factor - 1.0 ( default ) means no zoom
     *
     * @param zoom - the new Zoom-factor
     */
    public void setZoom(float zoom) {
        this.zoom = zoom;
        regenerateStoneImagesWithNewSize();
    }

    public PointF getZoomTranslate() {
        if (zoom <= 1.0f) {
            return new PointF(0, 0);
        }

        final Cell act_zoom_point = calcActZoomPOI();

        if (act_zoom_point == null) {
            return new PointF(0, 0);
        }

        return new PointF(-stone_size * (act_zoom_point.x - getGame().getSize() / 2.0f / zoom), -stone_size * (act_zoom_point.y - getGame().getSize() / 2.0f / zoom));
    }

    private Cell calcActZoomPOI() {
        if (zoom_poi != null) {
            return zoom_poi;
        } else if (App.getInteractionScope().getTouchCell() != null) {
            return App.getInteractionScope().getTouchCell();
        }

        Log.w("zoom requested but no POI to center around");
        return null;
    }

    public void screenshot(String sshot_name) {
        final Bitmap bmp = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
        final Canvas c = new Canvas(bmp);
        draw2canvas(c);

        try {
            if (sshot_name.indexOf("://") > 0) {
                sshot_name = sshot_name.substring(sshot_name.indexOf("://") + 3);
            }
            new File(sshot_name.substring(0, sshot_name.lastIndexOf("/"))).mkdirs();
            new File(sshot_name).createNewFile();
            final FileOutputStream out = new FileOutputStream(sshot_name);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        draw2canvas(canvas);
    }

    private int getGameSize() {
        return getGame().getSize();
    }

    /**
     * used to make nice code around hoshi and last stone circle
     */
    private void drawBoardCircle(Canvas canvas, float x, float y, float size, Paint paint) {
        canvas.drawCircle(stone_size / 2.0f + x * stone_size, stone_size / 2.0f + y * stone_size, size, paint);
    }

    protected void draw2canvas(Canvas canvas) {
        canvas.save();

        // when we have zoomed in - center translate the canvas around the POI
        if (zoom > 1.0f) {
            canvas.translate(getZoomTranslate().x, getZoomTranslate().y);
        }

        if (regenerate_stones_flag) {
            regenerate_images();
        }

        boolean actpos_highlight_condition = false;

        if (!(do_actpos_highlight_ony_if_active && (!isFocused()))) {
            actpos_highlight_condition = do_actpos_highlight && App.getInteractionScope().hasValidTouchCoord();
        }

        // draw semi transparent stone on current touch pos as a shadow
        if ((!move_stone_mode) && actpos_highlight_condition) {
            final Cell touch_cell = App.getInteractionScope().getTouchCell();
            final Bitmap bitmap = (getGame().isBlackToMove()) ? black_stone_bitmap : white_stone_bitmap;
            canvas.drawBitmap(bitmap, touch_cell.x * stone_size, touch_cell.y * stone_size, placeStonePaint);
        }

        // draw the vertical lines for the grid
        final GoBoard board = getGame().getVisualBoard();
        for (byte x = 0; x < getGameSize(); x++)
            canvas.drawLine(stone_size / 2.0f + x * stone_size, stone_size / 2.0f, stone_size / 2.0f + x * stone_size, stone_size * (float) (board.getSize() - 1) + stone_size / 2.0f, (actpos_highlight_condition && (App.getInteractionScope().getTouchCell().x == x)) ? gridPaint_h : gridPaint);

        // draw the horizontal lines and the legend
        for (byte x = 0; x < board.getSize(); x++) {
            canvas.drawLine(stone_size / 2.0f, stone_size / 2.0f + x * stone_size, stone_size * (float) (board.getSize() - 1) + stone_size / 2.0f, stone_size / 2.0f + x * stone_size, (actpos_highlight_condition && (App.getInteractionScope().getTouchCell().x == x)) ? gridPaint_h : gridPaint);
            if (do_legend) {
                canvas.drawText("" + (getGameSize() - x), 6 + stone_size * (float) (getGameSize() - 1) + stone_size / 2.0f, stone_size / 2.0f + x * stone_size + gridPaint.getTextSize() / 3, legendPaint);

                if ((x > 7) && legend_sgf_mode)
                    canvas.drawText("" + (char) ('A' + (x + 1)), stone_size / 2.0f + x * stone_size, stone_size * (float) (getGameSize() - 1) + stone_size / 2.0f + 1 + gridPaint.getTextSize(), legendPaint);
                else
                    canvas.drawText("" + (char) ('A' + x), stone_size / 2.0f + x * stone_size, stone_size * (float) (getGameSize() - 1) + stone_size / 2.0f + 1 + gridPaint.getTextSize(), legendPaint);
            }
        }

        for (BoardCell cell : board.getAllCells()) {
            if (getGame().isCellHoschi(cell)) {
                drawBoardCircle(canvas, cell.x, cell.y, 2f + stone_size / 10, hoshi_paint);
            }

            // paint the territory with alpha opaque stones
            if (show_area_stones) {
                if (getGame().area_assign[cell.x][cell.y] == GoDefinitions.PLAYER_BLACK)
                    canvas.drawBitmap(black_stone_bitmap, cell.x * stone_size, cell.y * stone_size, opaque_paint);

                else if (getGame().area_assign[cell.x][cell.y] == GoDefinitions.PLAYER_WHITE)
                    canvas.drawBitmap(white_stone_bitmap, cell.x * stone_size, cell.y * stone_size, opaque_paint);

            }

            if (board.isCellDeadWhite(cell)) {
                canvas.drawBitmap(white_stone_bitmap_small, cell.x * stone_size + (stone_size - white_stone_bitmap_small.getWidth()) / 2, cell.y * stone_size + (stone_size - white_stone_bitmap_small.getHeight()) / 2, bitmapPaint);
            } else if (board.isCellDeadBlack(cell)) {
                canvas.drawBitmap(black_stone_bitmap_small, cell.x * stone_size + (stone_size - black_stone_bitmap_small.getWidth()) / 2, cell.y * stone_size + (stone_size - black_stone_bitmap_small.getHeight()) / 2, bitmapPaint);
            } else if (board.isCellWhite(cell)) {
                canvas.drawBitmap(white_stone_bitmap, cell.x * stone_size, cell.y * stone_size, getStonePaintForCell(cell));
            } else if (board.isCellBlack(cell)) {
                canvas.drawBitmap(black_stone_bitmap, cell.x * stone_size, cell.y * stone_size, getStonePaintForCell(cell));
            }

        }

        if (mark_last_stone) {
            final Cell lastMoveCell = getGame().getActMove().getCell();
            if (lastMoveCell != null) {
                final Paint paint = (board.isCellWhite(lastMoveCell)) ? blackLastStoneCirclePaint : whiteLastStoneCirclePaint;
                drawBoardCircle(canvas, lastMoveCell.x, lastMoveCell.y, 2f + stone_size / 4f, paint);
            }
        }

        // paint the markers
        for (GoMarker marker : getGame().getActMove().getMarkers()) {
            final Paint markerPaint = getTextPaintForCell(marker);
            final float x = marker.x * stone_size + stone_size / 2.0f;
            final float y = marker.y * stone_size + (stone_size) / 2.0f;
            marker.draw(canvas, stone_size, x, y, markerPaint);
        }

        canvas.restore();
    } // end of onDraw

    private Paint getStonePaintForCell(Cell cell) {
        if (move_stone_mode && cell.equals(getGame().getActMove().getCell())) {
            return opaque_paint;
        } else {
            return bitmapPaint;
        }
    }

    private Paint getTextPaintForCell(Cell cell) {
        if (getGame().getVisualBoard().isCellBlack(cell)) {
            return whiteTextPaint;
        } else {
            return blackTextPaint;
        }

    }

    private Bitmap getScaledRes(float size, int resID) {
        final Bitmap unscaled_bitmap = BitmapFactory.decodeResource(this.getResources(), resID);
        return Bitmap.createScaledBitmap(unscaled_bitmap, (int) size, (int) size, true);
    }

    /**
     * resize the images regarding to stone_size
     */
    public void regenerate_images() {

        white_stone_bitmap = getScaledRes(stone_size, R.drawable.stone_white);
        black_stone_bitmap = getScaledRes(stone_size, R.drawable.stone_black);
        white_stone_bitmap_small = getScaledRes(stone_size * SMALL_STONE_SCALE_FACTOR, R.drawable.stone_white);
        black_stone_bitmap_small = getScaledRes(stone_size * SMALL_STONE_SCALE_FACTOR, R.drawable.stone_black);

        regenerate_stones_flag = false;

        whiteTextPaint.setTextSize(stone_size);
        blackTextPaint.setTextSize(stone_size);

    }

    public void setGridEmboss(boolean grid_emboss) {
        gridPaint.setShadowLayer(1, 1, 1, grid_emboss ? 0xFFFFFFFF : 0xFF000000);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setSize(w, h);
    }

    private void setSize(int w, int h) {
        stone_size = zoom * (((w < h) ? w : h) / (float) getGame().getVisualBoard().getSize());
        regenerate_stones_flag = true;
    }

    public void regenerateStoneImagesWithNewSize() {
        setSize(getWidth(), getHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (enforceSquare()) {
            final int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
            final int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
            final int size = Math.min(parentWidth, parentHeight);
            setMeasuredDimension(size, size);
        }
    }

    public void setZoomPOI(Cell zoom_poi) {
        this.zoom_poi = zoom_poi;
        // TODO check use-cases if we need to invalidate here
    }

    public Cell pixel2cell(float pixelX, float pixelY) {
        final int cellX = (int) ((pixelX - getZoomTranslate().x) / stone_size);
        final int cellY = (int) ((pixelY - getZoomTranslate().y) / stone_size);
        final Cell cell = new Cell(cellX, cellY);

        if (getGame().getCalcBoard().isCellOnBoard(cell)) {
            return cell;
        }
        return null;
    }
}