package org.ligi.gobandroid_hd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.ligi.gobandroid_hd.GobandroidApp;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.gobandroid_hd.ui.GobandroidNotifications;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.online.UploadGameAndShareMoment;
import org.ligi.gobandroid_hd.ui.online.UserHandler;
import org.ligi.tracedroid.logging.Log;

import java.io.IOException;

/**
 * Created by ligi on 6/7/13.
 */
public class CloudHooks {

    public static void onApplicationCreation(GobandroidApp gobandroidApp) {
        UserHandler.syncUser(gobandroidApp);
    }

    public static void syncUser(GobandroidApp gobandroidApp) {
        UserHandler.syncUser(gobandroidApp);
    }

    public static void onGCMMessage(Context context, Intent intent) {
        Bundle extras=intent.getExtras();
        String game_key = extras.getString("game_key");
        if (game_key != null) { // cloud game message

            Log.i("GCM act" + ((GobandroidApp) context.getApplicationContext()).getGame().toString());

            GobandroidApp ga = (GobandroidApp) context.getApplicationContext();
            Log.i("GCM incoming Message cloud game" + game_key + "+ game cloud key" + ga.getGame().getCloudKey());
            if (!ga.hasActiveGoActivity() || ga.getGame().getCloudKey() == null || !ga.getGame().getCloudKey().equals(game_key)) {

                new GobandroidNotifications(context).addNewCloudMoveNotification(game_key);
            } else
                try {

                    String sgf = CloudGobanFactory.getInstance().games().get(game_key).execute().getSgf().getValue();
                    ga.getGame().setGame(SGFHelper.sgf2game(sgf, null));

                    while (ga.getGame().getActMove().hasNextMove())
                        ga.getGame().jump(ga.getGame().getActMove().getnextMove(0)); // mainstream

                    ga.getGame().setCloudDefs(game_key, extras.getString("role"));

                    ga.getGame().notifyGameChange();

                } catch (IOException e1) {

                }

        }
    }

    public static void onGoGameChange(GoGame game) {

        //GobandroidApp.get
    }

    public static void onSolvedTsumego(GobandroidFragmentActivity ctx,GoGame game) {
        new UploadGameAndShareMoment(ctx, "solved_tsumego").execute();
    }
}
