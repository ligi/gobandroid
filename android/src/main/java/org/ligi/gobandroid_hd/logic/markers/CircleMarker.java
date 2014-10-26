package org.ligi.gobandroid_hd.logic.markers;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CircleMarker extends BaseShapeMarker {

    public CircleMarker(byte x, byte y) {
        super(x, y);
    }

    public void draw(Canvas c, float size, float x, float y, Paint paint) {
        super.draw(c, size, x, y, paint);
        c.drawCircle(x, y, size / 4, localPaint);
    }
}
