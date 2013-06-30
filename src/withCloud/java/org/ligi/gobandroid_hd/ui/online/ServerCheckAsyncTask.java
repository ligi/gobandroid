package org.ligi.gobandroid_hd.ui.online;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.google.api.services.cloudgoban.CloudFactory;
import com.google.api.services.cloudgoban.Cloudgoban;
import com.tapfortap.AppWall;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

import java.io.IOException;

/**
 * check the server-state and if the server is faulty
 * <p/>
 * User: ligi
 * Date: 2/1/13
 * Time: 7:50 PM
 */
public class ServerCheckAsyncTask extends AsyncTask<Void, Void, Boolean> {

    public GobandroidFragmentActivity activity;

    public ServerCheckAsyncTask(GobandroidFragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (!aBoolean) // server problem
            new AlertDialog.Builder(activity)
                    .setTitle("Server Problem")
                    .setMessage("the server cannot be reached - this could mean it is over quota - please try again later ( quota is reset every 24h ) you can help paying for the servers and bringing down-times to a minimum by clicking \"view ad\"")
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            activity.finish();
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finish();
                        }
                    })
                    .setNeutralButton("view ad", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // In onCreate
                            AppWall.prepare(activity);

// Later when you want to display the app wall
                            AppWall.show(activity);

                            AppWall.setListener(new AppWall.AppWallListener() {
                                @Override
                                public void onDismiss() {
                                    activity.finish();
                                }
                            });

                        }
                    })
                    .show();

        super.onPostExecute(aBoolean);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        for (int retry = 0; retry < 3; retry++)
            try {
                Cloudgoban gc = CloudFactory.getCloudgoban();
                String status = gc.serverstatus().get().execute().getStatus();
                if (status != null)
                    return status.equals("OK");
                Thread.sleep((int) Math.pow(retry + 1, 2) * 1000); // exponential back off
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
            }

        return false;
    }
}
