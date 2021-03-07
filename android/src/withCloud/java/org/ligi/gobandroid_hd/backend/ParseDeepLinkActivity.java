package org.ligi.gobandroid_hd.backend;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.gms.plus.PlusShare;
import timber.log.Timber;


public class ParseDeepLinkActivity extends Activity {
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        final String deepLinkId = PlusShare.getDeepLinkId(this.getIntent());

        Timber.i("coming from DeepLink " + deepLinkId);
    }
}
