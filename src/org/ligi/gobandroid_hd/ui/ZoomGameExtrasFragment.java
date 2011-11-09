package org.ligi.gobandroid_hd.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ZoomGameExtrasFragment extends Fragment {


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		TextView tv=new TextView(this.getActivity());
		tv.setText("ZOOM");
		return tv;
	}

	
}
