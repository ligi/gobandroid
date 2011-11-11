package org.ligi.gobandroid_hd.ui;

import org.ligi.android.common.adapter.LinkAndDescriptionAdapter;
import org.ligi.android.common.adapter.LinkWithDescription;
import org.ligi.gobandroid_hd.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class SGFOnlineListFragment extends  ListFragment{
	
	public LinkWithDescription[] links=new LinkWithDescription[] {

			// source pro games
			new LinkWithDescription("http://www.andromeda.com/people/ddyer/age-summer-94/companion.html","Companion"),
			new LinkWithDescription("http://homepages.cwi.nl/~aeb/go/games/games/Judan/","Judan"),
			new LinkWithDescription("http://gogameworld.com/gophp/pg_samplegames.php","Commented gogameworld sample games"),
			new LinkWithDescription("http://sites.google.com/site/byheartgo/","byheartgo"),
						
			// problems
			new LinkWithDescription("http://www.usgo.org/problems/index.html","USGo Problems" ),
			
			//mixed
			new LinkWithDescription("http://www.britgo.org/bgj/recent.html","Britgo recent")
			//dead not there anymore			new LinkWithDescription("http://egoban.org/@@recent_games","egoban"),
	};
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setListAdapter(LinkAndDescriptionAdapter.createByArray(this.getActivity(), links,R.layout.two_line_list_item));
		this.getListView().setCacheColorHint(0);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		startActivity(new Intent( "android.intent.action.VIEW",	Uri.parse(links[position].getURL())));     
	}
}
