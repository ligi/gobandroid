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

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * This is the main Activity of gobandroid
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         
**/

public class SGFOnlineListActivity extends ListActivity {
	public String[] menu_items= {
								 "Commented gogameworld sample games",
								  "Britgo recent", 
    							 "USGo Problems" ,
    							 "egoban" ,
    							 "byheartgo",
    							 "Judan",
    							 "companion"};
    
	public String[] menu_urls = {
			"http://gogameworld.com/gophp/pg_samplegames.php",
			"http://www.britgo.org/bgj/recent.html",
			"http://www.usgo.org/problems/index.html",
			"http://egoban.org/@@recent_games",
			"http://sites.google.com/site/byheartgo/",
			"http://homepages.cwi.nl/~aeb/go/games/games/Judan/",
			"http://www.andromeda.com/people/ddyer/age-summer-94/companion.html"
	};
    
    
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

        startActivity(new Intent( "android.intent.action.VIEW",
        		Uri.parse( menu_urls[position])));     
    }
    
}