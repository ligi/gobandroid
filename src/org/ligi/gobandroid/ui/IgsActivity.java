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
import org.ligi.gobandroid.ui.GoPrefs;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Activity to select a source from where to load SGF's
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         
**/

public class IgsActivity extends ListActivity {
       
	private String[] menu_items= {"Connect", "Console", "Preferences" };

    private final static int MENU_CONNECT=0;
    private final static int MENU_CONSOLE=1;
    private final static int MENU_PREFERENCES=2;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        GoPrefs.init(this);
        
        setContentView(R.layout.main);
        this.setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, menu_items));
           
    }   

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Context context = getApplicationContext();
        CharSequence text = "initial";
    	int duration = Toast.LENGTH_SHORT;

    	switch (position) {

        	case MENU_CONNECT:
            	text = "Connect";
        		break;
        		
            case MENU_CONSOLE:
            	text = "Console";
            	break;

            case MENU_PREFERENCES:
            	text = "Preferences";
            	break;
    	}

    	Toast toast = Toast.makeText(context, text, duration);
    	toast.show();        	    
    }
}