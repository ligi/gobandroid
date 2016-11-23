package org.ligi.gobandroid_hd.ui.review

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.Menu
import android.view.MotionEvent
import kotlinx.android.synthetic.main.game.*
import org.ligi.gobandroid_hd.InteractionScope
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.Cell
import org.ligi.gobandroid_hd.logic.GoGame.MoveStatus
import org.ligi.gobandroid_hd.logic.GoGame.MoveStatus.VALID
import org.ligi.gobandroid_hd.ui.GoActivity
import org.ligi.gobandroid_hd.ui.alerts.GameForwardAlert
import org.ligi.gobandroid_hd.ui.fragments.NavigationAndCommentFragment
import org.ligi.gobandroid_hd.ui.ingame_common.SwitchModeHelper
import org.ligi.tracedroid.logging.Log

class GameReviewActivity : GoActivity() {

    override val gameExtraFragment: Fragment
        get() = NavigationAndCommentFragment()

    public override fun doMoveWithUIFeedback(cell: Cell?): MoveStatus {
        // we want the user not to be able to edit in review mode
        return VALID
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //getBoard().setOnKeyListener(this);
        go_board.do_actpos_highlight = false
    }

    public override fun onResume() {
        super.onResume()
    }

    override val isBoardFocusWanted: Boolean
        get() = false

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        Log.i("", "KeyEvent" + event.keyCode)
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {


                KeyEvent.KEYCODE_BOOKMARK -> {
                    Log.i("", "Focus:" + window.currentFocus!!)
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
                    //case KeyEvent.KEYCODE_MEDIA_:
                    GameForwardAlert.showIfNeeded(this, game)
                    return true
                }

                KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN -> return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    /*
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (event.getAction() == KeyEvent.ACTION_DOWN)
                switch (keyCode) {

                    case KeyEvent.KEYCODE_BOOKMARK:
                        new BookmarkDialog(this).show();
                        return true;

                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        SwitchModeHelper.startGame(this, InteractionScope.MODE_TELEVIZE);
                        return true;


                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        if (!getGame().canUndo())
                            return true;
                        getGame().undo();
                        return true;

                    case KeyEvent.KEYCODE_FORWARD:
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        GameForwardAlert.show(this, getGame());
                        return true;

                    case KeyEvent.KEYCODE_DPAD_UP:
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        return false;

                }
            return super.onKey(v, keyCode, event);
        }

    */
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
