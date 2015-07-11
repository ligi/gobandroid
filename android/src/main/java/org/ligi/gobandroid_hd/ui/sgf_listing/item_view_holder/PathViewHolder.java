package org.ligi.gobandroid_hd.ui.sgf_listing.item_view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import org.ligi.gobandroid_hd.R;

import java.io.File;

import butterknife.ButterKnife;

public class PathViewHolder extends RecyclerView.ViewHolder implements ViewHolderInterface {

    @Bind(R.id.pathName)
    TextView title;

    public PathViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void apply(final File file) {
        title.setText(file.getName());
    }
}
