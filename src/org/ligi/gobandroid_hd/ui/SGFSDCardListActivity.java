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

import java.io.File;
import java.util.Vector;
import java.util.Arrays;

import org.ligi.android.common.dialogs.ActivityFinishOnClickListener;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

import android.app.AlertDialog;
import android.os.Bundle;

/**
 * Activity to load SGF's from SD card
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         
**/

public class SGFSDCardListActivity extends GobandroidFragmentActivity {
    
	private String[] menu_items;
    private File[] files;
    private File dir;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list);
        
        String sgf_path=getSettings().getSGFBasePath();
        
        if (this.getIntent().getData()!=null)
        	dir=new File(this.getIntent().getData().getPath());
        else
        	dir=new File(sgf_path);
        
        AlertDialog.Builder alert=new AlertDialog.Builder(this).setTitle(R.string.problem_listing_sgf).setPositiveButton(R.string.ok,  new ActivityFinishOnClickListener(this));
        
        if (dir==null){
        	alert.setMessage(getResources().getString(R.string.sgf_path_invalid) +" " +sgf_path).show();
            return;
        }

        files=dir.listFiles();
        
        if (files==null){
    		alert.setMessage(getResources().getString(R.string.there_are_no_files_in) + " " +dir.getAbsolutePath() ).show();
            return;
        }
        
        Vector<String> fnames=new Vector<String>();
        for(File file:files) 
        	if ((file.getName().endsWith(".sgf"))||(file.isDirectory())||(file.getName().endsWith(".golink")))
        		fnames.add(file.getName());


        if (fnames.size()==0){
    		alert.setMessage(getResources().getString(R.string.there_are_no_files_in) + " " +sgf_path ).show();
            return;
        }
        
        menu_items=(String[])fnames.toArray(new String[fnames.size()]);
        Arrays.sort(menu_items);

        this.getSupportFragmentManager().beginTransaction().add(R.id.list_fragment, new SGFListFragment(menu_items,dir)).commit();
    }
    
}
