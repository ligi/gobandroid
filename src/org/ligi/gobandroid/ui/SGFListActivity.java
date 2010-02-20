package org.ligi.gobandroid.ui;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.CharBuffer;
import java.util.Vector;

import org.ligi.gobandroid.R;
import org.ligi.gobandroid.logic.SGFHelper;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
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
    File[] files;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        String sgf_path=GoPrefs.getSGFPath();
    	File dir=new File(sgf_path);
        
    	//        	Dialog dlg=new Dialog(this);
        
        /*dlg.setTitle("Foo");
        dlg.show();
        */
        Log.i("gobandroid" ,"dir!=null");

        if (dir==null){
    		new AlertDialog.Builder(this).setTitle("Problem listing SGF's").setMessage("The SGF Path is invalid " +sgf_path + "")
    		.setPositiveButton("Ok",  new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				finish();
    			}
    		}).show();

            return;
            }

        files=dir.listFiles();
        
        
        if (files==null){
    		new AlertDialog.Builder(this).setTitle("Problem listing SGF's").setMessage("There are no files in " +sgf_path + "")
    		.setPositiveButton("Ok",  new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    			finish();	
    			}
    		}).show();

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

        

        try {
			FileReader reader=new FileReader(files[position]);
			int c;
			String str="";
			while ((c=reader.read())!=-1)
			{
				str+=(char)c;
			}

	        Intent go_intent=new Intent(this,GoActivity.class);
	        
	        
	        
	        go_intent.putExtra("sgf",str );
	     
	        startActivity(go_intent);	
		} catch (Exception e) {
		
		}

		
        
        
    }
    
}