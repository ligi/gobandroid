package org.ligi.gobandroid_hd.ui.online;

import android.app.AlertDialog;
import android.content.Intent;
import org.ligi.android.common.dialogs.DialogDiscarder;
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
public class UploadGameToCloudEndpointsWithSend extends UploadGameToCloudEndpointsBase {

    private GobandroidFragmentActivity goActivity;

    public UploadGameToCloudEndpointsWithSend(GobandroidFragmentActivity goActivity, String type) {
        super(goActivity, type);
        this.goActivity = goActivity;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result == null) {
            AlertDialog.Builder alert_b = new AlertDialog.Builder(goActivity);
            alert_b.setMessage("Cannot create the game - please try again later");
            alert_b.setTitle("Server Problem");
            alert_b.setIcon(android.R.drawable.ic_dialog_alert);
            alert_b.setPositiveButton(R.string.ok, new DialogDiscarder());
            alert_b.show();
        } else
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, goActivity.getString(R.string.go_game_invitation));
                String color = goActivity.getString(R.string.white);
                if (goActivity.getGame().getActMove().isBlackToMove())
                    color = goActivity.getString(R.string.black);

                String sAux = "\n" + String.format(goActivity.getString(R.string.you_are_invited_to_a_go_game), goActivity.getGame().getSize(), color) + "\n";
                sAux = sAux + GobandroidConfiguration.CLOUD_GOBAN_URL_BASE + result + "\n \n #gobandroid\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                goActivity.startActivity(Intent.createChooser(i, goActivity.getString(R.string.choose_invite_method)));
            } catch (Exception e) { // e.toString();
            }
    }

    @Override
    public boolean doRegister() {
        return true;
    }
}
