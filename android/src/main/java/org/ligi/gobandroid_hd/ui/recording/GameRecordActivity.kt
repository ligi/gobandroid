package org.ligi.gobandroid_hd.ui.recording

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.Menu
import android.view.WindowManager

import org.ligi.gobandroid_hd.InteractionScope
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.events.GameChangedEvent
import org.ligi.gobandroid_hd.ui.GoActivity

/**
 * Activity to record a Game - or play on one device
 */
class GameRecordActivity : GoActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        interactionScope.mode = InteractionScope.Mode.RECORD
        // TODO the next line works but needs investigation - i thought more of
        // getBoard().requestFocus(); - but that was not working ..
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)

        try {
            val pass_avail = !game.isFinished

            menu.findItem(R.id.menu_game_pass).isVisible = pass_avail

            val undo_avail = game.canUndo()

            menu.findItem(R.id.menu_game_undo).isVisible = undo_avail

        } catch (ignored: NullPointerException) {
        }
        // we do not care when they do not exist

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menuInflater.inflate(R.menu.ingame_record, menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun doAutoSave(): Boolean {
        return true
    }

    override fun onGameChanged(gameChangedEvent: GameChangedEvent) {
        super.onGameChanged(gameChangedEvent)
        runOnUiThread {
            supportInvalidateOptionsMenu()

            val switch_to_count = game.isFinished

            if (switch_to_count) {
                switchToCounting()
            }
        }
    }

    override val gameExtraFragment: Fragment
        get() = RecordingGameExtrasFragment()

}
