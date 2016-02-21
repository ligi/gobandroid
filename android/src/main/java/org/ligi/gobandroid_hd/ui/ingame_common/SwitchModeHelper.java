package org.ligi.gobandroid_hd.ui.ingame_common;

import android.content.Context;
import android.content.Intent;

import org.ligi.gobandroid_hd.InteractionScope.Mode;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.editing.EditGameActivity;
import org.ligi.gobandroid_hd.ui.game_setup.GoSetupActivity;
import org.ligi.gobandroid_hd.ui.gnugo.PlayAgainstGnuGoActivity;
import org.ligi.gobandroid_hd.ui.recording.GameRecordActivity;
import org.ligi.gobandroid_hd.ui.review.GameReviewActivity;
import org.ligi.gobandroid_hd.ui.review.GoGamePlayerActivity;
import org.ligi.gobandroid_hd.ui.scoring.GameScoringActivity;
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoActivity;

public class SwitchModeHelper {

    public static Intent getIntentByMode(Context ctx, Mode mode) {
        switch (mode) {

            case EDIT:
                return new Intent(ctx, EditGameActivity.class);

            case RECORD:
                return new Intent(ctx, GameRecordActivity.class);

            case REVIEW:
                return new Intent(ctx, GameReviewActivity.class);

            case TSUMEGO:
                return new Intent(ctx, TsumegoActivity.class);

            case TELEVIZE:
                return new Intent(ctx, GoGamePlayerActivity.class);

            case COUNT:
                return new Intent(ctx, GameScoringActivity.class);

            case GNUGO:
                return new Intent(ctx, PlayAgainstGnuGoActivity.class);

            case SETUP:
                return new Intent(ctx, GoSetupActivity.class);

            default:
                return null;
        }
    }

    /**
     * @param activity - context
     * @param mode     - new mode
     */
    public static void startGame(GobandroidFragmentActivity activity, Mode mode) {
        activity.interactionScope.setMode(mode);
        activity.startActivity(getIntentByMode(activity, mode));
    }

    public static void startGameWithCorrectMode(GobandroidFragmentActivity activity) {
        startGame(activity, activity.interactionScope.getMode());
    }

}
