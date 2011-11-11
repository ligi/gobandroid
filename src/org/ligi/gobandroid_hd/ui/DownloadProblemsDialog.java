package org.ligi.gobandroid_hd.ui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;
import org.ligi.android.common.dialogs.DialogDiscarder;
import org.ligi.tracedroid.logging.Log;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class DownloadProblemsDialog {
	 /**
     * 
     * @param activity
     * @param autoclose - if the alert should close when connection is established
     * 
     */
    public static void show(Context activity,boolean autoclose,Intent after_connection_intent ) {
    	int cnt=11;
    	final String[] url_lst=new String[3*cnt];
    	for (int i=0;i<cnt;i++) {
    		url_lst[i*3]=String.format("http://gogameguru.com/i/2011/08/ggg-easy-%02d.sgf",i+1);
    		url_lst[i*3+1]=String.format("http://gogameguru.com/i/2011/08/ggg-intermediate-%02d.sgf",i+1);
    		url_lst[i*3+2]=String.format("http://gogameguru.com/i/2011/08/ggg-hard-%02d.sgf",i+1);
    	}
    		
        LinearLayout lin=new LinearLayout(activity);
        lin.setOrientation(LinearLayout.VERTICAL);

        ScrollView sv=new ScrollView(activity);
        TextView details_text_view=new TextView(activity);

        LinearLayout lin_in_scrollview=new LinearLayout(activity);
        lin_in_scrollview.setOrientation(LinearLayout.VERTICAL);
        sv.addView(lin_in_scrollview);
        lin_in_scrollview.addView(details_text_view);

        details_text_view.setText("no text");

        ProgressBar progress =new ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(url_lst.length);

        lin.addView(progress);
        lin.addView(sv);


        new AlertDialog.Builder(activity)
        .setTitle("Download Status")
        .setView(lin)
        .setPositiveButton("OK", new DialogDiscarder())
        .show();

        class AlertDialogUpdater implements Runnable {

            private Handler h=new Handler();
            private TextView myTextView;
            private ProgressBar myProgress;

            public AlertDialogUpdater(TextView ab,ProgressBar progress) {
                myTextView=ab;
                myProgress=progress;

            }

            public void run() {

                for (int i=0;i<url_lst.length;i++)
                     {
                        class MsgUpdater implements Runnable {
                            
                            private int i;
                            public MsgUpdater(int i) {
                            	this.i=i;
                            }
                            public void run() {
                            	myProgress.setProgress(i+1);
                            	if (i!=url_lst.length-1)
                            		myTextView.setText("Downloading " + url_lst[i]);
                            	else
                            		myTextView.setText("Ready - please restart DUBwise to apply changes!");
                            }
                        }
                        h.post(new MsgUpdater(i));
                        
                        try {
                        	URL url=new URL(url_lst[i]);
                        URLConnection ucon = url.openConnection();
                        BufferedInputStream bis = new BufferedInputStream(ucon.getInputStream());
 
                        ByteArrayBuffer baf = new ByteArrayBuffer(50);
                        int current = 0;
                        while ((current = bis.read()) != -1) 
                                baf.append((byte) current);

                        File path=new File(Environment.getExternalStorageDirectory()+"/gobandroid/sgf/problems");
                        path.mkdirs();
                        
                        String fname=url.getFile().substring(url.getFile().lastIndexOf("/"));
                        FileOutputStream fos = new FileOutputStream(new File(path.getAbsolutePath() +"/"+fname));
                        fos.write(baf.toByteArray());
                        fos.close();
                        } catch (Exception e) {  Log.i("",e);} 
                        
                        
                        
                        try {
							Thread.sleep(199);
						} catch (InterruptedException e) {	}

                    } 
            }
        }

        new Thread(new AlertDialogUpdater(details_text_view,progress)).start();
    }
}
