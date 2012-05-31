package org.ligi.gobandroid_hd.ui.tsumego.fetch;


import org.ligi.android.common.dialogs.DialogDiscarder;
import org.ligi.gobandroid_hd.ui.Refreshable;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Async tas to download tsumegos 
 * 
 * @author ligi
 *
 */
public class DownloadProblemsDialogTask extends AsyncTask<TsumegoSource[],String,Integer>  {

	
	private ProgressDialog progress_dialog;
	private Refreshable refreshable;
	public Context ctx;
	
	public DownloadProblemsDialogTask(Context ctx, Refreshable refreshable) {
		this.ctx=ctx;
		this.refreshable=refreshable;
	}

	
	@Override
	protected void onPreExecute() {
    	progress_dialog = ProgressDialog.show(ctx, "Download Status", "Downloading Tsumegos Please wait...", true);
		super.onPreExecute();
	}
	
	@Override
    protected void onProgressUpdate(String... progress) {
        progress_dialog.setMessage(progress[0]);
    }


	@Override
    protected void onPostExecute(Integer result) {
		 super.onPostExecute(result);
		 
		 if ((refreshable!=null)&&(result>0)) {
    		 refreshable.refresh();
    	 }
    	 progress_dialog.dismiss();
    	 String msg="No new Tsumegos found :-(";
    	 
    	 if (result>0) {
    		 msg="Downloaded " + result + " new Tsumegos ;-)";
    		 refreshable.refresh();
    	 }
    	 
    	 new AlertDialog.Builder(ctx).setMessage(msg)
    	 	.setTitle("Download Report")
    	 	.setPositiveButton("OK", new DialogDiscarder()).show();
     }


	@Override
	protected Integer doInBackground(TsumegoSource[]... params) {
		return TsumegoDownloadHelper.doDownload(ctx,params[0]);
	}

}