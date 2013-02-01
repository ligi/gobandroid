package org.ligi.gobandroid_hd.ui.online;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import com.google.android.gcm.GCMRegistrar;
import com.google.api.services.cloudgoban.Cloudgoban;
import com.google.api.services.cloudgoban.model.User;
import org.ligi.gobandroid_hd.GobandroidApp;
import org.ligi.tracedroid.Log;

import java.io.IOException;
import java.util.UUID;

public class UserHandler {

    /**
     * get the user key - e.g. needed to open a cloud game
     *
     * @param ctx
     * @return the user key
     */
    public static String getUserKey(GobandroidApp ctx) {
        return ctx.getSettings().getPreferences().getString("user_key", null);
    }

    /**
     * syncronize the local user with the one in the cloud
     *
     * @param ctx - we need to haz context
     */
    public static void syncUser(GobandroidApp ctx) {


        class SyncAsyncTask extends AsyncTask<Void, Void, Void> {

            private GobandroidApp ctx;

            public SyncAsyncTask(GobandroidApp ctx) {
                this.ctx = ctx;
            }

            @Override
            protected Void doInBackground(Void... params) {

                SharedPreferences prefs = ctx.getSettings().getPreferences();

                // if we don't have a secret -> generate one
                if (!prefs.contains("user_secret"))
                    prefs.edit().putString("user_secret", UUID.randomUUID().toString()).commit();


                Cloudgoban gc = ctx.getCloudgoban();

                User usr = new User();
                usr.setName(ctx.getSettings().getUsername());
                usr.setRank(ctx.getSettings().getRank());
                usr.setContact(GCMRegistrar.getRegistrationId(ctx));

                usr.setSecret(ctx.getSettings().getPreferences().getString("user_secret", ""));
                if (prefs.contains("user_key"))
                    usr.setEncodedKey(prefs.getString("user_key", null));

                int attempt = 0;
                String usr_key = null;
                while ((usr_key == null) && (attempt < 5))
                    try {
                        attempt++;
                        usr_key = gc.users().put(usr).execute().getEncodedKey();
                        Log.i("mod go_user e attempt " + attempt + " regId" + GCMRegistrar.getRegistrationId(ctx));
                    } catch (IOException e) {
                        Log.i("mod go_user" + e);
                    }

                // save the user_key
                if (usr_key != null) {
                    SharedPreferences.Editor edit = ctx.getSettings().getPreferences().edit();
                    edit.putString("user_key", usr_key);
                    edit.commit();
                }

                /*
                String user_key = getUserKey(ctx);
				//String user_key = null;
				
				if (user_key == null) { // user not yet registered
					Log.i("Creating go_user initial");
					usr.setSecret(UUID.randomUUID().toString());
					
					try {
						user_key=gc.users().put(usr).execute().getEncodedKey();
						Editor edit=ctx.getSettings().getPreferences().edit();
						edit.putString("user_secret",usr.getSecret());
						edit.putString("user_key", user_key);
						edit.commit();
					} catch (IOException e) {
					}
				} else { // user there - update
					Log.i("mod go_user");
					
					usr.setSecret(ctx.getSettings().getPreferences().getString("user_secret",""));
					usr.setEncodedKey(user_key);

                    int attempt=0;
                    String mod_key;

					try {
                        attempt++;
						gc.users().put(usr).execute().getEncodedKey();
						Log.i("mod go_user e");
					} catch (IOException e) {
						Log.i("mod go_user" + e);
					}
				}
				*/
                return null;

            }

        }
        new SyncAsyncTask(ctx).execute();

    }

}
