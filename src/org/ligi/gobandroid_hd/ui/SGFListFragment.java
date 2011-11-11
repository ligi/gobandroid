package org.ligi.gobandroid_hd.ui;

import java.io.File;

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
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    	
        Intent go_intent=new Intent(this.getActivity(),SGFLoadActivity.class);
     	
        if (new File(dir + "/" + menu_items[position]).isDirectory())
        	go_intent=new Intent(this.getActivity(),SGFSDCardListActivity.class);
        
        go_intent.setData(Uri.parse( "file://" + dir + "/" + menu_items[position]));
        startActivity(go_intent);
        
	}
}