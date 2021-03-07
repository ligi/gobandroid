package org.ligi.gobandroid_hd.ui.fragments

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.core.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.nav_button_container.*
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.events.GameChangedEvent
import org.ligi.gobandroid_hd.logic.GoMove
import org.ligi.gobandroid_hd.ui.GoPrefs
import org.ligi.gobandroid_hd.ui.alerts.GameForwardAlert

class NavigationFragment : GobandroidGameAwareFragment() {

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
            = inflater.inflate(R.layout.nav_button_container, container, false)


    override fun onStart() {
        super.onStart()
        updateButtonStates()

        btn_next.setOnClickListener {
            if (GoPrefs.isShowForwardAlertWanted) {
                GameForwardAlert.showIfNeeded(requireActivity(), game)
            } else {
                game.redo(0)
            }
        }

        btn_prev.setOnClickListener {
            if (game.canUndo()) {
                game.undo()
            }
        }

        btn_first.setOnClickListener {
            val nextJunction = game.findPrevJunction()
            if (nextJunction!!.isFirstMove) {
                game.jump(nextJunction)
            } else {
                showJunctionInfoSnack(R.string.found_junction_snack_for_first)
                game.jump(nextJunction.nextMoveVariations[0])
            }
        }

        btn_first.setOnLongClickListener {
            game.jump(game.findFirstMove())
            true
        }

        btn_last.setOnClickListener {
            val nextJunction = game.findNextJunction()
            if (nextJunction!!.hasNextMove()) {
                showJunctionInfoSnack(R.string.found_junction_snack_for_last)
                game.jump(nextJunction.nextMoveVariations[0])
            } else {
                game.jump(nextJunction)
            }
        }

        btn_last.setOnLongClickListener {
            game.jump(game.findLastMove())
            true
        }
    }

    override fun onGoGameChanged(gameChangedEvent: GameChangedEvent?) {
        super.onGoGameChanged(gameChangedEvent)
        updateButtonStates()
    }

    private fun updateButtonStates() {
        setImageViewState(game.canUndo(), btn_first, btn_prev)
        setImageViewState(game.canRedo(), btn_next, btn_last)
        bindButtonToMove(game.nextVariationWithOffset(-1), btn_previous_var)
        bindButtonToMove(game.nextVariationWithOffset(1), btn_next_var)
    }

    private fun bindButtonToMove(move: GoMove?, button: ImageView) {
        setImageViewState(move != null, button)
        button.setOnClickListener { game.jump(move) }
    }

    private fun setImageViewState(state: Boolean, vararg views: ImageView) {
        views.forEach {
            it.isEnabled = state
            it.alpha = if (state) 1f else 0.4f
        }
    }

    private fun showJunctionInfoSnack(found_junction_snack_for_last: Int) {
        if (!GoPrefs.hasAcknowledgedJunctionInfo) {
            Snackbar.make(btn_last!!, found_junction_snack_for_last, Snackbar.LENGTH_LONG).setAction(android.R.string.ok) { GoPrefs.hasAcknowledgedJunctionInfo = true }.show()
        }
    }

}
