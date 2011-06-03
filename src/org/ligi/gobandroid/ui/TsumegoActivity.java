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

import java.io.File;
import java.util.Random;

import org.ligi.gobandroid.R;
import org.ligi.gobandroid.ui.GoPrefs;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Activity to select a source from where to load tsumego
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         
**/

public class TsumegoActivity extends ListActivity {
       
	private String[] tsumego_list;
	
    private final static int MENU_CONNECT=0;
    private final static int MENU_CONSOLE=1;
    private final static int MENU_PREFERENCES=2;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        GoPrefs.init(this);
        
        setContentView(R.layout.main);
        File tsumego_dir = new File("/sdcard/tsumego");
        tsumego_list = tsumego_dir.list();
        
        this.setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, tsumego_list));
           
    }   

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Context context = getApplicationContext();
    	int duration = Toast.LENGTH_SHORT;
    	String tsumego_pack_str = "/sdcard/tsumego/" + tsumego_list[position] + "/";
    	File tsumego_pack = new File(tsumego_pack_str);
    	String[] tsumego_list = tsumego_pack.list();
    	Random generator = new Random();
    	int index = generator.nextInt(tsumego_list.length);
    	String tsumego = tsumego_pack_str + tsumego_list[index];
    	
    	Toast toast = Toast.makeText(context, tsumego, duration);
    	toast.show();
    	
    	Intent go_intent=new Intent(this,SGFLoadActivity.class);
    	go_intent.setData(Uri.parse( "file://" + tsumego));
    	startActivity(go_intent);
    }
}