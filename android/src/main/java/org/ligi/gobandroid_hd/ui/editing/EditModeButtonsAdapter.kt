package org.ligi.gobandroid_hd.ui.editing

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import org.ligi.gobandroid_hd.ui.editing.model.EditModeItem

class EditModeButtonsAdapter(val editModePool: StatefulEditModeItems) : BaseAdapter() {

    override fun getCount(): Int {
        return editModePool.list.size
    }

    override fun getItem(i: Int): EditModeItem {
        return editModePool.list[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return EditModeButtonView(parent.context, getItem(position), editModePool.isPositionMode(position))
    }
}