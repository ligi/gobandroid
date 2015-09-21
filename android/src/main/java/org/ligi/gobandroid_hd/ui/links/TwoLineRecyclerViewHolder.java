package org.ligi.gobandroid_hd.ui.links;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class TwoLineRecyclerViewHolder extends RecyclerView.ViewHolder {

    @Bind(android.R.id.text1)
    TextView text1;

    @Bind(android.R.id.text2)
    TextView text2;

    public TwoLineRecyclerViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(final TwoLinedWithLink twoLinedWithLink) {
        text1.setText(twoLinedWithLink.getLine1());
        text2.setText(twoLinedWithLink.getLine2());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(twoLinedWithLink.getLink())));
            }
        });
    }
}
