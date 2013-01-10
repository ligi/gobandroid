package org.ligi.gobandroid_hd.online;

import java.io.IOException;
import java.util.UUID;

import org.ligi.gobandroid_hd.GobandroidApp;
import org.ligi.tracedroid.Log;

import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

import com.google.android.gcm.GCMRegistrar;
import com.google.api.services.cloudgoban.Cloudgoban;
import com.google.api.services.cloudgoban.model.User;

public class UserHandler {

	public static String getUserKey(GobandroidApp ctx) {
		return ctx.getSettings().getPreferences().getString("user_key", null);
	}
	
	public static void syncUser(GobandroidApp ctx) {
		class SyncAsyncTask extends AsyncTask<Void,Void,Void> {
			 
			private GobandroidApp ctx;
			
			public SyncAsyncTask(GobandroidApp ctx)  {
				this.ctx=ctx;
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				Cloudgoban gc = ctx.getCloudgoban();
				User usr = new User();
				usr.setName(ctx.getSettings().getUsername());
				usr.setRank(ctx.getSettings().getRank());
				usr.setContact(GCMRegistrar.getRegistrationId(ctx));
				
				String user_key = getUserKey(ctx);
				//String user_key = null;
				
				if (user_key == null) { // user not yet registered
					Log.i("Creating go_user initial");
					usr.setSecret(UUID.randomUUID().toString());
					
					try {
						user_key=gc.users().insert(usr).execute().getEncodedKey();
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
					try {
						gc.users().edit(usr).execute().getEncodedKey();
						Log.i("mod go_user e");
					} catch (IOException e) {
						Log.i("mod go_user" + e);
					}
				}
				return null;
			}
			
		}
		new SyncAsyncTask(ctx).execute();
		
	}
	 
}
