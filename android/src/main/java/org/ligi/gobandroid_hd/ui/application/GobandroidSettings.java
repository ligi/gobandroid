package org.ligi.gobandroid_hd.ui.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import java.io.File;
import org.ligi.gobandroid_hd.R;

public class GobandroidSettings {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_RANK = "rank";

    // we need some context
    private final Context ctx;

    public GobandroidSettings(Context ctx) {
        this.ctx = ctx;
    }

    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public String getSGFBasePath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory() + "/gobandroid/sgf/";
        }

        // workaround for Samsung tablet with internal and external SD-card
        final File probe = new File("/sdcard/Android");
        if (probe.exists() && probe.isDirectory()) {
            return "/sdcard/gobandroid/sgf/";
        }

        return ctx.getFilesDir() + "/sgf/";
    }

    public String getTsumegoPath() {
        return getSGFBasePath() + "tsumego/";
    }

    public String getBookmarkPath() {
        return getReviewPath() + "bookmarks/";
    }

    public String getReviewPath() {
        return getSGFBasePath() + "review/";
    }

    public String getSGFSavePath() {
        return getSGFBasePath() + "review/saved/";
    }

    public boolean isFullscreenEnabled() {
        return getPreferences().getBoolean(ctx.getString(R.string.prefs_fullscreen), false);
    }

    public boolean isSoundEnabled() {
        return getBoolForKey(R.string.prefs_do_sound, true);
    }

    public boolean isLegendEnabled() {
        return getBoolForKey(R.string.prefs_do_legend, true);
    }

    public boolean isSGFLegendEnabled() {
        return getBoolForKey(R.string.prefs_sgf_legend, true);
    }

    public boolean isConstantLightWanted() {
        return getBoolForKey(R.string.prefs_constant_light, true);
    }

    public boolean isGridEmbossEnabled() {
        return getBoolForKey(R.string.prefs_grid_emboss, true);
    }

    public boolean isTsumegoPushEnabled() {
        return getBoolForKey(R.string.prefs_push_tsumego, true);
    }

    public String getUsername() {
        return getPreferences().getString(KEY_USERNAME, "");
    }

    public void setUsername(String username) {
        getPreferences().edit().putString(KEY_USERNAME, username).commit();
    }

    public String getRank() {
        return getPreferences().getString(KEY_RANK, "");
    }

    public void setRank(String rank) {
        getPreferences().edit().putString(KEY_RANK, rank).commit();
    }

    private boolean getBoolForKey(@StringRes int stringRes, boolean defaultValue) {
        return getPreferences().getBoolean(ctx.getString(stringRes), defaultValue);
    }
}