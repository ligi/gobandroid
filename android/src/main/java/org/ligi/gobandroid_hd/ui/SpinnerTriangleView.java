package org.ligi.gobandroid_hd.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class SpinnerTriangleView extends View {
    private Paint p;
    private Path pth;

    public SpinnerTriangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        pth = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        pth.moveTo(w - this.getPaddingRight(), this.getPaddingTop());
        pth.lineTo(w - this.getPaddingRight(), h - this.getPaddingBottom());
        pth.lineTo(getPaddingLeft(), h - getPaddingBottom());
        pth.lineTo(w - this.getPaddingRight(), getPaddingTop());

        p.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(pth, p);
    }

}
