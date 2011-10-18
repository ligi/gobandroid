package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_hd.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SGFOnlineListFragment extends  ListFragment{
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
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		GoPrefs.init(this.getActivity());
	
		
		this.setListAdapter(new ArrayAdapter<String>(this.getActivity(),
				R.layout.list_item, menu_items));
	
		this.getListView().setCacheColorHint(0);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		startActivity(new Intent( "android.intent.action.VIEW",
		Uri.parse( menu_urls[position])));     
	}


}
