package org.ligi.gobandroid_hd.ui.editing

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.SwitchCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.GridView
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.events.GameChangedEvent
import org.ligi.gobandroid_hd.ui.editing.model.EditGameMode
import org.ligi.gobandroid_hd.ui.fragments.GobandroidGameAwareFragment
import org.ligi.kaxt.doAfterEdit

class EditGameExtrasFragment : GobandroidGameAwareFragment() {

    internal lateinit var editText: EditText

    override fun createView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {

        val editModePool = (activity as EditGameActivity).statefulEditModeItems
        val view = inflater.inflate(R.layout.edit_extras, container, false)

        val mode_grid = view.findViewById(R.id.gridView) as GridView
        val editSwitch = view.findViewById(R.id.editSwitch) as SwitchCompat
        val editText = view.findViewById(R.id.comment_et) as EditText

        val editModeAdapter = EditModeButtonsAdapter(editModePool)
        mode_grid.adapter = editModeAdapter

        mode_grid.onItemClickListener = OnItemClickListener { adapter, arg1, position, arg3 ->
            editModePool.setModeByPosition(position)
            (adapter.adapter as BaseAdapter).notifyDataSetChanged()
        }

        editSwitch.isChecked = true

        editSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            mode_grid.visibility = if (isChecked) View.VISIBLE else View.GONE
            editModePool.mode = if (isChecked) EditGameMode.BLACK else EditGameMode.PLAY
            editModeAdapter.notifyDataSetChanged()
        }
        editText.setText(gameProvider.get().actMove.comment)
        editText.setHint(R.string.enter_your_comments_here)
        editText.gravity = Gravity.TOP
        editText.setTextColor(ContextCompat.getColor(context, R.color.text_color_on_board_bg))

        editText.doAfterEdit {
            gameProvider.get().actMove.comment = it.toString()
        }

        return view
    }

    override fun onGoGameChanged(gameChangedEvent: GameChangedEvent) {
        super.onGoGameChanged(gameChangedEvent)
        activity?.runOnUiThread { editText.setText(gameProvider.get().actMove.comment) }
    }

}
