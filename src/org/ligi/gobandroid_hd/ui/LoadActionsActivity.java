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

import org.ligi.gobandroid_hd.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Activity to select a source from where to load SGF's
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         
**/

public class LoadActionsActivity extends ListActivity {
    
	private String[] menu_items= {"SD Card", "Online" };
    
    private final static int MENU_SDCARD=0;
    private final static int MENU_ONLINE=1;
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        GoPrefs.init(this);
        
        setContentView(R.layout.list);
        this.setListAdapter(new ArrayAdapter<String>(this,
        		R.layout.list_item, menu_items));
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent go_intent=null;
        switch (position) {
        	case MENU_SDCARD:
        		go_intent=new Intent(this,SGFSDCardListActivity.class);
        		break;
        		
            case MENU_ONLINE:
            	go_intent=new Intent(this,SGFOnlineListActivity.class);
            	break;
         }
   
        startActivity(go_intent);
    }
    
}