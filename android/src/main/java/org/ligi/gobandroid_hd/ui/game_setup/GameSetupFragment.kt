package org.ligi.gobandroid_hd.ui.game_setup

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.game_setup_inner.*
import org.ligi.gobandroid_hd.InteractionScope
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.ui.GoActivity
import org.ligi.gobandroid_hd.ui.GoPrefs
import org.ligi.gobandroid_hd.ui.fragments.GobandroidFragment

class GameSetupFragment : GobandroidFragment(), OnSeekBarChangeListener {

    val size_offset = 2

    var act_size = 9
    var act_handicap = 0

    private var wanted_size = act_size

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

    private var uiHandler = Handler()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.game_setup_inner, container, false)

        ButterKnife.bind(this, view)

        // set defaults
        act_size = GoPrefs.lastBoardSize
        act_handicap = GoPrefs.lastHandicap

        return view
    }

    override fun onStart() {

        size_seek.setOnSeekBarChangeListener(this)
        handicap_seek.setOnSeekBarChangeListener(this)

        size_button9x9.setOnClickListener { setSize(9) }
        size_button13x13.setOnClickListener { setSize(13) }
        size_button19x19.setOnClickListener { setSize(19) }
        refresh_ui()
        super.onStart()
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

        if (seekBar === size_seek && act_size != (progress + size_offset).toByte().toInt())
            setSize(progress + size_offset)
        else if (seekBar === handicap_seek && act_handicap != progress.toByte().toInt()) act_handicap = progress.toByte().toInt()

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

        if (interactionScope.mode === InteractionScope.Mode.GNUGO)
            size_seek.max = 19 - size_offset


        GoPrefs.lastBoardSize = act_size
        GoPrefs.lastHandicap = act_handicap

        if (gameProvider.get().size != act_size || gameProvider.get().handicap != act_handicap) {
            gameProvider.set(GoGame(act_size, act_handicap))
        }

        if (activity is GoActivity) {
            val board = (activity as GoActivity).board

            if (board != null) {
                board.regenerateStoneImagesWithNewSize()
                board.invalidate()
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }

}
