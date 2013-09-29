package org.ligi.gobandroid_hd;

import android.content.Context;
import android.content.Intent;

import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;


/**
 * Created by ligi on 6/7/13.
 */
public class CloudHooks {

    public static void onApplicationCreation(App app) {

    }

    public static void syncUser(App app) {

    }

    public static void onGCMMessage(Context context, Intent intent) {

    }

    public static void onSolvedTsumego(GobandroidFragmentActivity ctx, GoGame game) {
    }

    public static void uploadGameAndShareMoment(GobandroidFragmentActivity ctx, GoGame game, String type) {
    }

    public static void uploadGame(GobandroidFragmentActivity ctx, GoGame game, String type) {
    }

    public static void profileOrOnlinePlay(GobandroidFragmentActivity ctx) {
    }
}
