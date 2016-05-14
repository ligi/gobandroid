package org.ligi.gobandroid_hd.ui.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.GoPrefs;

public class GobandroidSettingsTransition {

    private static final String KEY_USERNAME = "username";
    private static final String KEY_RANK = "rank";

    // we need some context
    private final Context ctx;

    public GobandroidSettingsTransition(Context ctx) {
        this.ctx = ctx;
    }

    public void transition() {
        final GoPrefs prefs = GoPrefs.INSTANCE;

        prefs.setFullscreenEnabled(isFullscreenEnabled());
        prefs.setSoundWanted(isSoundEnabled());
        prefs.setLegendEnabled(isLegendEnabled());
        prefs.setSGFLegendEnabled(isSGFLegendEnabled());
        prefs.setConstantLightWanted(isConstantLightWanted());
        prefs.setGridEmbossEnabled(isGridEmbossEnabled());
        prefs.setTsumegoPushEnabled(isTsumegoPushEnabled());
        prefs.setUsername(getUsername());
        prefs.setRank(getRank());
        prefs.isVersionSeen(getPreferences().getInt("VERSION", 0));
    }

    private SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    private boolean isFullscreenEnabled() {
        return getPreferences().getBoolean(ctx.getString(R.string.prefs_fullscreen), false);
    }

    private boolean isSoundEnabled() {
        return getBoolForKey(R.string.prefs_do_sound, true);
    }

    private boolean isLegendEnabled() {
        return getBoolForKey(R.string.prefs_do_legend, true);
    }

    private boolean isSGFLegendEnabled() {
        return getBoolForKey(R.string.prefs_sgf_legend, true);
    }

    private boolean isConstantLightWanted() {
        return getBoolForKey(R.string.prefs_constant_light, true);
    }

    private boolean isGridEmbossEnabled() {
        return getBoolForKey(R.string.prefs_grid_emboss, true);
    }

    private boolean isTsumegoPushEnabled() {
        return getBoolForKey(R.string.prefs_push_tsumego, true);
    }

    private String getUsername() {
        return getPreferences().getString(KEY_USERNAME, "");
    }

    private String getRank() {
        return getPreferences().getString(KEY_RANK, "");
    }


    private boolean getBoolForKey(@StringRes int stringRes, boolean defaultValue) {
        return getPreferences().getBoolean(ctx.getString(stringRes), defaultValue);
    }

}