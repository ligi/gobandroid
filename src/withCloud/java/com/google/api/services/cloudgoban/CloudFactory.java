package com.google.api.services.cloudgoban;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

/**
 * Created by ligi on 6/30/13.
 */
public class CloudFactory {

    private static Cloudgoban cloudGoban;

    public static Cloudgoban getCloudgoban() {
        if (cloudGoban==null) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = new GsonFactory();

            cloudGoban= new Cloudgoban(transport, jsonFactory, null);
        }
        return cloudGoban;
    }
}
