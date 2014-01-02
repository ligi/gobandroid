package org.ligi.gobandroid_hd.ui.recording;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import org.ligi.gobandroid_hd.CloudHooks;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.GoPrefs;
import org.ligi.tracedroid.logging.Log;


/**
 * Activity to record a Game - or play on one device
 *
 * @author ligi
 */
public class GameRecordActivity extends GoActivity implements GoGameChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApp().getInteractionScope().setMode(InteractionScope.MODE_RECORD);
        // TODO the next line works but needs investigation - i thought more of
        // getBoard().requestFocus(); - but that was not working ..
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public byte doMoveWithUIFeedback(byte x, byte y) {

        if (isCloudMove()) {
            Log.i("showing info toast");
            showInfoToast(R.string.not_your_turn);
            return GoGame.MOVE_INVALID;
        }

        byte res = super.doMoveWithUIFeedback(x, y);
        if (res == GoGame.MOVE_VALID) {
            if (getGame().getActMove().hasNextMove()) {
                getGame().jump(getGame().getActMove().getnextMove(0));
            }
        }

        getGame().notifyGameChange();
        return res;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {        /*
         * case R.id.menu_game_switchmode: new SwitchModeDialog(this).show();
		 * return true;
		 */

            case R.id.menu_game_accept:
                CloudHooks.uploadGame(this, getGame(), null);
                acceptCloudMove();
                onGoGameChange();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        try {
            boolean pass_avail = !getGame().isFinished();

            if (isCloudGame() && isCloudMove()) {
                pass_avail = false;
            }

            if (isCloudGame() && isLastMoveAccepted()) {
                pass_avail = false;
            }

            menu.findItem(R.id.menu_game_pass).setVisible(pass_avail);

			/*
             * menu.findItem(R.id.menu_game_results).setVisible(
			 * getGame().isFinished());
			 */

            boolean undo_avail = getGame().canUndo();

            if (isCloudGame() && getGame().getCloudRole().equals("s")) {
                undo_avail = false;
            }

            if (isCloudGame() && !isCloudMove()) {
                undo_avail = false;
            }

            if (isCloudGame() && isLastMoveAccepted()) {
                undo_avail = false;
            }

            menu.findItem(R.id.menu_game_undo).setVisible(undo_avail);
            // TODO works but weird logic
            menu.findItem(R.id.menu_game_accept).setVisible(isCloudGame() && undo_avail);

            /*
            menu.findItem(R.id.menu_game_invite).setVisible(getSettings().isBetaWanted() && !GCMRegistrar.getRegistrationId(this).equals(""));
            */
        } catch (NullPointerException e) {
        } // we do not care when they do not exist

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.ingame_record, menu);
        return super.onCreateOptionsMenu(menu);

    }

    public boolean doAutosave() {
        return true;
    }

    @Override
    public void onGoGameChange() {
        super.onGoGameChange();
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                invalidateOptionsMenu();

                boolean switch_to_count = getGame().isFinished();

                if (isCloudGame() && (isCloudMove() && !isLastMoveAccepted())) {
                    switch_to_count = false;
                }

                if (switch_to_count) {
                    switchToCounting();
                }

            }

        });

    }

    @Override
    public Fragment getGameExtraFragment() {
        return new RecordingGameExtrasFragment();
    }

    @Override
    public void requestUndo() {
        if (isCloudGame()) {
            getGame().undo(GoPrefs.isKeepVariantEnabled());
        } else {
            super.requestUndo();
        }
    }

}
