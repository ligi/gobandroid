package org.ligi.gobandroid_hd.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.ligi.gobandroid_hd.events.GameChangedEvent
import org.ligi.gobandroid_hd.logic.GoGame

abstract class GobandroidGameAwareFragment : GobandroidFragment() {

    protected lateinit var game: GoGame

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        game = gameProvider.get()
        EventBus.getDefault().register(this)
        return createView(inflater!!, container, savedInstanceState)
    }

    abstract fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onGoGameChanged(gameChangedEvent: GameChangedEvent?) {

    }

}
