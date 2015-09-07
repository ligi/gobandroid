package org.ligi.gobandroid_hd.ui.gnugo;

import android.content.Context;
import android.content.Intent;
import org.ligi.axt.AXT;

public class GnuGoHelper {

    public final static String INTENT_ACTION_NAME = "org.ligi.gobandroidhd.ai.gnugo.GnuGoService";

    public static boolean isGnuGoAvail(Context ctx) {
        return (AXT.at(new Intent(INTENT_ACTION_NAME)).isServiceAvailable(ctx.getPackageManager(), 0));
    }
}
