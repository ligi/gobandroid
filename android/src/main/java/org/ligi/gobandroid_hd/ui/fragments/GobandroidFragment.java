package org.ligi.gobandroid_hd.ui.fragments;

import android.support.v4.app.Fragment;
import android.view.View;
import org.ligi.gobandroid_hd.App;

public class GobandroidFragment extends Fragment {
    // very nice hint by Jake Wharton via twitter
    @SuppressWarnings("unchecked")
    public <T> T findById(View view, int id) {
        return (T) view.findViewById(id);
    }

    public App getApp() {
        return (App) getActivity().getApplicationContext();
    }
}
