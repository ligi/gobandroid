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

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.ligi.gobandroid_hd.R;

/**
 * Adapter with a label and a icon - used in some ListViews
 *
 * @author <a href="http://ligi.de">Marcus -LiGi- Bueschleb </a>
 *         <p/>
 *         This software is licenced with GPLv3
 */
public class IconicAdapter extends ArrayAdapter<Object> {

    private Activity context;
    private Object[] items;

    public IconicAdapter(Activity context, Object[] items) {
        super(context, R.layout.icon_and_text, items);
        this.items = items;
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = vi.inflate(R.layout.icon_and_text, null);
        TextView label = (TextView) row.findViewById(R.id.TextView01);

        IconicMenuItem item = (IconicMenuItem) items[position];

        if (item.label != null)
            label.setText(item.label);
        else
            label.setText(item.label_resId);

        if ((items.length > position) && (item.drawable != -1)) {
            ImageView icon = (ImageView) row.findViewById(R.id.ImageView01);
            icon.setImageResource(item.drawable);
        }

        return (row);
    }
}
