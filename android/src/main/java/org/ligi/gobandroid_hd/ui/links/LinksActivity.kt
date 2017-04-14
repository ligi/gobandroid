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
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.links_view_pager.*
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity

/**
 * Activity to load present the user GO-Relevant links ( Rules / SGF's / .. )

 * @author [Marcus -Ligi- Bueschleb](http://ligi.de)
 */

class LinksActivity : GobandroidFragmentActivity() {


    class LinkFragmentItem(val title: String, val tag: String, val fragmentGetter: () -> Fragment)

    class LinkListFragmentPager(fm: FragmentManager, val items: Array<LinkFragmentItem>) : FragmentPagerAdapter(fm) {

        override fun getPageTitle(position: Int): CharSequence {
            return items[position].title
        }

        override fun getItem(i: Int): Fragment {
            App.tracker.trackEvent("ui_action", "links", items[i].tag, null)
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

        val items = arrayOf(
                LinkFragmentItem(getString(R.string.link_tab_about), "about", { AboutListFragment() }),
                LinkFragmentItem(getString(R.string.link_tab_videos), "videos", { VideoListFragment() }),
                LinkFragmentItem(getString(R.string.link_tab_sgf), "sgf", { SGFListFragment() }),
                LinkFragmentItem(getString(R.string.link_tab_credits), "credits", { CreditsListFragment() }))


        view_pager.adapter = LinkListFragmentPager(supportFragmentManager, items)
        sliding_tabs.setupWithViewPager(view_pager)

    }

}
