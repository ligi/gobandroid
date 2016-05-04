package org.ligi.gobandroid_hd.ui.sgf_listing.item_view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.io.File;
import org.ligi.gobandroid_hd.R;

public class PathViewHolder extends RecyclerView.ViewHolder implements ViewHolderInterface {

    @BindView(R.id.pathName)
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
