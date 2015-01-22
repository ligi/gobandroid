package org.ligi.gobandroid_hd.logic.markers;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.ligi.gobandroid_hd.logic.Cell;

public class TriangleMarker extends BaseShapeMarker {

    public TriangleMarker(Cell cell) {
        super(cell);
    }

    @Override
    public void draw(Canvas c, float size, float x, float y, Paint paint) {
        super.draw(c, size, x, y, paint);
        c.drawLine(x, y - size / 3, x + size / 4, y + size / 4, localPaint);
        c.drawLine(x, y - size / 3, x - size / 4, y + size / 4, localPaint);
        c.drawLine(x - size / 4, y + size / 4, x + size / 4, y + size / 4, localPaint);
    }
}
