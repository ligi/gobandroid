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

import java.util.Vector;

import org.ligi.gobandroid.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * This is the main Activity of gobandroid which shows an menu 
 * with the stuff you can do here 
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         
**/

public class gobandroid extends ListActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        GoPrefs.init(this);
        
        Vector<IconicMenuItem> menu_items_vector = new Vector<IconicMenuItem>();

		menu_items_vector.add(new IconicMenuItem(R.string.start_game,
				android.R.drawable.ic_menu_agenda, new Intent(this,
						GoSetupActivity.class)));
		
		menu_items_vector.add(new IconicMenuItem(R.string.load_game,
				android.R.drawable.ic_menu_save, new Intent(this,
						LoadActionsActivity.class)));
		
		menu_items_vector.add(new IconicMenuItem(R.string.settings,
				android.R.drawable.ic_menu_preferences, new Intent(this,
						GoPrefsActivity.class)));
		
		menu_items_vector.add(new IconicMenuItem(R.string.info,
				android.R.drawable.ic_menu_info_details, new Intent(this,
						AboutActivity.class)));
		
		menu_items_vector.add(new IconicMenuItem(R.string.quit,
				android.R.drawable.ic_menu_close_clear_cancel,null));
		
        setContentView(R.layout.main);
        
        this.setListAdapter(new IconicAdapter(this, menu_items_vector
				.toArray()));
       
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        
		IconicMenuItem item = ((IconicMenuItem) (this.getListAdapter()
				.getItem(position)));

		if (item.intent != null)
			startActivity(item.intent);
		else
			finish();
    }
    
}