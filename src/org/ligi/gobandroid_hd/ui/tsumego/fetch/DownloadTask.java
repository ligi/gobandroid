package org.ligi.gobandroid_hd.ui.tsumego.fetch;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;
import org.ligi.android.common.dialogs.DialogDiscarder;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.tracedroid.logging.Log;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;

/**
 * Async tas to download tsumegos 
 * 
 * @author ligi
 *
 */
public class DownloadTask extends AsyncTask<TsumegoSource,String,Integer> {

	/** There are sometimes more tsumegos avail **/
	private final static int LIMITER=23;
	
	private GobandroidFragmentActivity activity;
	private ProgressDialog progress_dialog;
	
	public DownloadTask(GobandroidFragmentActivity activity) {
		this.activity=activity;
	}

	@Override
	protected void onPreExecute() {
    	progress_dialog = ProgressDialog.show(activity, "Download Status", "Downloading Tsumegos Please wait...", true);
		super.onPreExecute();
	}
	
	@Override
	protected Integer doInBackground(TsumegoSource... params) {
		
		int download_count=0;  
        for (TsumegoSource src : params)   {
              
                boolean finished=false;
                int pos=10;
                
                while(!finished) {

                    while (new File(src.local_path+src.getFnameByPos(pos)).exists()) {
                    	this.publishProgress("Processing " + src.getFnameByPos(pos));
                		pos++;
                	}
                	
                    if (pos>=LIMITER) {
                    	finished=true;
                    }
                    else try {
                    	//new File().exists()
                    	URL url=new URL(src.remote_path+src.getFnameByPos(pos));
                        URLConnection ucon = url.openConnection();
                        BufferedInputStream bis = new BufferedInputStream(ucon.getInputStream());
 
                        ByteArrayBuffer baf = new ByteArrayBuffer(50);
                        int current = 0;
                        while ((current = bis.read()) != -1) 
                                baf.append((byte) current);
                        
                        FileOutputStream fos = new FileOutputStream(new File(src.local_path+src.getFnameByPos(pos)));
                        fos.write(baf.toByteArray());
                        fos.close();

                        download_count++;
                        
                     } catch (Exception e) {  Log.i("",e); finished=true;} 
                
                }
                
                try {
					Thread.sleep(199);
				} catch (InterruptedException e) {	}

            }
		return download_count;
	}

	@Override
    protected void onProgressUpdate(String... progress) {
        progress_dialog.setMessage(progress[0]);
    }

	@Override
    protected void onPostExecute(Integer result) {
    	 progress_dialog.dismiss();
    	 String msg="No new Tsumegos found :-(";
    	 
    	 if (result>0)
    		 msg="Downloaded " + result + " new Tsumegos ;-)";
    	 
    	 new AlertDialog.Builder(activity).setMessage(msg)
    	 	.setTitle("Download Report")
    	 	.setPositiveButton("OK", new DialogDiscarder()).show();
     }

}