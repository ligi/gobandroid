package org.ligi.gobandroid_hd.ui.tsumego.fetch;

import android.content.Context;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.File;
import okio.BufferedSink;
import okio.Okio;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.backend.GobandroidBackend;
import org.ligi.gobandroid_hd.ui.application.GobandroidSettings;

public class TsumegoDownloadHelper {

    private final static String BASE_URL = "http://gogameguru.com/i/go-problems/";

    public static TsumegoSource[] getDefaultList(GobandroidSettings settings) {
        return new TsumegoSource[]{new TsumegoSource(settings.getTsumegoPath() + "1.easy/", BASE_URL, "ggg-easy-%02d.sgf"),
                                   new TsumegoSource(settings.getTsumegoPath() + "2.intermediate/", BASE_URL, "ggg-intermediate-%02d.sgf"),
                                   new TsumegoSource(settings.getTsumegoPath() + "3.hard/", BASE_URL, "ggg-hard-%02d.sgf")};
    }

    public static int doDownloadDefault(App app) {
        return doDownload(app, getDefaultList(App.component().settings()));
    }

    public static int doDownload(Context ctx, TsumegoSource[] params) {
        int download_count = 0;

        final int limit = GobandroidBackend.getMaxTsumegos(ctx);

        if (limit != -1) for (TsumegoSource src : params) {

            boolean finished = false;
            int pos = 10;

            final OkHttpClient client = new OkHttpClient();

            while (!finished) {

                while (new File(src.local_path + src.getFnameByPos(pos)).exists()) {
                    pos++;
                }

                if (pos >= limit) {
                    finished = true;
                } else try {
                    final Request build = new Request.Builder().url(src.remote_path + src.getFnameByPos(pos)).build();

                    final Response response = client.newCall(build).execute();
                    final File downloadedFile = new File(src.local_path + src.getFnameByPos(pos));

                    final BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
                    sink.writeAll(response.body().source());
                    sink.close();

                    response.body().close();

                    download_count++;

                } catch (Exception e) {
                    e.printStackTrace();
                    finished = true;
                }

            }

            try {
                Thread.sleep(199);
            } catch (InterruptedException ignored) {
            }

        }
        return download_count;
    }
}
