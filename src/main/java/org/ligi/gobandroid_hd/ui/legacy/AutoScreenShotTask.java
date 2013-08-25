package org.ligi.gobandroid_hd.ui.legacy;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import org.ligi.androidhelper.AndroidHelper;
import org.ligi.androidhelper.helpers.dialog.DialogDiscardingOnClickListener;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.gobandroid_hd.ui.GoBoardViewHD;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoHelper;
import org.ligi.tracedroid.logging.Log;

import java.io.File;
import java.io.IOException;

/**
 * A task to do ScreenShots of GO-Boards/Situations to use as thumbnail to make
 * a more intuitive and better looking interface
 *
 * @author ligi
 * @deprecated - we use PreviewView now
 */
public class AutoScreenShotTask extends AsyncTask<String, String, Integer> {

    private GobandroidFragmentActivity activity;
    private AlertDialog progress_dialog;
    private GoBoardViewHD gbv;

    public AutoScreenShotTask(GobandroidFragmentActivity activity) {
        this.activity = activity;
    }

    public App getApp() {
        return (App) activity.getApplicationContext();
    }

    protected void onPostExecute(Integer result) {
        progress_dialog.dismiss();
        String msg = "No new Tsumegos found :-(";

        if (result > 0)
            msg = "Downloaded " + result + " new Tsumegos ;-)";

        // LinearLayout lin=new LinearLayout(activity);

        new AlertDialog.Builder(activity).setMessage(msg).setTitle(R.string.download_report)
                // .setView(gbv)
                .setPositiveButton(android.R.string.ok, new DialogDiscardingOnClickListener()).show();
    }

    protected void onProgressUpdate(String... progress) {
        progress_dialog.setMessage(progress[0]);
        gbv.invalidate();
    }

    @Override
    protected void onPreExecute() {

        View v = activity.getLayoutInflater().inflate(R.layout.screenshot_dialog, null);

        /** set upp the go board view **/
        gbv = (GoBoardViewHD) v.findViewById(R.id.board_to_shoot);
        gbv.setBackgroundResource(R.drawable.shinkaya);
        gbv.setGridEmboss(false); // looks better when scaled down
        gbv.do_legend = false; // gets to small in thumb
        gbv.do_actpos_highlight = false;

        /** tell the user what's happening **/
        TextView descr_tv = (TextView) v.findViewById(R.id.desc);
        descr_tv.setText(R.string.creating_thumbnails);

        progress_dialog = new AlertDialog.Builder(activity).setView(v).show();

        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... params) {
        processPath(params[0]);
        return 2;
    }

    public void processPath(String path) {

        Log.i("processing " + path);
        File dir = new File(path);
        File[] files = dir.listFiles();

        Log.i("processing res " + (dir == null) + " " + (files == null));
        if (files != null)
            for (File file : files) {
                if (file != null) {
                    if (file.isDirectory()) {
                        processPath(file.getPath());
                    } else if (file.getName().endsWith(".sgf")) {
                        try {
                            String sgf_content = AndroidHelper.at(file).loadToString();
                            if ((sgf_content != null) && (!sgf_content.equals(""))) {
                                getApp().getInteractionScope().setGame(SGFHelper.sgf2game(AndroidHelper.at(file).loadToString(), null));

                                if (file.getPath().contains("tsumego")) {
                                    gbv.setZoom(TsumegoHelper.calcZoom(getApp().getGame(), false));
                                    gbv.setZoomPOI(TsumegoHelper.calcPOI(getApp().getGame(), false));
                                } else {
                                    for (int i = 0; i < 42; i++)
                                        try {
                                            getApp().getGame().jump(getApp().getGame().getActMove().getnextMove(0));
                                        } catch (Exception e) {
                                        }
                                }

                                Log.i("doing screenshot" + file.getName());
                                gbv.screenshot(file.getPath() + ".png");
                                // this.publishProgress("foo");
                                this.publishProgress("foo");
                            }
                        } catch (IOException e) {
                            // we could not read the SGF - the only thing we can
                            // do there at the moment is log that :-(
                            Log.w("Problem reading SGF");
                        }
                    }
                }
            }

    }
}