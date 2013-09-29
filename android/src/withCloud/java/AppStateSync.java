import android.app.Activity;

import com.google.android.gms.appstate.AppStateClient;
import com.google.android.gms.appstate.OnStateLoadedListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.gobandroid_hd.App;

public class AppStateSync {

    private final static int KEY_SETTINGS = 0;
    private final static int KEY_TSUMEGO_PROGRESS = 1;

    public static void sync(final Activity activity, final AppStateClient client) {
        client.loadState(new OnStateLoadedListener() {
            @Override
            public void onStateLoaded(int i, int i2, byte[] bytes) {

            }

            @Override
            public void onStateConflict(int stateKey, String resolvedVersion, byte[] localData, byte[] serverData) {

            }
        }, KEY_TSUMEGO_PROGRESS);

        client.loadState(new OnStateLoadedListener() {
            @Override
            public void onStateLoaded(int i, int i2, byte[] bytes) {

            }

            @Override
            public void onStateConflict(int stateKey, String resolvedVersion, byte[] localData, byte[] serverData) {
                App app = (App) activity.getApplicationContext();
                JSONObject settings_obj = new JSONObject();
                try {
                    settings_obj.put("rank", app.getSettings().getRank());
                } catch (JSONException e) {
                }
            }
        }, KEY_SETTINGS);
    }


}
