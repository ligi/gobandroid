package org.ligi.gobandroid_hd.ui.review

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import kotlinx.android.synthetic.main.game.*
import org.ligi.gobandroid_hd.InteractionScope
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.Cell
import org.ligi.gobandroid_hd.logic.GoGame.MoveStatus.VALID
import org.ligi.gobandroid_hd.ui.GoActivity
import org.ligi.gobandroid_hd.ui.GobanDroidTVActivity
import org.ligi.gobandroid_hd.ui.alerts.GameForwardAlert
import org.ligi.gobandroid_hd.ui.fragments.CommentAndNowPlayingFragment
import org.ligi.gobandroid_hd.ui.ingame_common.SwitchModeHelper

class GoGamePlayerActivity : GoActivity() {

    private val PAUSE_FOR_LAST_MOVE = 30000
    private val PAUSE_BETWEEN_MOVES = 2300
    private val PAUSE_BETWEN_MOVES_EXTRA_PER_WORD = 200

    private var autoplay_active = true

    inner class AutoPlayRunnable : Runnable {

        override fun run() {
            while (autoplay_active && game.actMove.hasNextMove()) {

                runOnUiThread {
                    val next_mve = game.actMove.getnextMove(0)
                    game.jump(next_mve)
                }
                sleepWithProgress(calcTime())

            }
            SystemClock.sleep(PAUSE_FOR_LAST_MOVE.toLong())

            if (!interactionScope.is_in_noif_mode) {
                val next_intent = Intent(this@GoGamePlayerActivity, GobanDroidTVActivity::class.java)

                if (autoplay_active) {
                    startActivity(next_intent)
                    finish()
                }
            } else {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        go_board.setOnKeyListener(this)
        go_board.do_actpos_highlight = false

    }

    override fun onStart() {
        if (autoplay_active) {
            Thread(AutoPlayRunnable()).start()
        }
        super.onStart()
    }

    private val mTimerProgressRunnable = Runnable { setSupportProgress((Window.PROGRESS_START + progress_to_display * (Window.PROGRESS_END - Window.PROGRESS_START)).toInt()); }

    var progress_to_display = 0.5f

    /**
     * time in ms

     * @param time
     */
    private fun sleepWithProgress(time: Int) {
        val start_time = System.currentTimeMillis()

        while (System.currentTimeMillis() < start_time + time) {
            SystemClock.sleep(100)
            progress_to_display = 1f - (System.currentTimeMillis() - start_time + 1).toFloat() / time
            runOnUiThread(mTimerProgressRunnable)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menuInflater.inflate(R.menu.ingame_review, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onStop() {
        autoplay_active = false
        super.onStop()
    }

    override val gameExtraFragment = CommentAndNowPlayingFragment()

    public override fun doMoveWithUIFeedback(cell: Cell?)
            // we want the user not to be able to edit in review mode
            = VALID

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {

        if (event.action == KeyEvent.ACTION_DOWN)
            when (keyCode) {
                KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                    SwitchModeHelper.startGame(this, InteractionScope.Mode.REVIEW)
                    return true
                }

                KeyEvent.KEYCODE_MEDIA_PREVIOUS, KeyEvent.KEYCODE_DPAD_LEFT -> {
                    if (game.canUndo()) {
                        game.undo()
                    }

                    return true
                }

                KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_MEDIA_NEXT -> {
                    GameForwardAlert.showIfNeeded(this, game)
                    return true
                }

                KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN -> return false
            }
        return super.onKey(v, keyCode, event)
    }

    override fun isAsk4QuitEnabled() = false

    fun countWords(sentence: String) = if (interactionScope.is_in_noif_mode) {
        0
    } else {
        sentence.split(" ").size
    }

    private fun calcTime() = PAUSE_BETWEEN_MOVES + PAUSE_BETWEN_MOVES_EXTRA_PER_WORD * countWords(game.actMove.comment)

    override fun onTouch(v: View, event: MotionEvent) = true // this is a player - we do not want interaction

}
