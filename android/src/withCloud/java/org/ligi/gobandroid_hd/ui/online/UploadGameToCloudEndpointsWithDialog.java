package org.ligi.gobandroid_hd.ui.online;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.recording.GameRecordActivity;

/**
 * Created with IntelliJ IDEA.
 * User: ligi
 * Date: 1/31/13
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadGameToCloudEndpointsWithDialog extends UploadGameToCloudEndpointsWithUI {

    public UploadGameToCloudEndpointsWithDialog(GobandroidFragmentActivity goActivity, String type) {
        super(goActivity, type);
    }

    @Override
    public void onSuccess(String key) {
        new AlertDialog.Builder(goActivity).setMessage(R.string.game_created_msg)
                .setTitle(R.string.game_created)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goActivity.finish();
                        goActivity.startActivity(new Intent(goActivity, GameRecordActivity.class));
                    }
                })
                .show();
    }
}
