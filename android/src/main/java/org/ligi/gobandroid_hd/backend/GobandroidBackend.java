package org.ligi.gobandroid_hd.backend;

import android.content.Context;
import android.provider.Settings.Secure;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.ligi.gobandroid_hd.etc.GobandroidConfiguration;
import timber.log.Timber;

public class GobandroidBackend {

    /**
     * fetches the number of tsumegos available on gogameguru from the server
     *
     * @return the count or -1 on any error
     */
    public static int getMaxTsumegos(final Context ctx) {
        try {
            final URL url = new URL("http://" +
                                    GobandroidConfiguration.backend_domain +
                                    "/tsumegos/max?device_id=" +
                                    Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID));

            final OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url(url).build();

            final Response response = client.newCall(request).execute();
            final String count_str = response.body().string();

            final String cleanedCountStr = count_str.replace("\n", "").replace("\r", "").trim(); // clean the string
            return Integer.parseInt(cleanedCountStr);
        } catch (Exception e) {
            Timber.w(e, "cannot fetch the tsumego count");
            return -1;
        }
    }

    public static String getURLParamSnippet(final String key, final String val) {
        try {
            return key + "=" + URLEncoder.encode(val, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Timber.w("encoding problem");
            return key + "=" + val;
        }
    }

}
