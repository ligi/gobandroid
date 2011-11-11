package org.ligi.gobandroid_hd.ui.application;

import android.content.Context;
import android.os.Environment;

public class GobandroidSettings {
	public GobandroidSettings(Context ctx) {
	}
	
	public String getSGFBasePath() {
		return Environment.getExternalStorageDirectory()+"/gobandroid/sgf/";
	}
	
	public String getTsumegoPath() {
		return getSGFBasePath()+"tsumego/";
	}
	
	public String getReviewPath() {
		return getSGFBasePath()+"games/";
	}

}
