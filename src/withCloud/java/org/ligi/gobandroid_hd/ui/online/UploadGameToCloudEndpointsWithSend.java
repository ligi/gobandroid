package org.ligi.gobandroid_hd.ui.online;

import android.content.Intent;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.etc.GobandroidConfiguration;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

/**
 * Created with IntelliJ IDEA.
 * User: ligi
 * Date: 1/31/13
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadGameToCloudEndpointsWithSend extends UploadGameToCloudEndpointsWithUI {

    public UploadGameToCloudEndpointsWithSend(GobandroidFragmentActivity goActivity, String type) {
        super(goActivity, type);
    }

    @Override
    public void onSuccess(String key) {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, goActivity.getString(R.string.go_game_invitation));
            String color = goActivity.getString(R.string.white);
            if (goActivity.getGame().getActMove().isBlackToMove())
                color = goActivity.getString(R.string.black);

            String sAux = "\n" + String.format(goActivity.getString(R.string.you_are_invited_to_a_go_game), goActivity.getGame().getSize(), color) + "\n";
            sAux = sAux + GobandroidConfiguration.CLOUD_GOBAN_URL_BASE + key + "\n \n #gobandroid\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            goActivity.startActivity(Intent.createChooser(i, goActivity.getString(R.string.choose_invite_method)));
        } catch (Exception e) { // e.toString();
        }
    }
}
