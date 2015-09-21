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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
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


    private interface TwoLined {
        String getLine1();

        String getLine2();

    }

    private static class LinkWithDescriptionAndTitle extends LinkWithDescription {

        private final String title;

        private LinkWithDescriptionAndTitle(final String link, final String description, final String title) {
            super(link, description);
            this.title = title;
        }

        @Override
        public String getLine1() {
            return title;
        }

        @Override
        public String getLine2() {
            return description;
        }
    }

    private static class LinkWithDescription implements TwoLined {
        public final String link;
        protected final String description;

        private LinkWithDescription(final String link, final String description) {
            this.link = link;
            this.description = description;
        }

        @Override
        public String getLine1() {
            return description;
        }

        @Override
        public String getLine2() {
            return link;
        }
    }

    public static class TwoLineRecyclerViewHolder extends RecyclerView.ViewHolder {

        @Bind(android.R.id.text1)
        TextView text1;

        @Bind(android.R.id.text2)
        TextView text2;

        public TwoLineRecyclerViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(TwoLined twoLined) {
            text1.setText(twoLined.getLine1());
            text2.setText(twoLined.getLine2());
        }
    }

    private static class TwoLineRecyclerAdapter extends RecyclerView.Adapter<TwoLineRecyclerViewHolder> {

        private final TwoLined[] twoLinedContent;

        private TwoLineRecyclerAdapter(@NonNull final TwoLined[] twoLinedContent) {
            this.twoLinedContent = twoLinedContent;
        }

        @Override
        public TwoLineRecyclerViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final LayoutInflater from = LayoutInflater.from(parent.getContext());
            return new TwoLineRecyclerViewHolder(from.inflate(R.layout.two_line_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final TwoLineRecyclerViewHolder holder, final int position) {
            holder.bind(twoLinedContent[position]);
        }

        @Override
        public int getItemCount() {
            return twoLinedContent.length;
        }
    }

    private String act_tab_str_for_analytics = "";

    public static abstract class LinkListFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
            RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
            recyclerView.setAdapter(new TwoLineRecyclerAdapter(getData()));
            recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
            return recyclerView;
        }

        abstract TwoLined[] getData();
    }

    public static class AboutListFragment extends LinkListFragment {
        @Override
        TwoLined[] getData() {
            return new LinkWithDescription[]{new LinkWithDescriptionAndTitle("http://plus.google.com/106767057593220295403",
                                                                             "for news, infos, feedback",
                                                                             "Gobandroid Project Page"),
                                             new LinkWithDescriptionAndTitle("https://plus.google.com/u/0/communities/113554258125816193874",
                                                                             "for questions and participation",
                                                                             "Gobandroid Community"),
                                             new LinkWithDescription("http://github.com/ligi/gobandroid", "Code/Issues on GitHub"),
                                             new LinkWithDescription("http://play.google.com/store/apps/details?id=org.ligi.gobandroid_hd", "Google Play link"),
                                             new LinkWithDescription("http://gplv3.fsf.org/", "GPLv3 License")};
        }
    }



    public static class SGFListFragment extends LinkListFragment {
        @Override
        TwoLined[] getData() {
            return new LinkWithDescription[]{

                    // source pro games
                    new LinkWithDescription("http://www.andromeda.com/people/ddyer/age-summer-94/companion.html", "Companion"),

                    new LinkWithDescription("http://homepages.cwi.nl/~aeb/go/games/games/Judan/", "Judan"),
                    new LinkWithDescription("http://gogameworld.com/gophp/pg_samplegames.php", "Commented gogameworld sample games"),
                    new LinkWithDescription("http://sites.google.com/site/byheartgo/", "byheartgo"),
                    new LinkWithDescription("http://gokifu.com/", "gokifu"),

                    // problems
                    new LinkWithDescription("http://www.usgo.org/problems/index.html", "USGo Problems"),

                    // mixed
                    new LinkWithDescription("http://www.britgo.org/bgj/recent.html", "Britgo recent")
                    // dead not there anymore new
                    // LinkWithDescription("http://egoban.org/@@recent_games","egoban"),
            };

        }
    }
    public static class CreditsListFragment extends LinkListFragment {

        @Override
        TwoLined[] getData() {
            return new LinkWithDescription[]{new LinkWithDescriptionAndTitle("http://plus.google.com/104849265443273982798", "idea / concept / code ", "Ligi"),

                                             new LinkWithDescriptionAndTitle("http://gogameguru.com/",
                                                                             "source of default Tsumego and commented game SGF's",
                                                                             "gogameguru.com"),
                                             new LinkWithDescriptionAndTitle("http://jakewharton.github.io/butterknife/", "library used", "ButterKnife"),
                                             new LinkWithDescriptionAndTitle("http://plus.google.com/107941390233680026764", "Sounds", "Sebastian Blumtritt"),
                                             new LinkWithDescriptionAndTitle("http://plus.google.com/107473613683165260026",
                                                                             "Japanese Translation",
                                                                             "Hiroki Ino"),
                                             new LinkWithDescriptionAndTitle("http://plus.google.com/102354246669329539149",
                                                                             "Chinese Translation",
                                                                             "Noorudin Ma"),
                                             new LinkWithDescriptionAndTitle("http://transimple.de", "German Translation", "Dirk Blasejezak"),
                                             new LinkWithDescriptionAndTitle("http://plus.google.com/109272815840179446675",
                                                                             "French Translation #1",
                                                                             "Sylvain Soliman"),
                                             new LinkWithDescriptionAndTitle("http://github.com/Zenigata", "French Translation #2", "Zenigata"),
                                             new LinkWithDescriptionAndTitle("http://github.com/p3l", "Swedish Translation", "Peter Lundqvist"),
                                             new LinkWithDescriptionAndTitle("http://plus.google.com/108208532767895844741",
                                                                             "Russian Translation",
                                                                             "Dmitriy Sklyar"),
                                             new LinkWithDescriptionAndTitle("http://plus.google.com/104678898719261371574",
                                                                             "Spanish and Catalan",
                                                                             "Toni Garcia-Die"),
                                             new LinkWithDescriptionAndTitle("http://plus.google.com/105766576009856509183",
                                                                             "Italian Translation",
                                                                             "Livio Lo Verso"),
                                             new LinkWithDescriptionAndTitle("http://plus.google.com/116001545198026111276",
                                                                             "feedback & patches",
                                                                             "Oren Laskin on Google+"),
                                             new LinkWithDescriptionAndTitle("http://plus.google.com/105303388887291066710",
                                                                             "wooden background",
                                                                             "Ruth -lironah- Hinckley on Google+"),
                                             new LinkWithDescriptionAndTitle("http://www.silvestre.com.ar/", "GPL'd icons", "Silvestre Herrera"),
                                             new LinkWithDescriptionAndTitle("http://www.sente.ch", "FreegGoban stones", "sente.ch")};
        }
    }

    private class CreateFragmentPager extends FragmentPagerAdapter {
        public CreateFragmentPager(FragmentManager fm) {
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

        viewPager.setAdapter(new CreateFragmentPager(getSupportFragmentManager()));

        titlePageIndicator.setupWithViewPager(viewPager);

        /*
        setTitle(R.string.link_title);
        final int[] tabs = new int[]{R.string.link_tab_about, R.string.link_tab_credits, R.string.link_tab_sgf};
        for (int tab_str : tabs) {
            ActionBar.Tab tab = getSupportActionBar().newTab();
            tab.setText(tab_str);
            tab.setTag(tab_str);
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);
        }

        setList(tabs[0]);
        */
    }

    public void changeFragment(Fragment newFragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment, newFragment).commit();
    }
/*
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

                        new LinkWithDescription("http://homepages.cwi.nl/~aeb/go/games/games/Judan/", "Judan"),
                        new LinkWithDescription("http://gogameworld.com/gophp/pg_samplegames.php", "Commented gogameworld sample games"),
                        new LinkWithDescription("http://sites.google.com/site/byheartgo/", "byheartgo"),
                        new LinkWithDescription("http://gokifu.com/", "gokifu"),

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
*/
}
