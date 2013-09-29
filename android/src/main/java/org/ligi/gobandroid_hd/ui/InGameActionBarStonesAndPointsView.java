package org.ligi.gobandroid_hd.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.tracedroid.logging.Log;

public class InGameActionBarStonesAndPointsView extends View implements GoGame.GoGameChangeListener {

    private Bitmap white_stone_bitmap = null;
    private Bitmap black_stone_bitmap = null;
    private Paint mPaint = new Paint();
    private Rect black_bg_rect = new Rect();
    private Rect white_bg_rect = new Rect();
    private Paint myActiveBGPaint = new Paint();
    private FontMetrics fm;
    private float text_offset;
    private App app;

    private Bitmap getScaledRes(float size, int resID) {
        Bitmap unscaled_bitmap = BitmapFactory.decodeResource(this.getResources(), resID);
        return Bitmap.createScaledBitmap(unscaled_bitmap, (int) size, (int) size, true);
    }

    public InGameActionBarStonesAndPointsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InGameActionBarStonesAndPointsView(Context context) {

        super(context);

        app = (App) context.getApplicationContext();

        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPaint.setTextSize((int) (h / 2.5));
        mPaint.setAntiAlias(true);

        black_stone_bitmap = getScaledRes(h / 2, R.drawable.stone_black);
        white_stone_bitmap = getScaledRes(h / 2, R.drawable.stone_white);
        black_bg_rect = new Rect(0, 0, black_stone_bitmap.getWidth() * 3, black_stone_bitmap.getHeight());
        white_bg_rect = new Rect(black_bg_rect);
        white_bg_rect.offsetTo(0, black_stone_bitmap.getHeight());
        fm = mPaint.getFontMetrics();
        text_offset = (black_stone_bitmap.getHeight() - mPaint.getTextSize()) / 2 - (fm.top + fm.bottom);
    }

    public void init() {
        mPaint.setColor(Color.BLACK);
        getGame().addGoGameChangeListener(this);
        myActiveBGPaint.setColor(getResources().getColor(R.color.dividing_color));
        myActiveBGPaint.setStyle(Paint.Style.FILL);
    }

    private GoGame getGame() {
        return app.getGame();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (this.getWidth() > white_bg_rect.width() * 2) {
            String move_text = getContext().getString(R.string.move) + " " + getGame().getActMove().getMovePos();

            int mode_str = R.string.empty_str;
            switch (app.getInteractionScope().getMode()) {
                case InteractionScope.MODE_TSUMEGO:
                    mode_str = R.string.tsumego;
                    break;
                case InteractionScope.MODE_REVIEW:
                    mode_str = R.string.review;
                    break;
                case InteractionScope.MODE_RECORD:
                    mode_str = R.string.record;
                    break;
                case InteractionScope.MODE_TELEVIZE:
                    mode_str = R.string.go_tv;
                    break;

                case InteractionScope.MODE_COUNT:
                    mode_str = R.string.count;
                    break;

                case InteractionScope.MODE_GNUGO:
                    mode_str = R.string.gnugo;
                    break;

            }

            canvas.drawText(move_text, white_bg_rect.width() + 5, text_offset, mPaint);

            canvas.drawText(getContext().getString(mode_str), white_bg_rect.width() + 5, getHeight() / 2 + text_offset, mPaint);
        }

        if (getGame().isBlackToMove() || getGame().isFinished())
            canvas.drawRect(black_bg_rect, myActiveBGPaint);

        if ((!getGame().isBlackToMove()) || getGame().isFinished())
            canvas.drawRect(white_bg_rect, myActiveBGPaint);

        canvas.drawBitmap(black_stone_bitmap, black_stone_bitmap.getWidth() / 2, 0, null);
        canvas.drawBitmap(white_stone_bitmap, black_stone_bitmap.getWidth() / 2, getHeight() / 2, null);

        canvas.drawText(" " + getGame().getCapturesBlack(), (int) (black_stone_bitmap.getWidth() * 1.5), text_offset, mPaint);
        canvas.drawText(" " + getGame().getCapturesWhite(), (int) (black_stone_bitmap.getWidth() * 1.5), getHeight() / 2 + text_offset, mPaint);
        super.onDraw(canvas);
    }

    @Override
    public void onGoGameChange() {
        Log.i("game changed");
        this.postInvalidate();
    }
}
