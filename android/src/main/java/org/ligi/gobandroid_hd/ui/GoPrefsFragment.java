package org.ligi.gobandroid_hd.ui;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceFragmentCompat;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.application.GoAndroidEnvironment;

public class GoPrefsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(final Bundle bundle, final String rootKey) {
        App.component().inject(this);
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(GoAndroidEnvironment.Companion.getSettingsXMLName());
    }

    @Override
    public void onResume() {
        super.onResume();

        setPreferenceScreen(null);
        addPreferencesFromResource(R.xml.preferences);

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        // fullscreen setting is not useful when fullscreen is forced for small devices
        final boolean isFullscreenUseful = !getResources().getBoolean(R.bool.force_fullscreen);
        findPreference(getString(R.string.prefs_fullscreen)).setVisible(isFullscreenUseful);
        findPreference(getString(R.string.prefs_push_tsumego)).setVisible(false); // TODO bring back

        findPreference(getString(R.string.prefs_sgf_legend)).setDependency(getString(R.string.prefs_do_legend));
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.prefs_daynight))) {
            AppCompatDelegate.setDefaultNightMode(GoPrefs.INSTANCE.getThemeInt());
            recreateActivity();
        }
    }

    @TargetApi(11)
    public void recreateActivity() {
        if (Build.VERSION.SDK_INT >= 11) {
            getActivity().recreate();
        }
    }
}
