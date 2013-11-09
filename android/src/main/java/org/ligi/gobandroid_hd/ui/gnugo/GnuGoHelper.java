package org.ligi.gobandroid_hd.ui.gnugo;

import android.content.Context;
import android.content.Intent;

import org.ligi.axt.AXT;
import org.ligi.gobandroid_hd.logic.GnuGoMover;

public class GnuGoHelper {

    public static boolean isGnuGoAvail(Context ctx) {
        return (AXT.at(new Intent(GnuGoMover.intent_action_name)).isServiceAvailable(ctx.getPackageManager(), 0));
    }
}
