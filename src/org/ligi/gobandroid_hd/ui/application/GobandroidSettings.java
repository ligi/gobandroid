package org.ligi.gobandroid_hd.ui.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

public class GobandroidSettings {

	/* the keys */
	public final static String KEY_FULLSCREEN="fullscreen";
	public final static String KEY_SOUND="do_sound";
	public final static String KEY_DO_LEGEND="do_legend";
	public final static String KEY_SGF_LEGEND="sgf_legend";
	public static final String KEY_WAKE_LOCK = "wake_lock";
	
	private Context ctx;
	
	public GobandroidSettings(Context ctx) {
		this.ctx=ctx;
	}
	
	public SharedPreferences getPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(ctx);	
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
	
	public boolean isFullscreenEnabled() {
		return getPreferences().getBoolean(KEY_FULLSCREEN, false);
	}
	
	public boolean isSoundEnabled() {
		return getPreferences().getBoolean(KEY_SOUND, false);
	}
	
	public boolean isLegendEnabled() {
		return getPreferences().getBoolean(KEY_DO_LEGEND, false);
	}
	
	public boolean isSGFLegendEnabled() {
		return getPreferences().getBoolean(KEY_SGF_LEGEND, false);
	}
	
	public boolean isWakeLockEnabled() {
		return getPreferences().getBoolean(KEY_WAKE_LOCK, true);
	}
}