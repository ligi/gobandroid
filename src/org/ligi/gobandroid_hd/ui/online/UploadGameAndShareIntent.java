package org.ligi.gobandroid_hd.ui.online;


import android.content.Intent;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.tracedroid.Log;

public class UploadGameAndShareIntent extends UploadGameToCloudEndpointsWithUI {

    public String mIntroText;

    public UploadGameAndShareIntent(GobandroidFragmentActivity goActivity, String type, String introText) {
        super(goActivity, type);
        mIntroText = introText;
    }

    @Override
    public void onSuccess(String key) {
        Log.i("sharing to via Intent for type: " + type + " game key " + key);

        Intent s = new Intent(android.content.Intent.ACTION_SEND);

        s.setType("text/plain");
        s.putExtra(Intent.EXTRA_SUBJECT, mIntroText);
        s.putExtra(Intent.EXTRA_TEXT, "https://cloud-goban.appspot.com/game/" + key);

        goActivity.startActivity(Intent.createChooser(s, "Choose How"));
    }

}
