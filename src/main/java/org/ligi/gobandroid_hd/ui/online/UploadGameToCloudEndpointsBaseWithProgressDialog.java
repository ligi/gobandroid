package org.ligi.gobandroid_hd.ui.online;

import android.app.ProgressDialog;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;


public class UploadGameToCloudEndpointsBaseWithProgressDialog extends UploadGameToCloudEndpointsBase {

    private ProgressDialog pd;

    public UploadGameToCloudEndpointsBaseWithProgressDialog(GobandroidFragmentActivity goActivity, String type) {
        super(goActivity, type);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(goActivity);
        pd.setMessage(goActivity.getString(R.string.uploading_game));
        pd.show();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        pd.dismiss();
    }

}
