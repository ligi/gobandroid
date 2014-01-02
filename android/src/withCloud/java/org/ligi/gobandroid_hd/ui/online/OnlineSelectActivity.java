/**
 * gobandroid 
 * by Marcus -Ligi- Bueschleb 
 * http://ligi.de
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as 
 * published by the Free Software Foundation; 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. 
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 **/

package org.ligi.gobandroid_hd.ui.online;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.tapfortap.TapForTap;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

/**
 * Activity to load present the user GO-Relevant links ( Rules / SGF's / .. )
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 */

public class OnlineSelectActivity extends GobandroidFragmentActivity implements ActionBar.TabListener {

    private String act_tab_str_for_analytics = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TapForTap.initialize(this, "D0343AA784E98E6F6362DBD9C1D541B6");

        this.setContentView(R.layout.content_with_ad);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.setTitle(R.string.online);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        int[] tabs = new int[]{R.string.online_create_game, /* R.string.online_your_games, */ R.string.online_view_game, R.string.online_join_game};

        for (int tab_str : tabs) {
            ActionBar.Tab tab = getSupportActionBar().newTab();
            tab.setText(tab_str);
            tab.setTag(tab_str);
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);
        }


        new ServerCheckAsyncTask(this).execute(); // this checks if the server is OK
    }

    public void changeFragment(Fragment newFragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.content, newFragment).commit();
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        App.getTracker().trackEvent("ui_action", "links", act_tab_str_for_analytics, null);

//
        switch ((Integer) tab.getTag()) {
            case R.string.online_join_game:
                new GamesListLoader(this, "public_invite").execute();
                break;

            case R.string.online_create_game:
                changeFragment(new OnlineCreateFragment());
                break;

            case R.string.online_view_game:
                new GamesListLoader(this, "public_watching").execute();
                break;


        }

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
    }

}
