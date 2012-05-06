package org.ligi.gobandroid_hd.ui.sgf_listing;

import java.io.File;
import org.ligi.gobandroid_hd.GobandroidApp;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.ui.GoLinkLoadActivity;
import org.ligi.gobandroid_hd.ui.SGFLoadActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class SGFListFragment extends ListFragment{

	private String[] menu_items;
    private String dir;
	private BaseAdapter adapter;
	
    public SGFListFragment() {
	}
	
	public SGFListFragment(String[] menu_items,File dir) {
		this.menu_items=menu_items;
		this.dir=dir.getAbsolutePath();
	}
	
		    	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		InteractionScope interaction_scope=((GobandroidApp)(getActivity().getApplicationContext())).getInteractionScope();
		
		if(menu_items==null) 
			menu_items=savedInstanceState.getStringArray("menu_items");
		
		if(dir==null) 
			dir=savedInstanceState.getString("dir");
		
		 if (interaction_scope.getMode()==InteractionScope.MODE_TSUMEGO)
			 adapter=new TsumegoPathViewAdapter(this.getActivity(), menu_items,dir);
		 else if (interaction_scope.getMode()==InteractionScope.MODE_REVIEW)
			 adapter=new ReviewPathViewAdapter(this.getActivity(), menu_items,dir);
		 
        this.setListAdapter(adapter);
        
        this.getListView().setCacheColorHint(0);
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
    	
        Intent intent2start=new Intent(this.getActivity(),SGFLoadActivity.class);
        String fname=dir + "/" + menu_items[position];
        
        
        // check if it is directory behind golink or general
        if (GoLink.isGoLink(fname) ) {
        	intent2start.setClass(getActivity(), GoLinkLoadActivity.class);
        } else if (!fname.endsWith(".sgf")) {
        	intent2start.setClass(getActivity(), SGFSDCardListActivity.class);
        }
                
        intent2start.setData(Uri.parse( fname));
        startActivity(intent2start);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putStringArray("menu_items", menu_items);
		outState.putString("dir",dir);
	 }
}