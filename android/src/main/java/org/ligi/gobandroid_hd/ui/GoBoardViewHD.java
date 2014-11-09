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
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import org.ligi.axt.views.SquareView;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
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
public class GoBoardViewHD extends SquareView {

    private int zoom_poi = -1;

    // public boolean grid_embos=true; // GoPrefs.getGridEmbossEnabled()
    public boolean do_legend = true;
    public boolean do_actpos_highlight = true;
    public boolean do_actpos_highlight_ony_if_active = true;
    // public boolean do_mark_act=true;
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

    protected float zoom = 1.0f;

    /*
        public GoBoardViewHD(Context context, boolean square, float zoom) {
            super(context);
            this.zoom = zoom;
            enforce_square = square;
            init();
        }
    */
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
        return getApp().getGame();
    }

    public void prepare_keyinput() {
        if (getApp().getInteractionScope().getTouchPosition() < 0)
            getApp().getInteractionScope().setTouchPosition(0);
    }

    /**
     * set the zoom factor - 1.0 ( default ) means no zoom
     *
     * @param zoom
     */
    public void setZoom(float zoom) {
        this.zoom = zoom;
        setSize(this.getWidth(), this.getHeight());
    }

    public PointF getZoomTranslate() {
        if (zoom <= 1.0f)
            return new PointF(0, 0);

        int act_zoom_poi = 0;

        if (zoom_poi >= 0) {
            act_zoom_poi = zoom_poi;
        } else if (getApp().getInteractionScope().getTouchPosition() >= 0) {
            act_zoom_poi = getApp().getInteractionScope().getTouchPosition();
        } else
            Log.w("zoom requested but no POI to center around");

        Point act_zoom_point = getGame().linear_coordinate2Point(act_zoom_poi);
        PointF res = new PointF(-stone_size * (act_zoom_point.x - getGame().getSize() / 2.0f / zoom), -stone_size * (act_zoom_point.y - getGame().getSize() / 2.0f / zoom));

        return res;
    }

    public void screenshot(String sshot_name) {
        Bitmap bmp = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        draw2canvas(c);

        try {
            if (sshot_name.indexOf("://") > 0)
                sshot_name = sshot_name.substring(sshot_name.indexOf("://") + 3);
            Log.i("writing screenshot " + sshot_name);
            new File(sshot_name.substring(0, sshot_name.lastIndexOf("/"))).mkdirs();
            new File(sshot_name).createNewFile();
            FileOutputStream out = new FileOutputStream(sshot_name);
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
        Log.i("draw");
        canvas.save();

        // when we have zoomed in - center translate the canvas around the POI
        if (zoom > 1.0f) {
            canvas.translate(getZoomTranslate().x, getZoomTranslate().y);
        }

        if (regenerate_stones_flag)
            regenerate_images();

        boolean actpos_highlight_condition = false;

        if (!(do_actpos_highlight_ony_if_active && (!isFocused()))) {
            actpos_highlight_condition = do_actpos_highlight && getApp().getInteractionScope().hasValidTouchCoord();
        }

        // draw semi transparent stone on current touch pos as a shadow
        if ((!move_stone_mode) && actpos_highlight_condition) {
            canvas.drawBitmap(((getGame().isBlackToMove()) ? black_stone_bitmap : white_stone_bitmap), getApp().getInteractionScope().getTouchX() * stone_size, getApp().getInteractionScope().getTouchY() * stone_size, placeStonePaint);
        }

        // draw the vertical lines for the grid
        for (byte x = 0; x < getGameSize(); x++)
            canvas.drawLine(stone_size / 2.0f + x * stone_size, stone_size / 2.0f, stone_size / 2.0f + x * stone_size, stone_size * (float) (getGame().getVisualBoard().getSize() - 1) + stone_size / 2.0f, (actpos_highlight_condition && (getApp()
                    .getInteractionScope().getTouchX() == x)) ? gridPaint_h : gridPaint);

        // draw the horizontal lines and the legend
        for (byte x = 0; x < getGame().getVisualBoard().getSize(); x++) {
            canvas.drawLine(stone_size / 2.0f, stone_size / 2.0f + x * stone_size, stone_size * (float) (getGame().getVisualBoard().getSize() - 1) + stone_size / 2.0f, stone_size / 2.0f + x * stone_size, (actpos_highlight_condition && (getApp()
                    .getInteractionScope().getTouchY() == x)) ? gridPaint_h : gridPaint);
            if (do_legend) {
                canvas.drawText("" + (getGameSize() - x), 6 + stone_size * (float) (getGameSize() - 1) + stone_size / 2.0f, stone_size / 2.0f + x * stone_size + gridPaint.getTextSize() / 3, legendPaint);

                if ((x > 7) && legend_sgf_mode)
                    canvas.drawText("" + (char) ('A' + (x + 1)), stone_size / 2.0f + x * stone_size, stone_size * (float) (getGameSize() - 1) + stone_size / 2.0f + 1 + gridPaint.getTextSize(), legendPaint);
                else
                    canvas.drawText("" + (char) ('A' + x), stone_size / 2.0f + x * stone_size, stone_size * (float) (getGameSize() - 1) + stone_size / 2.0f + 1 + gridPaint.getTextSize(), legendPaint);
            }
        }

        for (byte x = 0; x < getGameSize(); x++)
            for (byte y = 0; y < getGameSize(); y++) {
                if (getGame().isPosHoschi(x, y))
                    drawBoardCircle(canvas, x, y, 2f + stone_size / 10, hoshi_paint);

                // paint the territory with alpha opaque stones
                if (show_area_stones) {
                    if (getGame().area_assign[x][y] == GoDefinitions.PLAYER_BLACK)
                        canvas.drawBitmap(black_stone_bitmap, x * stone_size, y * stone_size, opaque_paint);

                    if (getGame().area_assign[x][y] == GoDefinitions.PLAYER_WHITE)
                        canvas.drawBitmap(white_stone_bitmap, x * stone_size, y * stone_size, opaque_paint);

                }

                if (getGame().getCalcBoard().isCellDead(x, y)) {
                    if (getGame().getVisualBoard().isCellWhite(x, y))
                        canvas.drawBitmap(white_stone_bitmap_small, x * stone_size + (stone_size - white_stone_bitmap_small.getWidth()) / 2, y * stone_size + (stone_size - white_stone_bitmap_small.getHeight()) / 2, bitmapPaint);

                    if (getGame().getVisualBoard().isCellBlack(x, y))
                        canvas.drawBitmap(black_stone_bitmap_small, x * stone_size + (stone_size - black_stone_bitmap_small.getWidth()) / 2, y * stone_size + (stone_size - black_stone_bitmap_small.getHeight()) / 2, bitmapPaint);

                } else {

                    boolean should_draw_opaque = (move_stone_mode && (x == getGame().getActMove().getX()) && (y == getGame().getActMove().getY()));

                    if (getGame().getVisualBoard().isCellWhite(x, y))
                        canvas.drawBitmap(white_stone_bitmap, x * stone_size, y * stone_size, should_draw_opaque ? opaque_paint : bitmapPaint);
                    if (getGame().getVisualBoard().isCellBlack(x, y))
                        canvas.drawBitmap(black_stone_bitmap, x * stone_size, y * stone_size, should_draw_opaque ? opaque_paint : bitmapPaint);

                    if (mark_last_stone) { // if the last stone should be marked
                        /** mark the last move */
                        if ((getGame().getActMove().getX() == x) && (getGame().getActMove().getY() == y)) {
                            if (getGame().getVisualBoard().isCellWhite(x, y))
                                drawBoardCircle(canvas, x, y, 2f + stone_size / 4f, blackLastStoneCirclePaint);
                            else if (getGame().getVisualBoard().isCellBlack(x, y))
                                drawBoardCircle(canvas, x, y, 2f + stone_size / 4f, whiteLastStoneCirclePaint);
                        }
                    }
                }

            }

        final FontMetrics fm = whiteTextPaint.getFontMetrics();

        // paint the markers
        for (GoMarker marker : getGame().getActMove().getMarkers()) {
            final Paint markerPaint = getTextPaintForCell(getGame().getVisualBoard(), marker.getX(), marker.getY());
            final float x = marker.getX() * stone_size + stone_size / 2.0f;
            final float y = marker.getY() * stone_size + (stone_size) / 2.0f;
            //canvas.drawText(marker.getText(), x, y, markerPaint);


            marker.draw(canvas, stone_size, x, y, markerPaint);
        }

        canvas.restore();
    } // end of onDraw

    private Paint getTextPaintForCell(GoBoard board, int x, int y) {
        if (getGame().getVisualBoard().isCellBlack(x, y)) {
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

        Log.i("regenerating images to stone size " + stone_size);
        float SMALL_STONE_SCALER = 0.6f;
        white_stone_bitmap = getScaledRes(stone_size, R.drawable.stone_white);
        black_stone_bitmap = getScaledRes(stone_size, R.drawable.stone_black);
        white_stone_bitmap_small = getScaledRes(stone_size * SMALL_STONE_SCALER, R.drawable.stone_white);
        black_stone_bitmap_small = getScaledRes(stone_size * SMALL_STONE_SCALER, R.drawable.stone_black);

        regenerate_stones_flag = false;

        whiteTextPaint.setTextSize(stone_size);
        blackTextPaint.setTextSize(stone_size);

    }

    public void setGridEmboss(boolean grid_emboss) {
        if (grid_emboss) {
            gridPaint.setShadowLayer(1, 1, 1, 0xFFFFFFFF);
        } else {
            gridPaint.setShadowLayer(1, 1, 1, 0xFF000000);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setSize(w, h);
    }

    private void setSize(int w, int h) {
        stone_size = zoom * (((w < h) ? w : h) / (float) getGame().getVisualBoard().getSize());
        regenerate_stones_flag = true;
    }

    public void boardSizeChanged() {
        setSize(this.getWidth(), this.getHeight());
    }

    public void regenerateStoneImagesWithNewSize() {
        setSize(getWidth(), getHeight());
    }

    public void setZoomPOI(int zoom_poi) {
        this.zoom_poi = zoom_poi;
        // TODO check use-cases if we need to invalidate here
    }

    public int pixel2boardPos(float x, float y) {
        final int board_x = (int)((x - getZoomTranslate().x) / stone_size);
        final int board_y = (int)((y - getZoomTranslate().y) / stone_size);

        if (board_x>=getGame().getSize() || board_y>=getGame().getSize()) {
            return -1;
        }

        return board_x + board_y * getGame().getSize();
    }
}