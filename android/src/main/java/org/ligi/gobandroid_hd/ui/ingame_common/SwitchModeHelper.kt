package org.ligi.gobandroid_hd.ui.ingame_common

import android.content.Context
import android.content.Intent
import org.ligi.gobandroid_hd.InteractionScope.Mode
import org.ligi.gobandroid_hd.InteractionScope.Mode.*
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity
import org.ligi.gobandroid_hd.ui.editing.EditGameActivity
import org.ligi.gobandroid_hd.ui.game_setup.GoSetupActivity
import org.ligi.gobandroid_hd.ui.gnugo.PlayAgainstGnuGoActivity
import org.ligi.gobandroid_hd.ui.recording.GameRecordActivity
import org.ligi.gobandroid_hd.ui.review.GameReviewActivity
import org.ligi.gobandroid_hd.ui.review.GoGamePlayerActivity
import org.ligi.gobandroid_hd.ui.scoring.GameScoringActivity
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoActivity

object SwitchModeHelper {

    fun getIntentByMode(ctx: Context, mode: Mode): Intent? {
        when (mode) {

            EDIT -> return Intent(ctx, EditGameActivity::class.java)

            RECORD -> return Intent(ctx, GameRecordActivity::class.java)

            REVIEW -> return Intent(ctx, GameReviewActivity::class.java)

            TSUMEGO -> return Intent(ctx, TsumegoActivity::class.java)

            TELEVIZE -> return Intent(ctx, GoGamePlayerActivity::class.java)

            COUNT -> return Intent(ctx, GameScoringActivity::class.java)

            GNUGO -> return Intent(ctx, PlayAgainstGnuGoActivity::class.java)

            SETUP -> return Intent(ctx, GoSetupActivity::class.java)

            else -> return null
        }
    }

    /**
     * @param activity - context
     * *
     * @param mode     - new mode
     */
    fun startGame(activity: GobandroidFragmentActivity, mode: Mode) {
        activity.interactionScope.mode = mode
        activity.startActivity(getIntentByMode(activity, mode))
    }

    fun startGameWithCorrectMode(activity: GobandroidFragmentActivity) {
        startGame(activity, activity.interactionScope.mode)
    }

}
