package org.ligi.gobandroid_hd.ui.links;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.ligi.gobandroid_hd.R;

public abstract class LinkListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setAdapter(new TwoLineRecyclerAdapter(getData()));
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        return recyclerView;
    }

    abstract TwoLinedWithLink[] getData();
}
