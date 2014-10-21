package org.ligi.gobandroid_hd;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.ligi.gobandroid_hd.gcm.GCMRegistrationStore;
import org.ligi.gobandroid_hd.gcm.RegisterDevice;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

public class CloudHooks {

    @TargetApi(14)
    public static void onApplicationCreation(final App app) {
        if (Build.VERSION.SDK_INT >= 14) {
            app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    final boolean noRegistrationId = new GCMRegistrationStore(activity).getRegistrationId().isEmpty();
                    if (noRegistrationId && checkPlayServices(activity)) {
                        RegisterDevice.registerDevice(app);
                    }
                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {

                }
            });
        }
    }

    public static void syncUser(App app) {

    }

    public static void onGCMMessage(Context context, Intent intent) {
/*        Bundle extras = intent.getExtras();
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
                    ga.getGame().setGame(SGFReader.sgf2game(sgf, null));

                    while (ga.getGame().getActMove().hasNextMove())
                        ga.getGame().jump(ga.getGame().getActMove().getnextMove(0)); // mainstream

                    ga.getGame().setCloudDefs(game_key, extras.getString("role"));

                    ga.getGame().notifyGameChange();

                } catch (IOException e1) {

                }

        }
*/
    }

    public static void onGoGameChange(GoGame game) {

        //App.get
    }

    public static void onSolvedTsumego(GobandroidFragmentActivity ctx, GoGame game) {
        //      new UploadGameAndShareMoment(ctx, "solved_tsumego").execute();
    }


    public static void uploadGameAndShareMoment(GobandroidFragmentActivity ctx, GoGame game, String type) {
//        new UploadGameAndShareMoment(ctx, type).execute();
    }

    public static void uploadGame(GobandroidFragmentActivity ctx, GoGame game, String type) {
        //      new UploadGameToCloudEndpointsBase(ctx, type).execute();
    }

    public static void profileOrOnlinePlay(final GobandroidFragmentActivity ctx) {
/*
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
*/
    }

    private static boolean checkPlayServices(Activity activity) {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        return resultCode == ConnectionResult.SUCCESS;
        // TODO check if we want some install option here
    }
}
