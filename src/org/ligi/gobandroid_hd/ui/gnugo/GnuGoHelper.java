package org.ligi.gobandroid_hd.ui.gnugo;

import android.content.Context;
import android.content.Intent;
import org.ligi.androidhelper.AndroidHelper;
import org.ligi.gobandroid_hd.logic.GnuGoMover;

public class GnuGoHelper {

    public static boolean isGnuGoAvail(Context ctx) {
        return (AndroidHelper.at(new Intent(GnuGoMover.intent_action_name)).isIntentAvailable( ctx.getPackageManager(), 0));
    }
}
