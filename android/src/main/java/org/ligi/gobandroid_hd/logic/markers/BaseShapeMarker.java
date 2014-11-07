package org.ligi.gobandroid_hd.logic.markers;

import android.graphics.Canvas;
import android.graphics.Paint;

public class BaseShapeMarker extends GoMarker {

    Paint localPaint;

    public BaseShapeMarker(byte x, byte y) {
        super(x, y, "\u25cb"); // initialize with unicode for triangle
    }

    public void draw(Canvas c, float size, float x, float y, Paint paint) {
        if (localPaint == null) {
            localPaint = new Paint(paint);
            localPaint.setStyle(Paint.Style.STROKE);
            localPaint.setStrokeWidth(3);
            localPaint.setAntiAlias(true);
        }
    }
}
