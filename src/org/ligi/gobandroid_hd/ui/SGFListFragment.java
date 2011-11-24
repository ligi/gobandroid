package org.ligi.gobandroid_hd.ui;

import java.io.File;

import org.ligi.android.common.files.FileHelper;
import org.ligi.gobandroid_hd.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SGFListFragment extends ListFragment {

	private String[] menu_items;
    private String dir;
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putStringArray("menu_items", menu_items);
		outState.putString("dir",dir);
		
	 }


	public SGFListFragment() {
	}
	
	public SGFListFragment(String[] menu_items,File dir) {
		this.menu_items=menu_items;
		this.dir=dir.getAbsolutePath();
	}
	    	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		if(menu_items==null) 
			menu_items=savedInstanceState.getStringArray("menu_items");
		
		if(dir==null) 
			dir=savedInstanceState.getString("dir");
		
        this.setListAdapter(new ArrayAdapter<String>(this.getActivity(),
        		R.layout.list_item, menu_items));
        
        this.getListView().setCacheColorHint(0);
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    	
        Intent intent2start=new Intent(this.getActivity(),SGFLoadActivity.class);
        String fname=dir + "/" + menu_items[position];
        
        
        if (fname.endsWith(".golink")) {
        	fname=FileHelper.file2String(new File(fname));
        }
        
        if (fname.contains(":#")) {
        	String[] arr_content=fname.split(":#");
        	int move_id=Integer.parseInt(arr_content[1]);
        	fname=arr_content[0];
        	intent2start.putExtra("move_num",move_id);
        }
        
        if (!fname.endsWith(".sgf")) {
        	intent2start=new Intent(this.getActivity(),SGFSDCardListActivity.class);
        }
        
        if (!fname.contains("://"))
        	fname="file://"+fname;
                
        intent2start.setData(Uri.parse( fname));
        
        startActivity(intent2start);
        
	}
}