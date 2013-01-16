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

import android.content.Intent;

public class IconicMenuItem {

    public int drawable;
    public String label = null;
    public int label_resId;
    public Intent intent = null;
    public int action = -1;

    public IconicMenuItem(String label, int drawable, Intent intent) {
        this.drawable = drawable;
        this.label = label;
        this.intent = intent;
    }

    public IconicMenuItem(String label, int drawable, int action) {
        this.drawable = drawable;
        this.label = label;
        this.action = action;
    }

    public IconicMenuItem(int label_resId, int drawable, Intent intent) {
        this.drawable = drawable;
        this.label_resId = label_resId;
        this.intent = intent;
    }

    public IconicMenuItem(int label_resId, int drawable, int action) {
        this.drawable = drawable;
        this.label_resId = label_resId;
        this.action = action;
    }

}