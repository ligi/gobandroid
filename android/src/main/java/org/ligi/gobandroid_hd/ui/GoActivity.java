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
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintManager;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.ligi.axt.AXT;
import org.ligi.axt.listeners.DialogDiscardingOnClickListener;
import org.ligi.gobandroid_hd.BuildConfig;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.events.GameChangedEvent;
import org.ligi.gobandroid_hd.events.OptionsItemClickedEvent;
import org.ligi.gobandroid_hd.logic.Cell;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.StatelessBoardCell;
import org.ligi.gobandroid_hd.logic.sgf.SGFWriter;
import org.ligi.gobandroid_hd.print.GoGamePrintDocumentAdapter;
import org.ligi.gobandroid_hd.ui.alerts.GameInfoDialog;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.fragments.DefaultGameExtrasFragment;
import org.ligi.gobandroid_hd.ui.recording.SaveSGFDialog;
import org.ligi.gobandroid_hd.ui.review.BookmarkDialog;
import org.ligi.gobandroid_hd.ui.scoring.GameScoringActivity;
import org.ligi.gobandroid_hd.ui.share.ShareSGFDialog;
import org.ligi.snackengage.SnackEngage;
import org.ligi.snackengage.conditions.AfterNumberOfOpportunities;
import org.ligi.snackengage.conditions.NeverAgainWhenClickedOnce;
import org.ligi.snackengage.conditions.locale.IsOneOfTheseLocales;
import org.ligi.snackengage.snacks.RateSnack;
import org.ligi.snackengage.snacks.TranslateSnack;
import org.ligi.tracedroid.logging.Log;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;
import static org.ligi.gobandroid_hd.logic.GoGame.MoveStatus.INVALID_NOT_ON_BOARD;
import static org.ligi.gobandroid_hd.ui.GoSoundManager.Sound.PICKUP1;
import static org.ligi.gobandroid_hd.ui.GoSoundManager.Sound.PICKUP2;
import static org.ligi.gobandroid_hd.ui.GoSoundManager.Sound.PLACE1;
import static org.ligi.gobandroid_hd.ui.GoSoundManager.Sound.PLACE2;

/**
 * Activity for a Go Game
 */
public class GoActivity extends GobandroidFragmentActivity implements OnTouchListener, OnKeyListener {

    public GoSoundManager sound_man;

    @BindView(R.id.go_board)
    GoBoardViewHD go_board;

    @BindView(R.id.zoom_board)
    GoBoardViewHD zoom_board;

    @BindView(R.id.game_extra_container)
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

        setContentView(R.layout.game);

        ButterKnife.bind(this);

        if (!BuildConfig.DEBUG) {
            // if there where stacktraces collected -> give the user the option to send them
            if (!TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this)) {
                SnackEngage.from(go_board)
                           .withSnack(new RateSnack().withConditions(new NeverAgainWhenClickedOnce(), new AfterNumberOfOpportunities(42)))
                           .withSnack(new TranslateSnack("https://www.transifex.com/ligi/gobandroid/").withConditions(new AfterNumberOfOpportunities(4),
                                                                                                                      new IsOneOfTheseLocales(Locale.KOREA,
                                                                                                                                              Locale.KOREAN),
                                                                                                                      new NeverAgainWhenClickedOnce()))
                           .build()
                           .engageWhenAppropriate();
            }
        }

        getSupportActionBar().setHomeButtonEnabled(true);

        AXT.at(this).disableRotation();

        if (GoPrefs.INSTANCE.isConstantLightWanted()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        if (getGame() == null) { // cannot do anything without a game
            Log.w("finish()ing " + this + " cuz getGame()==null");
            finish();
            return;
        }

        if (sound_man == null) {
            sound_man = new GoSoundManager(this, env);
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
        go_board.setMove_stone_mode(false);
    }


    /**
     * set some preferences on the go board - intended to be called in onResume
     */
    private void setBoardPreferences() {
        if (go_board == null) {
            Log.w("setBoardPreferences() called with go_board==null - means setupBoard() was propably not called - skipping to not FC");
            return;
        }

        go_board.setDo_legend(GoPrefs.INSTANCE.isLegendEnabled());
        go_board.setLegend_sgf_mode(GoPrefs.INSTANCE.isSGFLegendEnabled());
        go_board.setGridEmboss(GoPrefs.INSTANCE.isGridEmbossEnabled());
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


        getBus().register(this);
    }

    @Override
    public boolean doFullScreen() {
        return GoPrefs.INSTANCE.isFullscreenEnabled() | getResources().getBoolean(R.bool.force_fullscreen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.ingame_common, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        final MenuItem item = menu.findItem(R.id.menu_game_print);

        if (item != null) {
            item.setVisible(Build.VERSION.SDK_INT >= 19);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        getBus().post(new OptionsItemClickedEvent(item.getItemId()));
        switch (item.getItemId()) {

            case R.id.menu_game_print:
                doPrint();
                return true;

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

                getBus().post(GameChangedEvent.INSTANCE);

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

    @TargetApi(19)
    public void doPrint() {
        final PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        final String jobName = getString(R.string.app_name);
        printManager.print(jobName, new GoGamePrintDocumentAdapter(this, jobName), null);
    }

    public void switchToCounting() {
        interactionScope.setMode(InteractionScope.Mode.COUNT);
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

    protected GoGame.MoveStatus doMoveWithUIFeedback(Cell cell) {
        if (cell == null) {
            return INVALID_NOT_ON_BOARD;
        }

        final GoGame.MoveStatus res = getGame().do_move(cell);

        switch (res) {
            case INVALID_IS_KO:
            case INVALID_CELL_NO_LIBERTIES:
                showInfoToast(getToastForResult(res));
        }

        return res;
    }

    @StringRes
    private int getToastForResult(GoGame.MoveStatus res) {
        switch (res) {
            case INVALID_IS_KO:
                return R.string.invalid_move_ko;

            case INVALID_CELL_NO_LIBERTIES:
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
        interactionScope.setTouchCell(getBoard().pixel2cell(event.getX(), event.getY()));

        if (!getApp().isTesting()) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                gameExtrasContainer.setVisibility(View.VISIBLE);
                zoom_board.setVisibility(View.GONE);
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                gameExtrasContainer.setVisibility(View.GONE);
                zoom_board.setVisibility(View.VISIBLE);
            }
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
                sound_man.playSound(PICKUP1);
            } else {
                sound_man.playSound(PICKUP2);
            }
        }

        doTouch(event);

        return true;
    }

    @Override
    public void onPause() {

        go_board.setMove_stone_mode(false);

        if (doAutoSave()) {
            try {
                final File f = new File(env.getSGFSavePath() + "/autosave.sgf");
                f.createNewFile();

                FileWriter sgf_writer = new FileWriter(f);

                BufferedWriter out = new BufferedWriter(sgf_writer);

                out.write(SGFWriter.INSTANCE.game2sgf(getGame()));
                out.close();
                sgf_writer.close();

            } catch (IOException e) {
                Log.i("" + e);
            }
        }
        getBus().unregister(this);
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
                interactionScope.setTouchCell(getBoard().pixel2cell(event.getX(), event.getY()));
                break;

            case MotionEvent.ACTION_OUTSIDE:
                interactionScope.setTouchCell(null);
                break;

            case MotionEvent.ACTION_UP:

                if (go_board.getMove_stone_mode()) {
                    // TODO check if this is an illegal move ( e.g. in variants )

                    if (interactionScope.getTouchCell() != null && getGame().getVisualBoard().isCellFree(interactionScope.getTouchCell())) {
                        getGame().getActMove().setCell(interactionScope.getTouchCell());
                        getGame().getActMove().setDidCaptures(true); // TODO check if we harm sth with that
                        getGame().refreshBoards();
                    }
                    go_board.setMove_stone_mode(false); // moving of stone done
                } else if ((getGame().getActMove().isOnCell(interactionScope.getTouchCell()))) {
                    initializeStoneMove();
                } else {
                    doMoveWithUIFeedback(interactionScope.getTouchCell());
                }

                interactionScope.setTouchCell(null);
                break;
        }

        getBus().post(GameChangedEvent.INSTANCE);
    }


    public void initializeStoneMove() {

        if (go_board.getMove_stone_mode()) { // already in the mode
            return; // -> do nothing
        }

        go_board.setMove_stone_mode(true);

        // TODO check if we only want this in certain modes
        if (GoPrefs.INSTANCE.isAnnounceMoveActive()) {

            new AlertDialog.Builder(this).setMessage(R.string.hint_stone_move).setPositiveButton(R.string.ok,

                                                                                                 new DialogInterface.OnClickListener() {
                                                                                                     public void onClick(DialogInterface dialog,
                                                                                                                         int whichButton) {
                                                                                                         GoPrefs.INSTANCE.setAnnounceMoveActive(false);
                                                                                                     }
                                                                                                 }).show();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {

            final Cell ensuredTouchPosition;
            if (interactionScope.getTouchCell() == null) {
                ensuredTouchPosition = getGame().getStatelessGoBoard().getCell(0, 0);
            } else {
                ensuredTouchPosition = interactionScope.getTouchCell();
            }
            final StatelessBoardCell boardCell = getGame().getCalcBoard().getStatelessGoBoard().getCell(ensuredTouchPosition);

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (boardCell.getUp() != null) {
                        interactionScope.setTouchCell(boardCell.getUp());
                    } else {
                        return false;
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (boardCell.getLeft() != null) {
                        interactionScope.setTouchCell(boardCell.getLeft());
                    } else {
                        return false;
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (boardCell.getDown() != null) {
                        interactionScope.setTouchCell(boardCell.getDown());
                    } else {
                        return false;
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (boardCell.getRight() != null) {
                        interactionScope.setTouchCell(boardCell.getRight());
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
        return go_board;
    }

    public void requestUndo() {

        go_board.setMove_stone_mode(false);

        UndoWithVariationDialog.Companion.userInvokedUndo(this,interactionScope,getGame());
    }

    protected EventBus getBus() {
        return EventBus.getDefault();
    }

    @Subscribe
    public void onGameChanged(GameChangedEvent gameChangedEvent) {
        Log.i("onGoGameChange in GoActivity");
        if (getGame().getActMove().getMovePos() > last_processed_move_change_num) {
            if (getGame().isBlackToMove()) {
                sound_man.playSound(PLACE1);
            } else {
                sound_man.playSound(PLACE2);
            }
        }
        last_processed_move_change_num = getGame().getActMove().getMovePos();

        game2ui();
    }

    protected void notifyGoGameChange() {
        EventBus.getDefault().post(GameChangedEvent.INSTANCE);
    }
}