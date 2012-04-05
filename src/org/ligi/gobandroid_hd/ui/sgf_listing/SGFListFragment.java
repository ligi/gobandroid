package org.ligi.gobandroid_hd.ui.sgf_listing;

import java.io.File;
import java.io.IOException;
import org.ligi.android.common.files.FileHelper;
import org.ligi.gobandroid_hd.ui.GoInteractionProvider;
import org.ligi.gobandroid_hd.ui.SGFLoadActivity;
import org.ligi.tracedroid.logging.Log;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class SGFListFragment extends ListFragment {

	private String[] menu_items;
    private String dir;
	private BaseAdapter adapter;
	
    public SGFListFragment() {
	}
	
	public SGFListFragment(String[] menu_items,File dir) {
		this.menu_items=menu_items;
		this.dir=dir.getAbsolutePath();
	}
	
	private void refresh() {
		adapter.notifyDataSetChanged();
	}
	    	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		if(menu_items==null) 
			menu_items=savedInstanceState.getStringArray("menu_items");
		
		if(dir==null) 
			dir=savedInstanceState.getString("dir");
		
		 if (GoInteractionProvider.getMode()==GoInteractionProvider.MODE_TSUMEGO)
			 adapter=new TsumegoPathViewAdapter(this.getActivity(), menu_items,dir);
		 else if (GoInteractionProvider.getMode()==GoInteractionProvider.MODE_REVIEW)
			 adapter=new ReviewPathViewAdapter(this.getActivity(), menu_items,dir);
		 
        this.setListAdapter(adapter);
        
        this.getListView().setCacheColorHint(0);
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
    	
        Intent intent2start=new Intent(this.getActivity(),SGFLoadActivity.class);
        String fname=dir + "/" + menu_items[position];
        
        if (fname.endsWith(".golink")) {
        	try {
        		fname=FileHelper.file2String(new File(fname));
        	} catch (IOException e) {
        		Log.w("problem loading file" + fname.toString());
        	}
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
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putStringArray("menu_items", menu_items);
		outState.putString("dir",dir);
	 }

	

}