package org.ligi.gobandroid_hd.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ZoomGameExtrasFragment extends Fragment {

	private GoBoardViewHD board;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		board=new GoBoardViewHD(this.getActivity(),false,3.0f);
		
		return board;
	}
	
	public GoBoardViewHD getBoard() {
		return board;
	}

	
}
