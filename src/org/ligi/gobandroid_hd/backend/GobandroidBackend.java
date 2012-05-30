package org.ligi.gobandroid_hd.backend;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import org.ligi.android.common.net.NetHelper;
import org.ligi.gobandroid_hd.GobandroidApp;
import org.ligi.gobandroid_hd.etc.GobandroidConfiguration;
import org.ligi.tracedroid.Log;

import com.google.android.c2dm.C2DMessaging;

import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings.Secure;

public class GobandroidBackend {

	/**
	 * fetches the nuber of tsumegos available on gogameguru from the server
	 * 
	 * @return the count or -1 on any error 
	 */
	public final static int getMaxTsumegos() {
		try {
			URL url=new URL("http://"+GobandroidConfiguration.backend_domain+"/tsumegos/max");
			String count_str=NetHelper.downloadURL2String(url);
			count_str=count_str.replace("\n", "").replace("\r","").trim(); // clean the string
			return Integer.parseInt(count_str);
		} catch (Exception e) {
			Log.w("cannot fetch the tsumego count" +e );
			return -1;
		}
	}
	

	private static String getURLParamSnippet(String key,String val) {
		try {
			return key+"="+URLEncoder.encode(val,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.w("encoding problem");
			return key+"="+val;
		}
	}
	
	static class RegisterDeviceAsyncTask extends AsyncTask<Void,Void,Void> {

		private GobandroidApp app;
		
		public RegisterDeviceAsyncTask(GobandroidApp app) {
			this.app=app;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				
				String device_id=Secure.getString( app.getContentResolver(), Secure.ANDROID_ID);
				String push_id=C2DMessaging.getRegistrationId(app);
				if (push_id.equals(""))
					push_id="unknown";
				
				String url_str="https://"+GobandroidConfiguration.backend_domain+"/push/register?";
				url_str+=getURLParamSnippet("device_id",device_id);
				url_str+="&"+getURLParamSnippet("push_key",push_id);
				url_str+="&"+getURLParamSnippet("app_version",app.getAppVersion());
				
				String wanted_pushs="";
				if (app.getSettings().isTsumegoPushEnabled())
					wanted_pushs+="tsumego";
				if (wanted_pushs.equals(""))
					wanted_pushs="none";
				
				url_str+="&"+getURLParamSnippet("wanted_pushs",wanted_pushs);
				url_str+="&"+getURLParamSnippet("device_str",Build.VERSION.RELEASE + " | " + Build.MANUFACTURER + " | " + Build.DEVICE  + " | " + Build.MODEL + " | " + Build.DISPLAY + " | " + Build.CPU_ABI +" | "+Build.TYPE + " | " +Build.TAGS);
				
				
				URL url=new URL(url_str);
				NetHelper.downloadURL2String(url).replace("\n","").replace("\r","");//.equals("saved");
			} catch (Exception e) {
				Log.w("cannot register push" +e );
			}
			return null;
		}
		
	}
	public final static void registerDevice(GobandroidApp app) {
		new RegisterDeviceAsyncTask(app).execute();
	}
}
