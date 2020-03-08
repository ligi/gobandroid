package org.ligi.gobandroid_hd.ui.fragments

import androidx.fragment.app.Fragment
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy

import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.InteractionScope
import org.ligi.gobandroid_hd.model.GameProvider


abstract class GobandroidFragment : Fragment() {

    protected val gameProvider: GameProvider  by App.kodein.lazy.instance()
    protected val interactionScope: InteractionScope  by App.kodein.lazy.instance()

}
