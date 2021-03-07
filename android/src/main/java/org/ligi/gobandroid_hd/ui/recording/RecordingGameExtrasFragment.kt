package org.ligi.gobandroid_hd.ui.recording

import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.EditText
import android.widget.LinearLayout
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.events.GameChangedEvent
import org.ligi.gobandroid_hd.ui.fragments.GobandroidGameAwareFragment
import org.ligi.kaxt.doAfterEdit

class RecordingGameExtrasFragment : GobandroidGameAwareFragment() {

    val editText by lazy { EditText(activity) }

    private val handler = Handler()

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val lp = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        editText.setText(game.actMove.comment)
        editText.setHint(R.string.enter_your_comments_here)
        editText.gravity = Gravity.TOP
        editText.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_on_board_bg))

        editText.doAfterEdit {
            game.actMove.comment = it.toString()
        }

        editText.layoutParams = lp

        return editText
    }

    override fun onGoGameChanged(gameChangedEvent: GameChangedEvent?) {
        super.onGoGameChanged(gameChangedEvent)
        handler.post {
            if (activity != null) {
                editText.setText(game.actMove.comment)
            }
        }
    }

}
