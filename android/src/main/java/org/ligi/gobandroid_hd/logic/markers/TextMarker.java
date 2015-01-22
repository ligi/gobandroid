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

import org.ligi.gobandroid_hd.logic.Cell;

/**
 * class to mark a pos on the board useful for go problems - e.g. from SGF
 */
public class TextMarker extends GoMarker {

    private final String text;

    public TextMarker(Cell cell, String text) {
        super(cell);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }

        if (!(o instanceof TextMarker)) {
            return false;
        }

        final TextMarker otherMarker = (TextMarker) o;

        if (!isInCell(otherMarker)) {
            return false;
        }

        if (!getText().equals(otherMarker.getText())) {
            return false;
        }

        return true; // If we reached this place we can assume the Markers are the same
    }

    @Override
    public String toString() {
        return "TextMarker:" + super.toString() + text;
    }

    public void draw(Canvas c, float size, float x, float y, Paint paint) {
        final Paint.FontMetrics fm = paint.getFontMetrics();
        c.drawText(getText(), x, y + size + (fm.ascent + fm.descent), paint);
    }
}
