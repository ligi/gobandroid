package org.ligi.gobandroid_hd.ui.sgf_listing.item_view_holder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.sgf_dir_list_item.view.*
import java.io.File

class PathViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ViewHolderInterface {

    override fun apply(file: File) {
        itemView.pathName.text = file.name
    }
}
