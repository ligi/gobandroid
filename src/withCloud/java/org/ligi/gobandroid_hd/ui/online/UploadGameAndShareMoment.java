package org.ligi.gobandroid_hd.ui.online;


import com.google.android.gms.plus.model.moments.ItemScope;
import com.google.android.gms.plus.model.moments.Moment;

import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.tracedroid.logging.Log;

public class UploadGameAndShareMoment extends UploadGameToCloudEndpointsBase {

    public UploadGameAndShareMoment(GobandroidFragmentActivity goActivity, String type) {
        super(goActivity, type);
    }

    @Override
    protected void onPostExecute(String key) {
        super.onPostExecute(key);
//
//        Log.i("trying to write moment for type: " + type + " game key " + key);
//
//        boolean connected = goActivity.getPlusClient().isConnected();
//
//        if (!connected) {
//            Log.w("not possible to share moment as mPlusClient is not connected");
//            return; // need to be connected to share a moment :-(
//        }
//
//        ItemScope target = new ItemScope.Builder()
//                .setUrl("http://cloud-goban.appspot.com/game/" + key)
//                .build();
//
//        Moment moment = new Moment.Builder()
//                .setType("http://schemas.google.com/AddActivity")
//                .setTarget(target)
//                .build();
//
//        goActivity.getPlusClient().writeMoment(moment);
    }
}
