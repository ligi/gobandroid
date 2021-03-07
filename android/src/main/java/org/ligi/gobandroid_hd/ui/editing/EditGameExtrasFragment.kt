package org.ligi.gobandroid_hd.ui.editing

import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.edit_extras.*
import kotlinx.android.synthetic.main.edit_extras.view.*
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.events.GameChangedEvent
import org.ligi.gobandroid_hd.ui.editing.model.EditGameMode
import org.ligi.gobandroid_hd.ui.fragments.GobandroidGameAwareFragment
import org.ligi.kaxt.doAfterEdit

class EditGameExtrasFragment : GobandroidGameAwareFragment() {


    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val editModePool = (activity as EditGameActivity).statefulEditModeItems
        val view = inflater.inflate(R.layout.edit_extras, container, false)

        val editModeAdapter = EditModeButtonsAdapter(editModePool)
        view.gridView.adapter = editModeAdapter

        view.gridView.onItemClickListener = OnItemClickListener { adapter, _, position, _ ->
            editModePool.setModeByPosition(position)
            (adapter.adapter as BaseAdapter).notifyDataSetChanged()
        }

        view.editSwitch.isChecked = true

        view.editSwitch.setOnCheckedChangeListener { _, isChecked ->
            view.gridView.visibility = if (isChecked) View.VISIBLE else View.GONE
            editModePool.mode = if (isChecked) EditGameMode.BLACK else EditGameMode.PLAY
            editModeAdapter.notifyDataSetChanged()
        }
        view.comment_et.setText(gameProvider.get().actMove.comment)
        view.comment_et.setHint(R.string.enter_your_comments_here)
        view.comment_et.gravity = Gravity.TOP
        view.comment_et.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_on_board_bg))

        view.comment_et.doAfterEdit {
            gameProvider.get().actMove.comment = it.toString()
        }

        return view
    }

    override fun onGoGameChanged(gameChangedEvent: GameChangedEvent?) {
        super.onGoGameChanged(gameChangedEvent)
        activity?.runOnUiThread { comment_et.setText(gameProvider.get().actMove.comment) }
    }

}
