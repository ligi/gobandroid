package org.ligi.gobandroid_hd.ui.tsumego.fetch;

import android.content.Context;

import org.apache.http.util.ByteArrayBuffer;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.backend.GobandroidBackend;
import org.ligi.tracedroid.logging.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

public class TsumegoDownloadHelper {

    private final static String BASE_URL = "http://gogameguru.com/i/go-problems/";

    public static TsumegoSource[] getDefaultList(App app) {
        return new TsumegoSource[]{new TsumegoSource(app.getSettings().getTsumegoPath() + "1.easy/", BASE_URL, "ggg-easy-%02d.sgf"), new TsumegoSource(app.getSettings().getTsumegoPath() + "2.intermediate/", BASE_URL, "ggg-intermediate-%02d.sgf"),
                new TsumegoSource(app.getSettings().getTsumegoPath() + "3.hard/", BASE_URL, "ggg-hard-%02d.sgf")};
    }

    public static int doDownloadDefault(App app) {
        return doDownload(app, getDefaultList(app));
    }

    public static int doDownload(Context ctx, TsumegoSource[] params) {
        int download_count = 0;

        int limit = GobandroidBackend.getMaxTsumegos(ctx);

        if (limit != -1)
            for (TsumegoSource src : params) {

                boolean finished = false;
                int pos = 10;

                while (!finished) {

                    while (new File(src.local_path + src.getFnameByPos(pos)).exists()) {

                        pos++;
                    }

                    if (pos >= limit) {
                        finished = true;
                    } else
                        try {
                            // new File().exists()
                            URL url = new URL(src.remote_path + src.getFnameByPos(pos));
                            URLConnection ucon = url.openConnection();
                            BufferedInputStream bis = new BufferedInputStream(ucon.getInputStream());

                            ByteArrayBuffer baf = new ByteArrayBuffer(50);
                            int current;
                            while ((current = bis.read()) != -1)
                                baf.append((byte) current);

                            FileOutputStream fos = new FileOutputStream(new File(src.local_path + src.getFnameByPos(pos)));
                            fos.write(baf.toByteArray());
                            fos.close();

                            download_count++;

                        } catch (Exception e) {
                            Log.i("", e);
                            finished = true;
                        }

                }

                try {
                    Thread.sleep(199);
                } catch (InterruptedException e) {
                }

            }
        return download_count;
    }
}
