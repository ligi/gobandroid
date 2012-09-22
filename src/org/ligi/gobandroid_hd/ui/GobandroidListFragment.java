package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_beta.R;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

public class GobandroidListFragment extends ListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setCacheColorHint(0);
        getListView().setDivider((GradientDrawable)getActivity().getResources().getDrawable(R.drawable.divider));
        getListView().setDividerHeight(2);
        
	}
}
