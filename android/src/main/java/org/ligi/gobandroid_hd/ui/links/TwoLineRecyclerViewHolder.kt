package org.ligi.gobandroid_hd.ui.links

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.two_line_list_item.view.*

class TwoLineRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(twoLinedWithLink: LinkWithDescription) {
        itemView.text1.text = twoLinedWithLink.line1
        itemView.text2.text = twoLinedWithLink.line2
        itemView.setOnClickListener { v -> v.context.startActivity(Intent("android.intent.action.VIEW", Uri.parse(twoLinedWithLink.link))) }
    }
}
