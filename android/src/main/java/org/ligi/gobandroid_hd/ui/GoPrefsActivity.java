/**
 * gobandroid
 * by Marcus -Ligi- Bueschleb
 * http://ligi.de
 * <p/>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 **/

package org.ligi.gobandroid_hd.ui;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

/**
 * Activity to edit the gobandroid game preferences
 *
 * @author <a href="http://ligi.de">Marcus -LiGi- Bueschleb </a>
 *         <p/>
 *         This software is licenced with GPLv3
 */
public class GoPrefsActivity extends GobandroidFragmentActivity {

    public static class GoPrefsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(final Bundle bundle, final String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            // fullscreen setting is not useful when fullscreen is forced for small devices
            final boolean isFullscreenUseful = !getResources().getBoolean(R.bool.force_fullscreen);
            findPreference(getString(R.string.prefs_fullscreen)).setVisible(isFullscreenUseful);
            findPreference(getString(R.string.prefs_push_tsumego)).setVisible(false); // TODO bring back

            findPreference(getString(R.string.prefs_sgf_legend)).setDependency(getString(R.string.prefs_do_legend));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.simple_container);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GoPrefsFragment()).commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        setTitle(R.string.settings);
    }
}
