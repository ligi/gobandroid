package org.ligi.gobandroid_hd.backend;

import android.content.Context;
import android.provider.Settings.Secure;

import org.ligi.axt.AXT;
import org.ligi.gobandroid_hd.etc.GobandroidConfiguration;
import org.ligi.tracedroid.logging.Log;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

public class GobandroidBackend {

    /**
     * fetches the nuber of tsumegos available on gogameguru from the server
     *
     * @return the count or -1 on any error
     */
    public final static int getMaxTsumegos(Context ctx) {
        try {
            URL url = new URL("http://" + GobandroidConfiguration.backend_domain + "/tsumegos/max?device_id=" + Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID));
            String count_str = AXT.at(url).downloadToString();

            count_str = count_str.replace("\n", "").replace("\r", "").trim(); // clean
            // the
            // string
            return Integer.parseInt(count_str);
        } catch (Exception e) {
            Log.w("cannot fetch the tsumego count " + e);
            return -1;
        }
    }

    public static String getURLParamSnippet(String key, String val) {
        try {
            return key + "=" + URLEncoder.encode(val, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.w("encoding problem");
            return key + "=" + val;
        }
    }

}
