package org.ligi.gobandroid_hd.gcm;

import android.os.Build;
import android.provider.Settings;
import android.support.design.BuildConfig;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.ligi.axt.AXT;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.etc.GobandroidConfiguration;
import org.ligi.tracedroid.logging.Log;

import java.net.URL;

import static org.ligi.gobandroid_hd.backend.GobandroidBackend.getURLParamSnippet;

// TODO doesnt need to be a async task any more - no more UI interaction
public class RegisterDevice implements Runnable {

    private final App app;

    public RegisterDevice(App app) {
        this.app = app;
    }

    public static void registerDevice(App app) {
        new Thread(new RegisterDevice(app)).start();
    }

    @Override
    public void run() {
        try {

            String device_id = Settings.Secure.getString(app.getContentResolver(), Settings.Secure.ANDROID_ID);

            final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(app);
            String push_id = gcm.register(GobandroidConfiguration.GCM_SENDER_ID);

            if (push_id.equals("")) {
                push_id = "unknown";
            }

            String url_str = "https://" + GobandroidConfiguration.backend_domain + "/gcm/register?"
                    + getURLParamSnippet("device_id", device_id)
                    + "&" + getURLParamSnippet("push_key", push_id)
                    + "&" + getURLParamSnippet("app_version", BuildConfig.VERSION_NAME);

            if (app.getSettings().isTsumegoPushEnabled()) {
                url_str += "&" + getURLParamSnippet("want_tsumego", "t");
            }

            // TODO not send every time
            url_str += "&" + getURLParamSnippet("device_str", Build.VERSION.RELEASE + " | " + Build.MANUFACTURER + " | " + Build.DEVICE + " | " + Build.MODEL + " | " + Build.DISPLAY + " | " + Build.CPU_ABI + " | " + Build.TYPE + " | " + Build.TAGS);

            final URL url = new URL(url_str);
            AXT.at(url).downloadToString().replace("\n", "").replace("\r", "");// .equals("saved");

            new GCMRegistrationStore(app).storeRegistrationId(push_id);
        } catch (Exception e) {
            Log.w("cannot register push" + e);
        }
    }
}

