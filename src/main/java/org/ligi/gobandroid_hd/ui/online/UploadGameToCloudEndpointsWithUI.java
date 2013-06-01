package org.ligi.gobandroid_hd.ui.online;

import android.app.AlertDialog;
import org.ligi.androidhelper.helpers.dialog.DialogDiscardingOnClickListener;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

/**
 * Created with IntelliJ IDEA.
 * User: ligi
 * Date: 1/31/13
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class UploadGameToCloudEndpointsWithUI extends UploadGameToCloudEndpointsBaseWithProgressDialog {

    protected GobandroidFragmentActivity goActivity;

    public UploadGameToCloudEndpointsWithUI(GobandroidFragmentActivity goActivity, String type) {
        super(goActivity, type);
        this.goActivity = goActivity;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result == null) {
            AlertDialog.Builder alert_b = new AlertDialog.Builder(goActivity);
            alert_b.setMessage(goActivity.getString(R.string.cannot_create_game_server_problem));
            alert_b.setTitle(goActivity.getString(R.string.server_problem));
            alert_b.setIcon(android.R.drawable.ic_dialog_alert);
            alert_b.setPositiveButton(R.string.ok, new DialogDiscardingOnClickListener());
            alert_b.show();
        } else {
            onSuccess(result);
        }

    }

    @Override
    public boolean doRegister() {
        return true;
    }

    public abstract void onSuccess(String key);
}
