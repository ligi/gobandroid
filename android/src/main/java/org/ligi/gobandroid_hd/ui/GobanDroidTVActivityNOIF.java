package org.ligi.gobandroid_hd.ui;

import android.content.Intent;
import android.os.Bundle;

import org.ligi.gobandroid_hd.App;

/**
 * Activity to have the TV mode in NOIF ( No Interaction and Finite ) style to
 * e.g. use in BISMO
 *
 * @author <a href="http://ligi.de">Marcus -LiGi- Bueschleb </a>
 *         <p/>
 *         This software is licenced with GPLv3
 */

public class GobanDroidTVActivityNOIF extends GobanDroidTVActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        App.getInteractionScope().setIs_in_noif_mode(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public Intent getIntent2start() {
        return new Intent(this, GobanDroidTVActivity.class);
    }
}
