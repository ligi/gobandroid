package org.ligi.gobandroid_hd.ui.online;


import android.net.Uri;

import com.google.android.gms.plus.PlusShare;

import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.tracedroid.logging.Log;

public class UploadGameAndShareToGPplus extends UploadGameToCloudEndpointsWithUI {

    public String mIntroText;

    public UploadGameAndShareToGPplus(GobandroidFragmentActivity goActivity, String type, String introText) {
        super(goActivity, type);
        mIntroText = introText;
    }

    @Override
    public void onSuccess(String key) {
        Log.i("sharing to G+ for type: " + type + " game key " + key);
        postToGPlus(key);
    }

    private void postToGPlus(String key) {
        // Create an interactive post with the "VIEW_ITEM" label. This will
        // create an enhanced share dialog when the post is shared on Google+.
        // When the user clicks on the deep link, ParseDeepLinkActivity will
        // immediately parse the deep link, and route to the appropriate resource.
        Uri callToActionUrl = Uri.parse("https://cloud-goban.appspot.com/game/" + key);
        String callToActionDeepLinkId = "/game/" + key;

        // Create an interactive post builder.
        //PlusShare.Builder builder = new PlusShare.Builder(goActivity, goActivity.getPlusClient());
        PlusShare.Builder builder=null;

        // Set call-to-action metadata.
        //builder.addCallToAction("CREATE_ITEM", callToActionUrl, callToActionDeepLinkId);
        builder.addCallToAction("GO", callToActionUrl, callToActionDeepLinkId);

        // Set the target url (for desktop use).
        builder.setContentUrl(Uri.parse("https://cloud-goban.appspot.com/game/" + key));

        // Set the target deep-link ID (for mobile use).

        builder.setContentDeepLinkId(callToActionDeepLinkId);

        //builder.setRecipients()

        // Set the pre-filled message.
        builder.setText(mIntroText);

        goActivity.startActivityForResult(builder.getIntent(), 0);
    }
}
