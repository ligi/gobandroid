package org.ligi.gobandroid_hd.backend;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.gms.plus.PlusShare;
import org.ligi.tracedroid.logging.Log;

public class ParseDeepLinkActivity extends Activity {
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        String deepLinkId = PlusShare.getDeepLinkId(this.getIntent());


        Log.i("coming from DeepLink " + deepLinkId);
    }
}
