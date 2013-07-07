package org.ligi.gobandroid_hd.ui.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

import org.ligi.gobandroid_hd.GobandroidApp;
import org.ligi.gobandroid_hd.logic.GoGame;

public class GobandroidFragment extends Fragment {
    public GoGame getGame() {
        return ((GobandroidApp) getActivity().getApplicationContext()).getGame();
    }

    // very nice hint by Jake Wharton via twitter
    @SuppressWarnings("unchecked")
    public <T> T findById(View view, int id) {
        return (T) view.findViewById(id);
    }

    public GobandroidApp getApp() {
        return (GobandroidApp) getActivity().getApplicationContext();
    }
}
