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
	
	public String getBookmarkPath() {
		return getReviewPath()+"bookmarks/";
	}
	
	public String getReviewPath() {
		return getSGFBasePath()+"review/";
	}
	
	public String getSGFSavePath() {
		return getSGFBasePath()+"review/saved/";
	}
}
