package org.ligi.gobandroid_hd.ui;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ZoomGameExtrasFragment extends Fragment {

	private GoBoardViewHD board;
	private boolean show_shadow_stone=false;
	
	public ZoomGameExtrasFragment() {
		
	}
	
	public ZoomGameExtrasFragment(boolean _show_shadow_stone) {
		show_shadow_stone=_show_shadow_stone;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		board=new GoBoardViewHD(this.getActivity(),false,3.0f);
		board.do_mark_act=show_shadow_stone;
		return board;
	}
	
	public GoBoardViewHD getBoard() {
		return board;
	}

	
}
