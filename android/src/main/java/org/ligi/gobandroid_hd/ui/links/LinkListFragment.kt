package org.ligi.gobandroid_hd.ui.links

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.ligi.gobandroid_hd.R

abstract class LinkListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?)
            = (inflater!!.inflate(R.layout.recycler_view, container, false) as RecyclerView).apply {
        adapter = TwoLineRecyclerAdapter(getData())
        layoutManager = LinearLayoutManager(container!!.context)

    }

    internal abstract fun getData(): Array<LinkWithDescription>
}
