package org.ligi.gobandroid_hd.ui.game_setup

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.chibatching.kotpref.bulk
import kotlinx.android.synthetic.main.game.*
import kotlinx.android.synthetic.main.game_setup_inner.*
import org.ligi.gobandroid_hd.InteractionScope
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.ui.GoActivity
import org.ligi.gobandroid_hd.ui.GoPrefs
import org.ligi.gobandroid_hd.ui.fragments.GobandroidFragment

class GameSetupFragment : GobandroidFragment(), OnSeekBarChangeListener {

    val size_offset = 2

    var act_size = GoPrefs.lastBoardSize
    var act_handicap = GoPrefs.lastHandicap
    var act_lineWidth = GoPrefs.boardLineWidth

    private var wanted_size = act_size

    private var uiHandler = Handler()

    private fun setSize(size: Int) {
        wanted_size = size

        uiHandler.post(object : Runnable {
            override fun run() {
                if (act_size != wanted_size) {
                    act_size += if (act_size > wanted_size) -1 else 1
                    uiHandler.postDelayed(this, 16)
                }

                if (!activity.isFinishing) {
                    refresh_ui()
                }
            }
        })

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.game_setup_inner, container, false)
        return view
    }

    override fun onStart() {

        size_seek.setOnSeekBarChangeListener(this)
        handicap_seek.setOnSeekBarChangeListener(this)
        line_width_seek.setOnSeekBarChangeListener(this)

        size_button9x9.setOnClickListener { setSize(9) }
        size_button13x13.setOnClickListener { setSize(13) }
        size_button19x19.setOnClickListener { setSize(19) }

        refresh_ui()
        super.onStart()
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

        if (seekBar === size_seek && act_size != (progress + size_offset)) {
            setSize(progress + size_offset)
        } else if (seekBar === handicap_seek) {
            act_handicap = progress.toByte().toInt()
        } else if (seekBar === line_width_seek) {
            act_lineWidth = progress.toByte().toInt()
        }

        refresh_ui()
    }

    private fun isAnimating() = act_size != wanted_size

    /**
     * refresh the ui elements with values from act_size / act_handicap
     */
    fun refresh_ui() {

        game_size_label.text = getString(R.string.size) + " " + act_size + "x" + act_size

        if (!isAnimating()) {
            // only enable handicap seeker when the size is 9x9 or 13x13 or 19x19
            handicap_seek.isEnabled = act_size == 9 || act_size == 13 || act_size == 19

            handicap_label.text = if (handicap_seek.isEnabled) {
                getString(R.string.handicap) + " " + act_handicap
            } else {
                getString(R.string.handicap_only_for)
            }
        }

        // the checks for change here are important - otherwise samsung moment
        // will die here with stack overflow
        if (act_size - size_offset != size_seek.progress) size_seek.progress = act_size - size_offset

        if (act_handicap != handicap_seek.progress) handicap_seek.progress = act_handicap

        if (act_lineWidth != line_width_seek.progress) line_width_seek.progress = act_lineWidth

        if (interactionScope.mode === InteractionScope.Mode.GNUGO)
            size_seek.max = 19 - size_offset

        GoPrefs.bulk {
            lastBoardSize = act_size
            lastHandicap = act_handicap
            boardLineWidth = act_lineWidth
        }

        if (gameProvider.get().size != act_size || gameProvider.get().handicap != act_handicap) {
            gameProvider.set(GoGame(act_size, act_handicap))
        }

        if (activity is GoActivity) {
            val board = (activity as GoActivity).go_board

            if (board != null) {
                board.regenerateStoneImagesWithNewSize()
                board.invalidate()

                board.setLineSize(line_width_seek.progress.toFloat())
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }

}
