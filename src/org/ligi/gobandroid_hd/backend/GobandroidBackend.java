package org.ligi.gobandroid_hd.backend;

import java.net.URL;
import java.net.URLEncoder;

import org.ligi.android.common.net.NetHelper;
import org.ligi.gobandroid_hd.etc.GobandroidConfiguration;
import org.ligi.tracedroid.Log;

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
	
	
	public final static boolean registerPush(String device_id,String push_id) {
		try {
			
			URL url=new URL("http://"+GobandroidConfiguration.backend_domain+"/push/register?device_id="+URLEncoder.encode(device_id,"UTF-8")+"&push_key="+URLEncoder.encode(push_id,"UTF-8"));
			return NetHelper.downloadURL2String(url).replace("\n","").replace("\r","").equals("saved");
		} catch (Exception e) {
			Log.w("cannot register push" +e );
			return false;
		}
	}
}
