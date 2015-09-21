package org.ligi.gobandroid_hd.ui.links;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import org.ligi.gobandroid_hd.R;

class TwoLineRecyclerAdapter extends RecyclerView.Adapter<TwoLineRecyclerViewHolder> {

    private final TwoLinedWithLink[] twoLinedWithLinkContent;

    TwoLineRecyclerAdapter(@NonNull final TwoLinedWithLink[] twoLinedWithLinkContent) {
        this.twoLinedWithLinkContent = twoLinedWithLinkContent;
    }

    @Override
    public TwoLineRecyclerViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater from = LayoutInflater.from(parent.getContext());
        return new TwoLineRecyclerViewHolder(from.inflate(R.layout.two_line_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final TwoLineRecyclerViewHolder holder, final int position) {
        holder.bind(twoLinedWithLinkContent[position]);
    }

    @Override
    public int getItemCount() {
        return twoLinedWithLinkContent.length;
    }
}
