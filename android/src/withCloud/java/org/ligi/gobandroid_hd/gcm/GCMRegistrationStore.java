package org.ligi.gobandroid_hd.gcm;

import android.content.Context;
import android.content.SharedPreferences;

import org.ligi.gobandroid_hd.App;
import org.ligi.tracedroid.logging.Log;

public class GCMRegistrationStore {

    private final static String TAG = "GCMRegistrationStore";
    private static final String PROPERTY_REG_ID = "REG_ID";
    private static final String PROPERTY_APP_VERSION = "APP_VERSION";
    public static final String STORE_NAME = "GCMPreferences";
    private final Context context;

    public GCMRegistrationStore(Context context) {
        this.context = context;
    }

    public void storeRegistrationId(String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = App.getVersionCode();
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
    }


    public String getRegistrationId() {
        final SharedPreferences prefs = getGCMPreferences(context);
        final String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = App.getVersionCode();
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

}
