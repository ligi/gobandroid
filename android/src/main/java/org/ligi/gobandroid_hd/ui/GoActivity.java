/**
 * gobandroid 
 * by Marcus -Ligi- Bueschleb 
 * http://ligi.de
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as 
 * published by the Free Software Foundation; 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. 
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 **/

package org.ligi.gobandroid_hd.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

import org.ligi.axt.AXT;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoMove;
import org.ligi.gobandroid_hd.logic.sgf.SGFWriter;
import org.ligi.gobandroid_hd.ui.alerts.GameInfoAlert;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.fragments.DefaultGameExtrasFragment;
import org.ligi.gobandroid_hd.ui.fragments.ZoomGameExtrasFragment;
import org.ligi.gobandroid_hd.ui.recording.SaveSGFDialog;
import org.ligi.gobandroid_hd.ui.review.BookmarkDialog;
import org.ligi.gobandroid_hd.ui.scoring.GameScoringActivity;
import org.ligi.gobandroid_hd.ui.share.ShareSGFDialog;
import org.ligi.tracedroid.logging.Log;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Activity for a Go Game
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         <p/>
 *         License: This software is licensed with GPLv3
 */

public class GoActivity extends GobandroidFragmentActivity implements OnTouchListener, OnKeyListener, GoGame.GoGameChangeListener {

    public ZoomGameExtrasFragment myZoomFragment;
    public GoSoundManager sound_man;
    private GoBoardViewHD go_board = null;
    private Toast info_toast = null;
    private Fragment actFragment;
    private InteractionScope interaction_scope;
    private int last_processed_move_change_num = 0;

    private GoMove last_accept;

    public Fragment getGameExtraFragment() {

        return new DefaultGameExtrasFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoPrefs.init(this); // TODO remove legacy

        setContentView(R.layout.game);


        if (!App.isTesting) {
            // if there where stacktraces collected -> give the user the option to send them
            TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this);
        }

        interaction_scope = getApp().getInteractionScope();
        this.getSupportActionBar().setHomeButtonEnabled(true);

        AXT.at(this).disableRotation();

        if (getSettings().isWakeLockEnabled()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        if (getGame() == null) { // cannot do anything without a game
            Log.w("finish()ing " + this + " cuz getGame()==null");
            finish();
            return;
        }

        if (sound_man == null) {
            sound_man = new GoSoundManager(this);
        }

        View customNav = new CustomActionBar(this);

        FragmentTransaction fragmentTransAction = this.getSupportFragmentManager().beginTransaction();

        fragmentTransAction.add(R.id.game_extra_container, getGameExtraFragment()).commit();

        // this.setContentView(R.layout.game);
        getSupportActionBar().setCustomView(customNav);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        customNav.setFocusable(false);

        createInfoToast();

        setupBoard();

        game2ui();
        getZoomFragment();
    }

    @SuppressLint("ShowToast")
    // this is correct - we do not want to show the toast at this stage
    private void createInfoToast() {
        info_toast = Toast.makeText(this.getBaseContext(), "", Toast.LENGTH_LONG);
    }

    /**
     * find the go board widget and set up some properties
     */
    private void setupBoard() {

        go_board = (GoBoardViewHD) findViewById(R.id.go_board);

        if (go_board == null) {
            Log.w("requesting board and none there");
            return; // had an NPE here - TODO figure out why exactly and if this
            // fix has some disadvantage
        }

        go_board.setOnTouchListener(this);
        go_board.setOnKeyListener(this);
        go_board.move_stone_mode = false;
    }

    @Override
    public void onGoGameChange() {
        Log.i("onGoGameChange in GoActivity");
        if (getGame().getActMove().getMovePos() > last_processed_move_change_num) {
            if (getGame().isBlackToMove()) {
                sound_man.playSound(GoSoundManager.SOUND_PLACE1);
            } else {
                sound_man.playSound(GoSoundManager.SOUND_PLACE2);
            }
        }
        last_processed_move_change_num = getGame().getActMove().getMovePos();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getApp().getInteractionScope().getTouchPosition() < 0) {
                    setFragment(getGameExtraFragment());
                }
            }
        });

        game2ui();
    }

    /**
     * set some preferences on the go board - intended to be called in onResume
     */
    private void setBoardPreferences() {
        if (go_board == null) {
            Log.w("setBoardPreferences() called with go_board==null - means setupBoard() was propably not called - skipping to not FC");
            return;
        }

        go_board.do_legend = getSettings().isLegendEnabled();
        go_board.legend_sgf_mode = getSettings().isSGFLegendEnabled();
        go_board.setGridEmboss(getSettings().isGridEmbossEnabled());
    }

    public boolean isBoardFocusWanted() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isBoardFocusWanted()) {
            go_board.setFocusableInTouchMode(true);
            go_board.setFocusable(true);
            go_board.requestFocus();
        } else {
            go_board.setFocusableInTouchMode(false);
            go_board.setFocusable(false);
        }
        setBoardPreferences();

        if (getGame() == null) {
            Log.w("we do not have a game in onStart of a GoGame activity - thats crazy!");
        } else {
            getGame().addGoGameChangeListener(this);
        }
    }

    public ZoomGameExtrasFragment getZoomFragment() {
        if (myZoomFragment == null) {
            myZoomFragment = new ZoomGameExtrasFragment(true);
        }
        return myZoomFragment;
    }

    @Override
    public boolean doFullScreen() {
        return getSettings().isFullscreenEnabled() | getResources().getBoolean(R.bool.force_fullscreen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.ingame_common, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {        /*
         * case R.id.menu_game_switchmode: new SwitchModeDialog(this).show();
		 * return true;
		 */

            case R.id.menu_game_info:
                new GameInfoAlert(this, getGame()).show();
                return true;

            case R.id.menu_game_undo:
                if (!getGame().canUndo()) {
                    break;
                }

                requestUndo();
                return true;

            case R.id.menu_game_pass:
                getGame().pass();
                getGame().notifyGameChange();

                return true;

			/*
             * case R.id.menu_game_results: new GameResultsAlert(this,
			 * getGame()).show(); return true;
			 */
            case R.id.menu_write_sgf:
                new SaveSGFDialog(this).show();
                return true;

            /*
            case R.id.menu_game_invite:
                getGame().setCloudDefs(null, null);
                new UploadGameToCloudEndpointsWithSend(this, "private_invite").execute();
                return true;
            */

            case R.id.menu_bookmark:
                new BookmarkDialog(this).show();
                return true;

            case R.id.menu_game_share:
                //new ShareAsAttachmentDialog(this).show();
                new ShareSGFDialog(this).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void switchToCounting() {

        getApp().getInteractionScope().setMode(InteractionScope.MODE_COUNT);
        startActivity(new Intent(this, GameScoringActivity.class));
        finish();

    }

    /**
     * control whether we want to ask the user - different in modes
     *
     * @return
     */
    public boolean isAsk4QuitEnabled() {
        return true;
    }

    private void shutdown(boolean toHome) {
        // sound_man.playSound(GoSoundManager.SOUND_END);
        getGame().getGoMover().stop();
        finish();

        if (toHome) {
            // startActivity(new Intent(this, gobandroid.class));
        }
    }

    public void quit(final boolean toHome) {
        if (!isAsk4QuitEnabled()) {
            shutdown(toHome);
            return;
        }

        new AlertDialog.Builder(this).setTitle(R.string.end_game_quesstion_title).setMessage(R.string.quit_confirm).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                shutdown(toHome);
            }
        }).setCancelable(true).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                quit(false);
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * show a the info toast with a specified text from a resource ID
     *
     * @param resId
     */
    protected void showInfoToast(int resId) {
        // info_toast.cancel();
        info_toast.setText(resId);
        info_toast.show();
    }

    protected byte doMoveWithUIFeedback(byte x, byte y) {

        byte res = getGame().do_move(x, y);
        switch (res) {
            case GoGame.MOVE_INVALID_IS_KO:
                showInfoToast(R.string.invalid_move_ko);
                break;
            case GoGame.MOVE_INVALID_CELL_NO_LIBERTIES:
                showInfoToast(R.string.invalid_move_no_liberties);
                break;
        }
        return res;
    }

    public void game2ui() {
        go_board.postInvalidate();
        refreshZoomFragment();
    }

    public void setFragment(Fragment newFragment) {

        if (actFragment == newFragment) {
            // GoFrag same same no need to do a thing here
            return;
        }

        FragmentTransaction fragmentTransAction = getSupportFragmentManager().beginTransaction();


        if (actFragment != null) {
            fragmentTransAction.remove(actFragment);
        }

        fragmentTransAction.replace(R.id.game_extra_container, newFragment);

        fragmentTransAction.commit();

        actFragment = newFragment;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Log.i("touch in GoActivity");
        if (event.getAction() == MotionEvent.ACTION_UP) {
            setFragment(getGameExtraFragment());
            if (getResources().getBoolean(R.bool.small)) {
                this.getSupportActionBar().show();
            }

        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setFragment(getZoomFragment());

            // for very small devices we want to hide the ActionBar to actually
            // see something in the Zoom-Fragment
            if (getResources().getBoolean(R.bool.small)) {
                this.getSupportActionBar().hide();
            }

            if (getGame().isBlackToMove()) {
                sound_man.playSound(GoSoundManager.SOUND_PICKUP1);
            } else {
                sound_man.playSound(GoSoundManager.SOUND_PICKUP2);
            }
        }

        if (!getGame().getGoMover().isReady()) {
            showInfoToast(R.string.wait_gnugo);
        } else if (getGame().getGoMover().isMoversMove()) {
            showInfoToast(R.string.not_your_turn);
        } else
        // if (!getSlidingMenu().is)
        {
            doTouch(event);
        }

        // refreshZoomFragment();
        return true;
    }

    public boolean isLastMoveAccepted() {

        if (last_accept == null) {
            return false;
        }

        return (last_accept.getMovePos() == getGame().getActMove().getMovePos());
    }

    @Override
    public void onPause() {

        go_board.move_stone_mode = false;

        /*
        TODO dismiss ProgressDialog from upload when needed

        if (pd != null)
            pd.dismiss();
          */
        if (getGame() == null) {
            Log.w("we do not have a game (anymore) in onStop of a GoGame activity - thats crazy!");
        } else {
            getGame().removeGoGameChangeListener(this);
        }

        if (doAutosave()) {
            try {
                File f = new File(getSettings().getSGFSavePath() + "/autosave.sgf");
                f.createNewFile();

                FileWriter sgf_writer = new FileWriter(f);

                BufferedWriter out = new BufferedWriter(sgf_writer);

                out.write(SGFWriter.game2sgf(getGame()));
                out.close();
                sgf_writer.close();

            } catch (IOException e) {
                Log.i("" + e);
            }
        }
        super.onPause();
    }

    public boolean doAutosave() {
        return false;
    }

    public void doTouch(MotionEvent event) {

        // calculate position on the field by position on the touchscreen

        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            interaction_scope.setTouchPosition(getBoard().pixel2boardPos(event.getX(), event.getY()));
        } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            interaction_scope.setTouchPosition(-1);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {

            if (go_board.move_stone_mode) {
                // TODO check if this is an illegal move ( e.g. in variants )

                if (getGame().getVisualBoard().isCellFree(interaction_scope.getTouchX(), interaction_scope.getTouchY())) {
                    getGame().getActMove().setXY((byte) interaction_scope.getTouchX(), (byte) interaction_scope.getTouchY());
                    getGame().getActMove().setDidCaptures(true); // TODO check
                    // if we
                    // harm sth
                    // with that
                    getGame().refreshBoards();

                }
                go_board.move_stone_mode = false; // moving of stone done
            } else if ((getGame().getActMove().getX() == interaction_scope.getTouchX()) && (getGame().getActMove().getY() == interaction_scope.getTouchY())) {
                initializeStoneMove();
            } else {
                doMoveWithUIFeedback((byte) interaction_scope.getTouchX(), (byte) interaction_scope.getTouchY());
            }

            interaction_scope.setTouchPosition(-1);

        }

        getGame().notifyGameChange();
    }


    public void initializeStoneMove() {

        if (getGame().getGoMover().isPlayingInThisGame()) // dont allow with a
        // mover
        {
            return;
        }

        if (go_board.move_stone_mode) // already in the mode
        {
            return; // -> do nothing
        }

        go_board.move_stone_mode = true;

        // TODO check if we only want this in certain modes
        if (GoPrefs.isAnnounceMoveActive()) {

            new AlertDialog.Builder(this).setMessage(R.string.hint_stone_move).setPositiveButton(R.string.ok,

                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            GoPrefs.setAnnounceMoveActive(false);
                        }
                    }).show();
        }
    }

    public boolean doAskToKeepVariant() {
        return GoPrefs.isAskVariantEnabled() && getApp().getInteractionScope().ask_variant_session;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    go_board.prepare_keyinput();
                    if (interaction_scope.getTouchY() > 0) {
                        interaction_scope.touch_position -= getGame().getSize();
                    } else {
                        return false;
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_LEFT:
                    go_board.prepare_keyinput();
                    if (interaction_scope.getTouchX() > 0) {
                        interaction_scope.touch_position--;
                    } else {
                        return false;
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_DOWN:
                    go_board.prepare_keyinput();
                    if (interaction_scope.getTouchY() < getGame().getVisualBoard().getSize() - 1) {
                        interaction_scope.touch_position += getGame().getSize();
                    } else {
                        return false;
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    go_board.prepare_keyinput();
                    if (interaction_scope.getTouchX() < getGame().getVisualBoard().getSize() - 1) {
                        interaction_scope.touch_position++;
                    } else {
                        return false;
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_CENTER:
                    doMoveWithUIFeedback((byte) interaction_scope.getTouchX(), (byte) interaction_scope.getTouchY());
                    break;

                default:

                    return false;

            }

            go_board.postInvalidate();
            refreshZoomFragment();
            return true;
        }
        return false;
    }

    public void refreshZoomFragment() {
        Log.i("refreshZoomFragment()" + getZoomFragment().getBoard() + " " + myZoomFragment.getBoard());
        if (getZoomFragment().getBoard() == null) // nothing we can do
        {
            return;
        }

        if (myZoomFragment.getBoard() != null) {
            myZoomFragment.getBoard().postInvalidate();
        }
    }

    public GoBoardViewHD getBoard() {
        if (go_board == null) {
            setupBoard();
        }
        return go_board;
    }

    public void requestUndo() {

        go_board.move_stone_mode = false;
        if (doAskToKeepVariant()) {
            new UndoWithVariationDialog(this).show();
        } else {
            getGame().undo(GoPrefs.isKeepVariantEnabled());
        }
    }

}