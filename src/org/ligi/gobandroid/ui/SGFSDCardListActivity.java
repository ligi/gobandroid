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
import java.util.Vector;

import org.ligi.gobandroid.R;
import org.ligi.gobandroid.logic.Logger;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
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

public class SGFSDCardListActivity extends ListActivity {
    
	private String[] menu_items;
    private File[] files;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoPrefs.init(this);
        
        setContentView(R.layout.main);
        
        String sgf_path=GoPrefs.getSGFPath();
        
        File dir;
        
        if (this.getIntent().getData()!=null)
        	dir=new File(this.getIntent().getData().getPath());
        else
        	dir=new File(sgf_path);
        if (dir==null){
    		new AlertDialog.Builder(this).setTitle(R.string.problem_listing_sgf).setMessage(getResources().getString(R.string.sgf_path_invalid) +" " +sgf_path)
    		.setPositiveButton(R.string.ok,  new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				finish();
    			}
    		}).show();

            return;
            }

        files=dir.listFiles();
        
        
        if (files==null){
    		new AlertDialog.Builder(this).setTitle(R.string.problem_listing_sgf).setMessage(getResources().getString(R.string.there_are_no_files_in) + " " +sgf_path )
    		.setPositiveButton(R.string.ok,  new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    			finish();	
    			}
    		}).show();

            return;
            }
        
        Vector<String> fnames=new Vector<String>();
        for(File file:files) 
        	if ((file.getName().endsWith(".sgf"))||(file.isDirectory()))
        		fnames.add(file.getName());
        		
        menu_items=(String[])fnames.toArray(new String[fnames.size()]);
        
        this.setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, menu_items));
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (files[position].isDirectory())
        {
        	Intent sgf_list_intent=new Intent(this,SGFSDCardListActivity.class);
        	sgf_list_intent.setData(Uri.parse("file://"+files[position].getAbsolutePath()));
        	startActivity(sgf_list_intent);
        	
        }
        else
        {
        	Intent go_intent=new Intent(this,GoActivity.class);
        	go_intent.setData(Uri.parse( "file://" + files[position]));
        	startActivity(go_intent);
        }
        
    }
    
}