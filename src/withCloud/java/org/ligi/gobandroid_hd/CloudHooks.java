package org.ligi.gobandroid_hd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.gobandroid_hd.ui.GobandroidNotifications;
import org.ligi.gobandroid_hd.ui.BaseProfileActivity;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.online.OnlineSelectActivity;
import org.ligi.gobandroid_hd.ui.online.UploadGameAndShareMoment;
import org.ligi.gobandroid_hd.ui.online.UploadGameToCloudEndpointsBase;
import org.ligi.gobandroid_hd.ui.online.UserHandler;
import org.ligi.tracedroid.logging.Log;

import java.io.IOException;

public class CloudHooks {

    public static void onApplicationCreation(App app) {
        UserHandler.syncUser(app);
    }

    public static void syncUser(App app) {
        UserHandler.syncUser(app);
    }

    public static void onGCMMessage(Context context, Intent intent) {
        Bundle extras=intent.getExtras();
        String game_key = extras.getString("game_key");
        if (game_key != null) { // cloud game message

            Log.i("GCM act" + ((App) context.getApplicationContext()).getGame().toString());

            App ga = (App) context.getApplicationContext();
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

        //App.get
    }

    public static void onSolvedTsumego(GobandroidFragmentActivity ctx,GoGame game) {
        new UploadGameAndShareMoment(ctx, "solved_tsumego").execute();
    }


    public static void uploadGameAndShareMoment(GobandroidFragmentActivity ctx,GoGame game,String type) {
        new UploadGameAndShareMoment(ctx, type).execute();
    }

    public static void uploadGame(GobandroidFragmentActivity ctx,GoGame game,String type) {
        new UploadGameToCloudEndpointsBase(ctx, type).execute();
    }

    public static void profileOrOnlinePlay(final GobandroidFragmentActivity ctx) {

        if (ctx.getApp().getSettings().getUsername().equals(""))
            new AlertDialog.Builder(ctx).setMessage(ctx.getString(R.string.enter_username)).setTitle(ctx.getString(R.string.who_are_you))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ctx.startActivity(new Intent(ctx, BaseProfileActivity.class));
                        }
                    }).show();
        else
            ctx.startActivity(new Intent(ctx, OnlineSelectActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));

    }
}
