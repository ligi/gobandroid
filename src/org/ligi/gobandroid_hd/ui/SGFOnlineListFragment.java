package org.ligi.gobandroid_hd.ui;

import org.ligi.android.common.adapter.LinkAndDescriptionAdapter;
import org.ligi.android.common.adapter.LinkWithDescription;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class SGFOnlineListFragment extends  ListFragment{
	
	public LinkWithDescription[] links=new LinkWithDescription[] {
			new LinkWithDescription("http://gogameworld.com/gophp/pg_samplegames.php","Commented gogameworld sample games"),
			new LinkWithDescription("http://www.britgo.org/bgj/recent.html","Britgo recent"),
			new LinkWithDescription("http://www.usgo.org/problems/index.html","USGo Problems" ),
			new LinkWithDescription("http://egoban.org/@@recent_games","egoban"),
			new LinkWithDescription("http://sites.google.com/site/byheartgo/","byheartgo"),
			new LinkWithDescription("http://homepages.cwi.nl/~aeb/go/games/games/Judan/","Judan"),
			new LinkWithDescription("http://www.andromeda.com/people/ddyer/age-summer-94/companion.html","companion")
	};
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GoPrefs.init(this.getActivity());
		this.setListAdapter(LinkAndDescriptionAdapter.createByArray(this.getActivity(), links));
		this.getListView().setCacheColorHint(0);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		startActivity(new Intent( "android.intent.action.VIEW",	Uri.parse(links[position].getURL())));     
	}
}
