package org.ligi.gobandroid_hd.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.application.GoAndroidEnvironment

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
        findPreference<Preference>(getString(R.string.prefs_fullscreen))?.isVisible = isFullscreenUseful
        findPreference<Preference>(getString(R.string.prefs_push_tsumego))?.isVisible = false // TODO bring back
        findPreference<Preference>(getString(R.string.prefs_sgf_legend))?.dependency = getString(R.string.prefs_do_legend)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == getString(R.string.prefs_daynight)) {
            AppCompatDelegate.setDefaultNightMode(GoPrefs.getThemeInt())
            requireActivity().recreate()
        }
    }

}
