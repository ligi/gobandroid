package org.ligi.gobandroid_hd.ui.links;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import org.ligi.axt.adapters.LinkAndDescriptionAdapter;
import org.ligi.axt.adapters.LinkWithDescription;
import org.ligi.gobandroid_hd.R;

public class LinkListFragment extends ListFragment {

    public LinkWithDescription[] links;

    public LinkListFragment() {
    }

    public LinkListFragment(LinkWithDescription[] links) {
        this.links = links;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (links != null) {
            this.setListAdapter(LinkAndDescriptionAdapter.createByArray(this.getActivity(), links, R.layout.two_line_list_item));
            this.getListView().setCacheColorHint(0);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse(links[position].getURL())));
    }
}
