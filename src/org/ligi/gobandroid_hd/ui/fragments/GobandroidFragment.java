package org.ligi.gobandroid_hd.ui.fragments;

import org.ligi.gobandroid_hd.GobandroidApp;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.tracedroid.logging.Log;

import android.support.v4.app.Fragment;


public class GobandroidFragment extends Fragment {
	public GoGame getGame() {
		Log.i("preGetGame  activity" + getActivity() );
		Log.i("preGetGame content " + getActivity().getApplicationContext() + " activity" + getActivity() );
		return ((GobandroidApp)getActivity() .getApplicationContext()).getGame();
	}
}
