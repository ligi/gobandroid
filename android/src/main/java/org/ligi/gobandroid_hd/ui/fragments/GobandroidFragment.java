package org.ligi.gobandroid_hd.ui.fragments;

import android.support.v4.app.Fragment;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.model.GameProvider;

import javax.inject.Inject;

public abstract class GobandroidFragment extends Fragment {

    @Inject
    protected GameProvider gameProvider;

    @Inject
    protected InteractionScope interactionScope;

    public GobandroidFragment() {
        App.component().inject(this);
    }

}
