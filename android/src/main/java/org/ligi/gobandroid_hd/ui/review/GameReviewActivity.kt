package org.ligi.gobandroid_hd.ui.review

import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MotionEvent
import kotlinx.android.synthetic.main.game.*
import org.ligi.gobandroid_hd.InteractionScope
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.Cell
import org.ligi.gobandroid_hd.logic.GoGame.MoveStatus.VALID
import org.ligi.gobandroid_hd.ui.GoActivity
import org.ligi.gobandroid_hd.ui.alerts.GameForwardAlert
import org.ligi.gobandroid_hd.ui.fragments.NavigationAndCommentFragment
import org.ligi.gobandroid_hd.ui.ingame_common.SwitchModeHelper

class GameReviewActivity : GoActivity() {

    override val gameExtraFragment = NavigationAndCommentFragment()

    // we want the user not to be able to edit in review mode
    public override fun doMoveWithUIFeedback(cell: Cell?) = VALID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        go_board.do_actpos_highlight = false
    }

    override val isBoardFocusWanted = false

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {

                KeyEvent.KEYCODE_BOOKMARK -> {
                    //new BookmarkDialog(this).show();
                    return true
                }

                KeyEvent.KEYCODE_MEDIA_PLAY -> {
                    SwitchModeHelper.startGame(this, InteractionScope.Mode.TELEVIZE)
                    return true
                }


                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                    if (!game.canUndo()) {
                        return true
                    }
                    game.undo()
                    return true
                }

                KeyEvent.KEYCODE_FORWARD, KeyEvent.KEYCODE_MEDIA_NEXT -> {
                    GameForwardAlert.showIfNeeded(this, game)
                    return true
                }

                KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN -> return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun doTouch(event: MotionEvent) {
        eventForZoomBoard(event)
    }

    override fun quit(toHome: Boolean) {
        EndReviewDialog(this).show()
    }

    override fun initializeStoneMove() {
        // we do not want this behaviour so we override and do nothing
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menuInflater.inflate(R.menu.ingame_review, menu)
        return super.onCreateOptionsMenu(menu)
    }

}
