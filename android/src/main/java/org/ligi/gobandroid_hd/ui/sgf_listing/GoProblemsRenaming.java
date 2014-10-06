package org.ligi.gobandroid_hd.ui.sgf_listing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import org.ligi.axt.AXT;
import org.ligi.gobandroid_hd.helper.SGFFileNameFilter;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.sgf.SGFReader;
import org.ligi.tracedroid.logging.Log;

import java.io.File;

public class GoProblemsRenaming extends AsyncTask<Void, Integer, String> {

    private final Context context;
    private final File dir;
    private String[] list;

    private ProgressDialog progressDialog;

    public GoProblemsRenaming(Context context, File dir) {
        this.context = context;
        this.dir = dir;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        list = dir.list(new SGFFileNameFilter());

        progressDialog = new ProgressDialog(context);

        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(list.length);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {

        int i = 0;

        for (String filename : list) {

            final File gameFile = new File(dir, filename);
            try {
                final GoGame game = SGFReader.sgf2game(AXT.at(gameFile).readToString(), null, SGFReader.BREAKON_FIRSTMOVE);

                if (game == null) {
                    // some files inside goproblems are broken move them in a special directory
                    final File brokenPath = new File(dir, "broken");
                    brokenPath.mkdirs();
                    gameFile.renameTo(new File(brokenPath, gameFile.getName()));
                } else {
                    final File levelPath = new File(dir, game.getMetaData().getDifficulty());
                    levelPath.mkdirs();
                    gameFile.renameTo(new File(levelPath, gameFile.getName()));
                }
            } catch (Exception e) {
                Log.w("problem in the process of GoProblemsRenaming",e);
            }

            publishProgress(i++);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        progressDialog.dismiss();
        final Intent intent = new Intent(context, SGFFileSystemListActivity.class);
        intent.setData(Uri.parse("file://"+dir.toString()));
        context.startActivity(intent);
        super.onPostExecute(s);
    }
}
