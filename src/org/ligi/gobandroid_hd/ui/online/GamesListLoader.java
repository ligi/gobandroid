package org.ligi.gobandroid_hd.ui.online;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import com.google.api.services.cloudgoban.Cloudgoban;
import com.google.api.services.cloudgoban.model.GameCollection;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ligi
 * Date: 1/31/13
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
class GamesListLoader extends AsyncTask<Void, Void, GameCollection> {

    private Dialog dlg;
    private OnlineSelectActivity onlineSelectActivity;
    private String type;


    public GamesListLoader(OnlineSelectActivity onlineSelectActivity, String type) {
        this.onlineSelectActivity = onlineSelectActivity;
        this.type = type;
    }

    @Override
    protected void onPreExecute() {
        ProgressDialog progDailog = new ProgressDialog(onlineSelectActivity);
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
        progDailog.show();
        dlg = progDailog;// new ProgressDialog.Builder(onlineSelectActivity).setMessage("loading").set.show();
        super.onPreExecute();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onPostExecute(GameCollection aVoid) {
        super.onPostExecute(aVoid);    //To change body of overridden methods use File | Settings | File Templates.
        dlg.dismiss();
        onlineSelectActivity.changeFragment(new ViewOnlineGameListFragment(aVoid));
    }

    @Override
    protected GameCollection doInBackground(Void... params) {

        Cloudgoban gc = onlineSelectActivity.getApp().getCloudgoban();
        try {
            return gc.games().list().setLimit(10).setType(type).execute();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }
}
