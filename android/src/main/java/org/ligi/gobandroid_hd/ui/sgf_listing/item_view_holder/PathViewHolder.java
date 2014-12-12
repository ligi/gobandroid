package org.ligi.gobandroid_hd.ui.sgf_listing.item_view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.ligi.gobandroid_hd.R;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PathViewHolder extends RecyclerView.ViewHolder implements ViewHolderInterface {

    @InjectView(R.id.pathName)
    TextView title;

    public PathViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    @Override
    public void apply(final File file) {
        title.setText(file.getName());
    }
}
