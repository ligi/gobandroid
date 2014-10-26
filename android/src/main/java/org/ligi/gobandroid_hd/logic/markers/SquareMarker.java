package org.ligi.gobandroid_hd.logic.markers;

import android.graphics.Canvas;
import android.graphics.Paint;

public class SquareMarker extends BaseShapeMarker {

    public SquareMarker(byte x, byte y) {
        super(x, y);
    }

    @Override
    public void draw(Canvas c, float size, float x, float y, Paint paint) {
        super.draw(c, size, x, y, paint);
        c.drawRect(x - size / 4, y - size / 4, x + size / 4, y + size / 4, localPaint);
    }
}
