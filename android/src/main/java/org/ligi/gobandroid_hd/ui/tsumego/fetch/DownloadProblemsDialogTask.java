package org.ligi.gobandroid_hd.ui.tsumego.fetch;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.ligi.axt.listeners.DialogDiscardingOnClickListener;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.Refreshable;

/**
 * Async tas to download tsumegos
 *
 * @author ligi
 */
public class DownloadProblemsDialogTask extends AsyncTask<TsumegoSource[], String, Integer> {

    private ProgressDialog progress_dialog;
    private Refreshable refreshable;
    public Context ctx;

    public DownloadProblemsDialogTask(Context ctx, Refreshable refreshable) {
        this.ctx = ctx;
        this.refreshable = refreshable;
    }

    @Override
    protected void onPreExecute() {
        progress_dialog = ProgressDialog.show(ctx, ctx.getString(R.string.download_status), ctx.getString(R.string.downloading_tsumegos_please_wait), true);
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        progress_dialog.setMessage(progress[0]);
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);

        if ((refreshable != null) && (result > 0)) {
            refreshable.refresh();
        }
        progress_dialog.dismiss();
        String msg = ctx.getString(R.string.no_new_tsumegos_found);

        if (result > 0) {
            msg = String.format(ctx.getString(R.string.downloaded_n_tsumego, result));
            refreshable.refresh();
        }

        new AlertDialog.Builder(ctx).setMessage(msg).setTitle(R.string.download_report).setPositiveButton(R.string.ok, new DialogDiscardingOnClickListener()).show();
    }

    @Override
    protected Integer doInBackground(TsumegoSource[]... params) {
        return TsumegoDownloadHelper.doDownload(ctx, params[0]);
    }

}