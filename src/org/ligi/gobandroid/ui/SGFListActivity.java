package org.ligi.gobandroid.ui;


import java.io.File;
import java.util.Vector;

import org.ligi.gobandroid.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * This is the main Activity of gobandroid
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         
**/

public class SGFListActivity extends ListActivity {
    public String[] menu_items;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        String sgf_path="/";
        
    	File dir=new File("/sdcard/gobandroid");
        
    	//        	Dialog dlg=new Dialog(this);
        
        /*dlg.setTitle("Foo");
        dlg.show();
        */
        if (dir==null){
        	
        
		new AlertDialog.Builder(this).setTitle("Save SGF").setMessage("How should the file I will write to " +sgf_path + " be named?")
		.setPositiveButton("Ok", null).show();
        //finish();
        return;
        }
        Log.i("gobandroid" ,"dir!=null");
        File[] files=dir.listFiles();
        
        
        if (files==null){
        	
            
    		new AlertDialog.Builder(this).setTitle("Problem listing SGF's").setMessage("There are no files in " +sgf_path + "")
    		.setPositiveButton("Ok", null).show();
            finish();
            return;
            }
        
        Vector<String> fnames=new Vector<String>();
        for(File file:files) {
    		Log.i("gobandroid" ,"processing file"+file);
        	if (file.getName().endsWith(".sgf"))
        		{
        		Log.i("gobandroid" ,"adding" + file.getName());
        		fnames.add(file.getName());
        		}
        }
        
        menu_items=(String[])fnames.toArray(new String[fnames.size()]);
        
        
        this.setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, menu_items));
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        switch (position) {
              }
    }
    
}