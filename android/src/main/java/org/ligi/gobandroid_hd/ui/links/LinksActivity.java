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

package org.ligi.gobandroid_hd.ui.links;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import butterknife.Bind;
import butterknife.ButterKnife;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;

/**
 * Activity to load present the user GO-Relevant links ( Rules / SGF's / .. )
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 */

public class LinksActivity extends GobandroidFragmentActivity {

    @Bind(R.id.view_pager)
    ViewPager viewPager;

    @Bind(R.id.sliding_tabs)
    TabLayout titlePageIndicator;

    private class LinkListFragmentPager extends FragmentPagerAdapter {
        public LinkListFragmentPager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return getString(R.string.link_tab_about);
                case 1:
                    return getString(R.string.link_tab_credits);
                case 2:
                default:
                    return getString(R.string.link_tab_sgf);
            }
        }

        @Override
        public Fragment getItem(int i) {
            App.getTracker().trackEvent("ui_action", "links", i == 0 ? "about" : i == 1 ? "credits" : "sgf", null);
            switch (i) {
                case 0:
                    return new AboutListFragment();
                case 1:
                    return new CreditsListFragment();
                case 2:
                default:
                    return new SGFListFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.links_view_pager);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        ButterKnife.bind(this);

        viewPager.setAdapter(new LinkListFragmentPager(getSupportFragmentManager()));
        titlePageIndicator.setupWithViewPager(viewPager);

    }

}
