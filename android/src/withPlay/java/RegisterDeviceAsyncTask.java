// TODO doesnt need to be a async task any more - no more UI interaction
static class RegisterDeviceAsyncTask extends AsyncTask<Void, Void, Void> {

    private App app;

    public RegisterDeviceAsyncTask(App app) {
        this.app = app;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {

            String device_id = Secure.getString(app.getContentResolver(), Secure.ANDROID_ID);
            String push_id = GCMRegistrar.getRegistrationId(app);
            if (push_id.equals(""))
                push_id = "unknown";

            String url_str = "https://" + GobandroidConfiguration.backend_domain + "/gcm/register?";
            url_str += getURLParamSnippet("device_id", device_id);
            url_str += "&" + getURLParamSnippet("push_key", push_id);
            url_str += "&" + getURLParamSnippet("app_version", app.getAppVersion());

            if (app.getSettings().isTsumegoPushEnabled())
                url_str += "&" + getURLParamSnippet("want_tsumego", "t");

            // TODO not send every time
            url_str += "&" + getURLParamSnippet("device_str", Build.VERSION.RELEASE + " | " + Build.MANUFACTURER + " | " + Build.DEVICE + " | " + Build.MODEL + " | " + Build.DISPLAY + " | " + Build.CPU_ABI + " | " + Build.TYPE + " | " + Build.TAGS);

            URL url = new URL(url_str);
            AXT.at(url).downloadToString().replace("\n", "").replace("\r", "");// .equals("saved");
        } catch (Exception e) {
            Log.w("cannot register push" + e);
        }
        return null;
    }

    public final static void registerDevice(App app) {
        new RegisterDeviceAsyncTask(app).execute();
    }
}

