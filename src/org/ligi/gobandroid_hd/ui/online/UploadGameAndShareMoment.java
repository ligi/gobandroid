package org.ligi.gobandroid_hd.ui.online;


import com.google.android.gms.plus.model.moments.ItemScope;
import com.google.android.gms.plus.model.moments.Moment;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.tracedroid.Log;

public class UploadGameAndShareMoment extends UploadGameToCloudEndpointsWithUI {

    public UploadGameAndShareMoment(GobandroidFragmentActivity goActivity, String type) {
        super(goActivity, type);
    }

    @Override
    public void onSuccess(String key) {
        Log.i("writing moment for type: " + type + " game key " + key);
        boolean connected = goActivity.getPlusClient().isConnected();

        ItemScope target = new ItemScope.Builder()
                .setUrl("http://cloud-goban.appspot.com/game/" + key)
                .build();

        Moment moment = new Moment.Builder()
                .setType("http://schemas.google.com/AddActivity")
                .setTarget(target)
                .build();

        goActivity.getPlusClient().writeMoment(moment);
    }


}
