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

package org.ligi.gobandroid_hd.ui;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Class to visually represent a Go Board in Android
 */
public class ZoomingGoBoardViewHD extends GoBoardViewHD {

    @Override
    protected boolean enforceSquare() {
        return false;
    }

    public ZoomingGoBoardViewHD(Context context, AttributeSet attrs) {
        super(context, attrs);
        setZoom(3f);
        do_actpos_highlight_ony_if_active = false;
    }
}