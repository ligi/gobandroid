package org.ligi.gobandroid_hd.logic.markers;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.ligi.gobandroid_hd.logic.Cell;

public class CircleMarker extends BaseShapeMarker {

    public CircleMarker(Cell cell) {
        super(cell);
    }

    public void draw(Canvas c, float size, float x, float y, Paint paint) {
        super.draw(c, size, x, y, paint);
        c.drawCircle(x, y, size / 4, localPaint);
    }
}
