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

package org.ligi.gobandroid_hd.ui.links;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import org.ligi.axt.adapters.LinkWithDescription;
import org.ligi.axt.adapters.LinkWithDescriptionAndTitle;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.tracedroid.logging.Log;

/**
 * Activity to load present the user GO-Relevant links ( Rules / SGF's / .. )
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 */

public class LinksActivity extends GobandroidFragmentActivity implements ActionBar.TabListener {

    private String act_tab_str_for_analytics = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.link_title);

        getSupportActionBar().setDisplayShowTitleEnabled(true);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        final int[] tabs = new int[]{R.string.link_tab_about, R.string.link_tab_credits, R.string.link_tab_sgf};
        for (int tab_str : tabs) {
            ActionBar.Tab tab = getSupportActionBar().newTab();
            tab.setText(tab_str);
            tab.setTag(tab_str);
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);
        }

        setList(tabs[0]);
    }

    public void changeFragment(Fragment newFragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment, newFragment).commit();
    }

    public void setList(int list) {
        Log.i("list" + list);
        final LinkWithDescription[] links = getLinkWithDescriptions(list);
        changeFragment(new LinkListFragment(links));
    }

    private LinkWithDescription[] getLinkWithDescriptions(int list) {
        switch (list) {
            case R.string.link_tab_credits:
                act_tab_str_for_analytics = "credits";
                return new LinkWithDescription[]{new LinkWithDescriptionAndTitle("http://plus.google.com/104849265443273982798", "idea / concept / code ", "Ligi"),

                        new LinkWithDescriptionAndTitle("http://gogameguru.com/", "source of default Tsumego and commented game SGF's", "gogameguru.com"),
                        new LinkWithDescriptionAndTitle("http://jakewharton.github.io/butterknife/", "library used", "ButterKnife"),
                        new LinkWithDescriptionAndTitle("https://code.google.com/p/guava-libraries/", "library used and nice fruit", "Guava"),
                        new LinkWithDescriptionAndTitle("http://plus.google.com/107941390233680026764", "Sounds", "Sebastian Blumtritt"), new LinkWithDescriptionAndTitle("http://plus.google.com/107473613683165260026", "Japanese Translation", "Hiroki Ino"),
                        new LinkWithDescriptionAndTitle("http://plus.google.com/102354246669329539149", "Chinese Translation", "Noorudin Ma"), new LinkWithDescriptionAndTitle("http://transimple.de", "German Translation", "Dirk Blasejezak"),
                        new LinkWithDescriptionAndTitle("http://plus.google.com/109272815840179446675", "French Translation #1", "Sylvain Soliman"), new LinkWithDescriptionAndTitle("http://github.com/Zenigata", "French Translation #2", "Zenigata"),
                        new LinkWithDescriptionAndTitle("http://github.com/p3l", "Swedish Translation", "Peter Lundqvist"), new LinkWithDescriptionAndTitle("http://plus.google.com/108208532767895844741", "Russian Translation", "Dmitriy Sklyar"),
                        new LinkWithDescriptionAndTitle("http://plus.google.com/104678898719261371574", "Spanish and Catalan", "Toni Garcia-Die"),
                        new LinkWithDescriptionAndTitle("http://plus.google.com/105766576009856509183", "Italian Translation", "Livio Lo Verso"),
                        new LinkWithDescriptionAndTitle("http://plus.google.com/116001545198026111276", "feedback & patches", "Oren Laskin on Google+"),
                        new LinkWithDescriptionAndTitle("http://plus.google.com/105303388887291066710", "wooden background", "Ruth -lironah- Hinckley on Google+"),
                        new LinkWithDescriptionAndTitle("http://www.silvestre.com.ar/", "GPL'd icons", "Silvestre Herrera"), new LinkWithDescriptionAndTitle("http://www.sente.ch", "FreegGoban stones", "sente.ch"),};

            case R.string.link_tab_about:
                act_tab_str_for_analytics = "about";
                return new LinkWithDescription[]{new LinkWithDescriptionAndTitle("http://plus.google.com/106767057593220295403", "for news, infos, feedback", "Gobandroid Project Page"),
                        new LinkWithDescriptionAndTitle("https://plus.google.com/u/0/communities/113554258125816193874", "for questions and participation", "Gobandroid Community"),
                        new LinkWithDescription("http://github.com/ligi/gobandroid", "Code/Issues on GitHub"), new LinkWithDescription("http://play.google.com/store/apps/details?id=org.ligi.gobandroid_hd", "Google Play link"),
                        new LinkWithDescription("http://gplv3.fsf.org/", "GPLv3 License")};

            case R.string.link_tab_sgf:
                act_tab_str_for_analytics = "sgf";
                return new LinkWithDescription[]{

                        // source pro games
                        new LinkWithDescription("http://www.andromeda.com/people/ddyer/age-summer-94/companion.html", "Companion"),

                        new LinkWithDescription("http://homepages.cwi.nl/~aeb/go/games/games/Judan/", "Judan"), new LinkWithDescription("http://gogameworld.com/gophp/pg_samplegames.php", "Commented gogameworld sample games"),
                        new LinkWithDescription("http://sites.google.com/site/byheartgo/", "byheartgo"), new LinkWithDescription("http://gokifu.com/", "gokifu"),

                        // problems
                        new LinkWithDescription("http://www.usgo.org/problems/index.html", "USGo Problems"),

                        // mixed
                        new LinkWithDescription("http://www.britgo.org/bgj/recent.html", "Britgo recent")
                        // dead not there anymore new
                        // LinkWithDescription("http://egoban.org/@@recent_games","egoban"),
                };
            default:
                return null; // not expected
        }

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        App.getTracker().trackEvent("ui_action", "links", act_tab_str_for_analytics, null);

        setList((Integer) tab.getTag());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
    }

}
