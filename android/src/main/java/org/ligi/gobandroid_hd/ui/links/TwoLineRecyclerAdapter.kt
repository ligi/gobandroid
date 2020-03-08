package org.ligi.gobandroid_hd.ui.links

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.ligi.gobandroid_hd.R

internal class TwoLineRecyclerAdapter(private val twoLinedWithLinkContent: Array<LinkWithDescription>) : RecyclerView.Adapter<TwoLineRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TwoLineRecyclerViewHolder {
        val from = LayoutInflater.from(parent.context)
        return TwoLineRecyclerViewHolder(from.inflate(R.layout.two_line_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: TwoLineRecyclerViewHolder, position: Int) {
        holder.bind(twoLinedWithLinkContent[position])
    }

    override fun getItemCount(): Int {
        return twoLinedWithLinkContent.size
    }
}
