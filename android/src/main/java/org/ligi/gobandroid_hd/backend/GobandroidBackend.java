package org.ligi.gobandroid_hd.backend;

import android.content.Context;
import android.provider.Settings.Secure;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import org.ligi.axt.AXT;
import org.ligi.gobandroid_hd.etc.GobandroidConfiguration;
import org.ligi.tracedroid.logging.Log;

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
            final String count_str = AXT.at(url).downloadToString();

            final String cleanedCountStr = count_str.replace("\n", "").replace("\r", "").trim(); // clean the string
            return Integer.parseInt(cleanedCountStr);
        } catch (Exception e) {
            Log.w("cannot fetch the tsumego count " + e);
            return -1;
        }
    }

    public static String getURLParamSnippet(final String key, final String val) {
        try {
            return key + "=" + URLEncoder.encode(val, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.w("encoding problem");
            return key + "=" + val;
        }
    }

}
