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

package org.ligi.gobandroid_hd.logic.markers;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * class to mark a pos on the board useful for go problems - e.g. from SGF
 */
public class GoMarker {

    private final byte x, y;
    private final String text;

    public GoMarker(byte x, byte y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public byte getX() {
        return x;
    }

    public byte getY() {
        return y;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GoMarker)) {
            return false;
        }

        GoMarker otherMarker = (GoMarker) o;

        if (!getText().equals(otherMarker.getText())) {
            return false;
        }

        if (getX() != otherMarker.getX()) {
            return false;
        }

        if (getY() != otherMarker.getY()) {
            return false;
        }

        return true; // If we reached this place we can assume the Markers are the same
    }

    public void draw(Canvas c, float size, float x, float y, Paint paint) {
        final Paint.FontMetrics fm = paint.getFontMetrics();
        c.drawText(getText(), x, y + size + (fm.ascent + fm.descent), paint);
    }
}
