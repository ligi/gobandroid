package org.ligi.gobandroid_hd.ui.recording;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.WindowManager;

import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.events.GameChangedEvent;
import org.ligi.gobandroid_hd.ui.GoActivity;

/**
 * Activity to record a Game - or play on one device
 */
public class GameRecordActivity extends GoActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interactionScope.setMode(InteractionScope.Mode.RECORD);
        // TODO the next line works but needs investigation - i thought more of
        // getBoard().requestFocus(); - but that was not working ..
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        try {
            final boolean pass_avail = !getGame().isFinished();

            menu.findItem(R.id.menu_game_pass).setVisible(pass_avail);

            final boolean undo_avail = getGame().canUndo();

            menu.findItem(R.id.menu_game_undo).setVisible(undo_avail);

        } catch (NullPointerException ignored) {
        } // we do not care when they do not exist

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.ingame_record, menu);
        return super.onCreateOptionsMenu(menu);

    }

    public boolean doAutoSave() {
        return true;
    }

    @Override
    public void onGameChanged(GameChangedEvent gameChangedEvent) {
        super.onGameChanged(gameChangedEvent);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                supportInvalidateOptionsMenu();

                final boolean switch_to_count = getGame().isFinished();

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

}
