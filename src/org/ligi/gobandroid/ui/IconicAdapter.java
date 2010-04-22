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

package org.ligi.gobandroid.ui;

import org.ligi.gobandroid.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class IconicAdapter extends ArrayAdapter<Object> { 
        Activity context; 
 
        Object[] items;

        
        public IconicAdapter(Activity context,Object[] items) {
        	super(context, R .layout.icon_and_text, items);
        	this.items=items;
        	
            this.context=context; 
        } 
 
        public View getView(int position, View convertView, ViewGroup parent) { 
        	 LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	 
            //ViewInflate inflater=context.get .getViewInflate(); 
            View row=vi.inflate(R.layout.icon_and_text, null); 
            TextView label=(TextView)row.findViewById(R.id.TextView01); 
 
            label.setText(((IconicMenuItem)items[position]).label); 

            
            if ((items.length>position)&&(((IconicMenuItem)items[position]).drawable!=-1)) { 
                ImageView icon=(ImageView)row.findViewById(R.id.ImageView01); 
                icon.setImageResource(((IconicMenuItem)items[position]).drawable ); 
            }    
 
            return(row); 
        } 
    }

