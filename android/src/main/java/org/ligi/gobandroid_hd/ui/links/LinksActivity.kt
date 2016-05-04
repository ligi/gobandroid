/**
 * gobandroid
 * by Marcus -Ligi- Bueschleb
 * http://ligi.de
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see //www.gnu.org/licenses/>.
 */

package org.ligi.gobandroid_hd.ui.links

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity

/**
 * Activity to load present the user GO-Relevant links ( Rules / SGF's / .. )

 * @author [Marcus -Ligi- Bueschleb](http://ligi.de)
 */

class LinksActivity : GobandroidFragmentActivity() {

    @BindView(R.id.view_pager)
    internal lateinit var viewPager: ViewPager

    @BindView(R.id.sliding_tabs)
    internal lateinit var titlePageIndicator: TabLayout

    class LinkFragmentItem(val title: String, val tag: String, val fragmentGetter: () -> Fragment)

    class LinkListFragmentPager(fm: FragmentManager, val items: Array<LinkFragmentItem>) : FragmentPagerAdapter(fm) {

        override fun getPageTitle(position: Int): CharSequence {
            return items[position].title
        }

        override fun getItem(i: Int): Fragment {
            App.getTracker().trackEvent("ui_action", "links", items[i].tag, null)
            return items[i].fragmentGetter()
        }

        override fun getCount(): Int {
            return items.size
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.links_view_pager)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
        }

        ButterKnife.bind(this)

        val items = arrayOf(
                LinkFragmentItem(getString(R.string.link_tab_about), "about", { AboutListFragment() }),
                LinkFragmentItem(getString(R.string.link_tab_videos), "videos", { VideoListFragment() }),
                LinkFragmentItem(getString(R.string.link_tab_sgf), "sgf", { SGFListFragment() }),
                LinkFragmentItem(getString(R.string.link_tab_credits), "credits", { CreditsListFragment() }))


        viewPager.adapter = LinkListFragmentPager(supportFragmentManager, items)
        titlePageIndicator.setupWithViewPager(viewPager)

    }

}
