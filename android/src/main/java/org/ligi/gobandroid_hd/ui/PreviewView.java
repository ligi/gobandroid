package org.ligi.gobandroid_hd.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.Cell;
import org.ligi.gobandroid_hd.logic.CellFactory;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoHelper;

/**
 * A lighter Board-View to use as preview ( e.g. in game listings
 *
 * @author <a href="http://ligi.de">Marcus -LiGi- Bueschleb </a>
 *         <p/>
 *         This software is licenced with GPLv3
 */
public class PreviewView extends View {

    private GoGame game;
    private Bitmap white_stone_bitmap, black_stone_bitmap;
    private Cell span;
    private int stone_size;
    private Paint black_line_paint;

    public PreviewView(Context context) {
        super(context);
        init();
    }

    public PreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        black_line_paint = new Paint();
        black_line_paint.setColor(Color.BLACK);
    }

    private Bitmap getScaledRes(float size, int resID) {
        final Bitmap unscaled_bitmap = BitmapFactory.decodeResource(getResources(), resID);
        return Bitmap.createScaledBitmap(unscaled_bitmap, (int) size, (int) size, true);
    }

    public GoGame getGame() {
        return game;
    }

    public void setGame(GoGame game) {
        this.game = game;
        span = TsumegoHelper.calcSpanAsPoint(game);
        white_stone_bitmap = null;
        requestLayout();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        white_stone_bitmap = null;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (span != null) {
            final int parentWidth = View.MeasureSpec.getSize(widthMeasureSpec);
            final float ratio = (span.y + 1) / (float) (span.x + 1);
            this.setMeasuredDimension((parentWidth), (int) (parentWidth * ratio));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (span == null) {
            return;
        }

        if (white_stone_bitmap == null) {
            stone_size = getWidth() / (span.x + 1);
            white_stone_bitmap = getScaledRes(stone_size, R.drawable.stone_white);
            black_stone_bitmap = getScaledRes(stone_size, R.drawable.stone_black);
        }

        for (int x = 0; x <= span.y; x++) {
            canvas.drawLine(0.5f * stone_size, (0.5f + x) * stone_size,
                    (0.5f + span.x) * stone_size, (0.5f + x) * stone_size,
                    black_line_paint);
        }

        for (int y = 0; y <= span.x; y++) {
            canvas.drawLine((0.5f + y) * stone_size, 0.5f * stone_size,
                    (0.5f + y) * stone_size, (.5f + span.y) * stone_size,
                    black_line_paint);
        }

        for (Cell cell : CellFactory.getAllCellsForRect(span.x, span.y)) {
            if (game.getVisualBoard().isCellBlack(cell))
                canvas.drawBitmap(black_stone_bitmap, cell.x * stone_size, cell.y
                        * stone_size, null);
            if (game.getVisualBoard().isCellWhite(cell))
                canvas.drawBitmap(white_stone_bitmap, cell.x * stone_size, cell.y
                        * stone_size, null);
        }

    }

}
