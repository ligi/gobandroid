package org.ligi.gobandroid_hd.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import android.support.v7.preference.PreferenceFragmentCompat
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.application.GoAndroidEnvironment
import org.ligi.kaxt.recreateWhenPossible

class GoPrefsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(bundle: Bundle?, rootKey: String?)
            = setPreferencesFromResource(R.xml.preferences, rootKey)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager.sharedPreferencesName = GoAndroidEnvironment.settingsXMLName
    }

    override fun onResume() {
        super.onResume()

        preferenceScreen = null
        addPreferencesFromResource(R.xml.preferences)

        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        // fullscreen setting is not useful when fullscreen is forced for small devices
        val isFullscreenUseful = !resources.getBoolean(R.bool.force_fullscreen)
        findPreference(getString(R.string.prefs_fullscreen)).isVisible = isFullscreenUseful
        findPreference(getString(R.string.prefs_push_tsumego)).isVisible = false // TODO bring back

        findPreference(getString(R.string.prefs_sgf_legend)).dependency = getString(R.string.prefs_do_legend)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == getString(R.string.prefs_daynight)) {
            AppCompatDelegate.setDefaultNightMode(GoPrefs.getThemeInt())
            activity.recreateWhenPossible()
        }
    }

}
