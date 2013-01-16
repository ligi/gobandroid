package org.ligi.gobandroid_hd.ui.gnugo;

import android.content.Context;
import android.content.Intent;
import org.ligi.android.common.intents.IntentHelper;
import org.ligi.gobandroid_hd.logic.GnuGoMover;

public class GnuGoHelper {

    public static boolean isGnuGoAvail(Context ctx) {
        return (IntentHelper.isServiceAvailable(new Intent(GnuGoMover.intent_action_name), ctx.getPackageManager(), 0));
    }
}
