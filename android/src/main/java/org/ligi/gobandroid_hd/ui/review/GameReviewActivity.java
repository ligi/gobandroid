package org.ligi.gobandroid_hd.ui.review;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;

import org.ligi.gobandroid_hd.CloudHooks;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.Cell;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.alerts.GameForwardAlert;
import org.ligi.gobandroid_hd.ui.fragments.NavigationAndCommentFragment;
import org.ligi.gobandroid_hd.ui.ingame_common.SwitchModeHelper;
import org.ligi.tracedroid.logging.Log;

public class GameReviewActivity extends GoActivity {

    public Fragment getGameExtraFragment() {
        return new NavigationAndCommentFragment();
    }

    @Override
    public byte doMoveWithUIFeedback(Cell cell) {
        // we want the user not to be able to edit in review mode
        return GoGame.MOVE_VALID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getBoard().setOnKeyListener(this);
        getBoard().do_actpos_highlight = false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean isBoardFocusWanted() {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("", "KeyEvent" + event.getKeyCode());
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {


                case KeyEvent.KEYCODE_BOOKMARK:
                    Log.i("", "Focus:" + getWindow().getCurrentFocus());
                    //new BookmarkDialog(this).show();
                    return true;

                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    SwitchModeHelper.startGame(this, InteractionScope.MODE_TELEVIZE);
                    return true;


                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    if (!getGame().canUndo()) {
                        return true;
                    }
                    getGame().undo();
                    return true;

                case KeyEvent.KEYCODE_FORWARD:
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    //case KeyEvent.KEYCODE_MEDIA_:
                    GameForwardAlert.showIfNeeded(this, getGame());
                    return true;

                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    return false;

            }
        }
        return super.onKeyDown(keyCode, event);
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
    @Override
    public void doTouch(MotionEvent event) {
        eventForZoomBoard(event);
    }

    @Override
    public void quit(final boolean toHome) {
        new EndReviewDialog(this).show();
    }

    @Override
    public void initializeStoneMove() {
        // we do not want this behaviour so we override and do nothing
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.ingame_review, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private String lastMomentFname;

    @Override
    public void onGoGameChange() {
        super.onGoGameChange();

        if (!getGame().getActMove().hasNextMove()) {

            if (lastMomentFname == null || !lastMomentFname.equals(getGame().getMetaData().getFileName())) {
                // TODO make sure it is not just the end of a variation
                CloudHooks.uploadGameAndShareMoment(this, getGame(), "reviewed_game");
                lastMomentFname = getGame().getMetaData().getFileName();
            }

        }
    }

}
