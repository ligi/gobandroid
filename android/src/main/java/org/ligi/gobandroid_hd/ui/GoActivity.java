/**
 * gobandroid
 * by Marcus -Ligi- Bueschleb
 * http://ligi.de
 * <p/>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 **/

package org.ligi.gobandroid_hd.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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
import org.ligi.axt.listeners.DialogDiscardingOnClickListener;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.Cell;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.StatelessBoardCell;
import org.ligi.gobandroid_hd.logic.sgf.SGFWriter;
import org.ligi.gobandroid_hd.ui.alerts.GameInfoDialog;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.fragments.DefaultGameExtrasFragment;
import org.ligi.gobandroid_hd.ui.recording.SaveSGFDialog;
import org.ligi.gobandroid_hd.ui.review.BookmarkDialog;
import org.ligi.gobandroid_hd.ui.scoring.GameScoringActivity;
import org.ligi.gobandroid_hd.ui.share.ShareSGFDialog;
import org.ligi.snackengage.SnackEngage;
import org.ligi.snackengage.snacks.DefaultRateSnack;
import org.ligi.tracedroid.logging.Log;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Activity for a Go Game
 */
public class GoActivity extends GobandroidFragmentActivity implements OnTouchListener, OnKeyListener, GoGame.GoGameChangeListener {

    public GoSoundManager sound_man;

    @Bind(R.id.go_board)
    GoBoardViewHD go_board;

    @Bind(R.id.zoom_board)
    GoBoardViewHD zoom_board;

    @Bind(R.id.game_extra_container)
    View gameExtrasContainer;

    private Toast info_toast = null;
    private Fragment actFragment;
    private int last_processed_move_change_num = 0;

    public Fragment getGameExtraFragment() {

        return new DefaultGameExtrasFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoPrefs.init(this); // TODO remove legacy

        setContentView(R.layout.game);

        ButterKnife.bind(this);

        if (!App.isTesting) {
            // if there where stacktraces collected -> give the user the option to send them
            if (!TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this)) {
                SnackEngage.from(go_board).withSnack(new DefaultRateSnack()).build().engageWhenAppropriate();
            }
        }

        getSupportActionBar().setHomeButtonEnabled(true);

        AXT.at(this).disableRotation();

        if (settings.isConstantLightWanted()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        if (getGame() == null) { // cannot do anything without a game
            Log.w("finish()ing " + this + " cuz getGame()==null");
            finish();
            return;
        }

        if (sound_man == null) {
            sound_man = new GoSoundManager(this, settings);
        }

        final View customNav = new CustomActionBar(this);

        final FragmentTransaction fragmentTransAction = getSupportFragmentManager().beginTransaction();
        fragmentTransAction.add(R.id.game_extra_container, getGameExtraFragment()).commit();
        getSupportFragmentManager().executePendingTransactions();


        getSupportActionBar().setCustomView(customNav);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        customNav.setFocusable(false);

        createInfoToast();

        setupBoard();

        game2ui();
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

        go_board.do_legend = settings.isLegendEnabled();
        go_board.legend_sgf_mode = settings.isSGFLegendEnabled();
        go_board.setGridEmboss(settings.isGridEmbossEnabled());
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

    @Override
    public boolean doFullScreen() {
        return settings.isFullscreenEnabled() | getResources().getBoolean(R.bool.force_fullscreen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.ingame_common, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_game_info:
                new GameInfoDialog(this, getGame()).show();
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

            case R.id.menu_write_sgf:
                new SaveSGFDialog(this).show();
                return true;

            case R.id.menu_bookmark:
                new BookmarkDialog(this).show();
                return true;

            case R.id.menu_game_share:
                //new ShareWithMultipleOptionsDialog(this).show();
                new ShareSGFDialog(this).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void switchToCounting() {

        interactionScope.setMode(InteractionScope.MODE_COUNT);
        startActivity(new Intent(this, GameScoringActivity.class));
        finish();

    }

    /**
     * @return true if we want to ask the user - different in modes
     */
    public boolean isAsk4QuitEnabled() {
        return true;
    }

    public void quit(final boolean toHome) {
        if (!isAsk4QuitEnabled()) {
            finish();
        } else {
            new AlertDialog.Builder(this).setTitle(R.string.end_game_quesstion_title)
                    .setMessage(R.string.quit_confirm)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }
                    })
                    .setCancelable(true)
                    .setNegativeButton(R.string.no, new DialogDiscardingOnClickListener())
                    .show();
        }
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
     */
    protected void showInfoToast(@StringRes int resId) {
        info_toast.setText(resId);
        info_toast.show();
    }

    protected byte doMoveWithUIFeedback(Cell cell) {
        if (cell == null) {
            return GoGame.MOVE_INVALID_NOT_ON_BOARD;
        }

        final byte res = getGame().do_move(cell);

        switch (res) {
            case GoGame.MOVE_INVALID_IS_KO:
            case GoGame.MOVE_INVALID_CELL_NO_LIBERTIES:
                showInfoToast(getToastForResult(res));
        }

        return res;
    }

    private int getToastForResult(byte res) {
        switch (res) {
            case GoGame.MOVE_INVALID_IS_KO:
                return R.string.invalid_move_ko;

            case GoGame.MOVE_INVALID_CELL_NO_LIBERTIES:
                return R.string.invalid_move_no_liberties;
        }

        throw (new RuntimeException("Illegal game result " + res));
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

        final FragmentTransaction fragmentTransAction = getSupportFragmentManager().beginTransaction();

        if (actFragment != null) {
            fragmentTransAction.remove(actFragment);
        }

        fragmentTransAction.replace(R.id.game_extra_container, newFragment);

        fragmentTransAction.commit();

        actFragment = newFragment;
    }

    protected void eventForZoomBoard(MotionEvent event) {
        interactionScope.setTouchPosition(getBoard().pixel2cell(event.getX(), event.getY()));

        if (event.getAction() == MotionEvent.ACTION_UP) {
            gameExtrasContainer.setVisibility(View.VISIBLE);
            zoom_board.setVisibility(View.GONE);
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            gameExtrasContainer.setVisibility(View.GONE);
            zoom_board.setVisibility(View.VISIBLE);
        }
        refreshZoomFragment();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        eventForZoomBoard(event);

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getResources().getBoolean(R.bool.small)) {
                getSupportActionBar().show();
            }

        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {

            // for very small devices we want to hide the ActionBar to actually
            // see something in the Zoom-Fragment
            if (getResources().getBoolean(R.bool.small)) {
                getSupportActionBar().hide();
            }

            if (getGame().isBlackToMove()) {
                sound_man.playSound(GoSoundManager.SOUND_PICKUP1);
            } else {
                sound_man.playSound(GoSoundManager.SOUND_PICKUP2);
            }
        }

        doTouch(event);

        return true;
    }

    @Override
    public void onPause() {

        go_board.move_stone_mode = false;

        if (getGame() == null) {
            Log.w("we do not have a game (anymore) in onStop of a GoGame activity - that's crazy!");
        } else {
            getGame().removeGoGameChangeListener(this);
        }

        if (doAutoSave()) {
            try {
                final File f = new File(settings.getSGFSavePath() + "/autosave.sgf");
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

    public boolean doAutoSave() {
        return false;
    }

    public void doTouch(MotionEvent event) {

        // calculate position on the field by position on the touchscreen

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                interactionScope.setTouchPosition(getBoard().pixel2cell(event.getX(), event.getY()));
                break;

            case MotionEvent.ACTION_OUTSIDE:
                interactionScope.setTouchPosition(null);
                break;

            case MotionEvent.ACTION_UP:

                if (go_board.move_stone_mode) {
                    // TODO check if this is an illegal move ( e.g. in variants )

                    if (interactionScope.getTouchCell() != null && getGame().getVisualBoard().isCellFree(interactionScope.getTouchCell())) {
                        getGame().getActMove().setCell(interactionScope.getTouchCell());
                        getGame().getActMove().setDidCaptures(true); // TODO check if we harm sth with that
                        getGame().refreshBoards();
                    }
                    go_board.move_stone_mode = false; // moving of stone done
                } else if ((getGame().getActMove().isOnCell(interactionScope.getTouchCell()))) {
                    initializeStoneMove();
                } else {
                    doMoveWithUIFeedback(interactionScope.getTouchCell());
                }

                interactionScope.setTouchPosition(null);
                break;
        }

        getGame().notifyGameChange();
    }


    public void initializeStoneMove() {

        if (go_board.move_stone_mode) { // already in the mode
            return; // -> do nothing
        }

        go_board.move_stone_mode = true;

        // TODO check if we only want this in certain modes
        if (GoPrefs.isAnnounceMoveActive()) {

            new AlertDialog.Builder(this).setMessage(R.string.hint_stone_move).setPositiveButton(R.string.ok,

                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            GoPrefs.setAnnounceMoveActive(false);
                        }
                    }).show();
        }
    }

    public boolean doAskToKeepVariant() {
        return GoPrefs.isAskVariantEnabled() && interactionScope.ask_variant_session;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final Cell ensuredTouchPosition = interactionScope.getEnsuredTouchPosition();
            final StatelessBoardCell boardCell = getGame().getCalcBoard().getStatelessGoBoard().getCell(ensuredTouchPosition);

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (boardCell.up != null) {
                        interactionScope.touch_position = boardCell.up;
                    } else {
                        return false;
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (boardCell.left != null) {
                        interactionScope.touch_position = boardCell.left;
                    } else {
                        return false;
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (boardCell.down != null) {
                        interactionScope.touch_position = boardCell.down;
                    } else {
                        return false;
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (boardCell.right != null) {
                        interactionScope.touch_position = boardCell.right;
                    } else {
                        return false;
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_CENTER:
                    doMoveWithUIFeedback(boardCell);
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
        zoom_board.postInvalidate();
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