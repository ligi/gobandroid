package org.ligi.gobandroid_hd;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.cloudgoban.Cloudgoban;

/**
 * Created by ligi on 6/7/13.
 */
public class CloudGobanFactory {
    private static Cloudgoban sCloudgoban;

    public static Cloudgoban getInstance() {

        if (sCloudgoban == null) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = new GsonFactory();

            sCloudgoban = new Cloudgoban(transport, jsonFactory, null);
        }

        return sCloudgoban;
    }
}
