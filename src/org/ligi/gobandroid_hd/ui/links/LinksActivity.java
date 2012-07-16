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

import org.ligi.android.common.adapter.LinkWithDescription;
import org.ligi.android.common.adapter.LinkWithDescriptionAndTitle;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.tracedroid.logging.Log;

import com.actionbarsherlock.app.ActionBar;
import com.google.analytics.tracking.android.EasyTracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Activity to load present the user GO-Relevant links ( Rules / SGF's / .. )
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         
**/

public class LinksActivity extends GobandroidFragmentActivity implements ActionBar.TabListener{

	private String act_tab_str_for_analytics="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.list);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        this.setTitle(R.string.link_title);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        int[] tabs=new int[] {R.string.link_tab_about,R.string.link_tab_credits,R.string.link_tab_sgf};

        for (int tab_str : tabs ) {
        	ActionBar.Tab tab = getSupportActionBar().newTab();
			tab.setText(tab_str);
			tab.setTag(tab_str); 
			tab.setTabListener(this);
			getSupportActionBar().addTab(tab);
        }
	    
        setList(tabs[0]);
    }

	public void changeFragment(Fragment newFragment) {
		getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment,newFragment).commit();
	}
	
	public void setList(int list) {
		Log.i("list"+ list);
		LinkWithDescription[] links=null;
		switch (list) {
			case R.string.link_tab_credits:
				act_tab_str_for_analytics="credits";
				links=new LinkWithDescription[] {
						new LinkWithDescriptionAndTitle("http://plus.google.com/104849265443273982798","idea / concept / code ","Ligi"),
						
						new LinkWithDescriptionAndTitle("http://gogameguru.com/","source of default Tsumego and commented game SGF's","gogameguru.com"),
						new LinkWithDescriptionAndTitle("http://actionbarsherlock.com/","library used for ActionBar backward compatibility ","ActionBarSherlock"),
						new LinkWithDescriptionAndTitle("http://plus.google.com/107941390233680026764","Sounds","Sebastian Blumtritt"),
						new LinkWithDescriptionAndTitle("http://plus.google.com/107473613683165260026","Japanese Translation","Hiroki Ino"),
						new LinkWithDescriptionAndTitle("http://plus.google.com/102354246669329539149","Chinese Translation","Noorudin Ma"),
						new LinkWithDescriptionAndTitle("http://transimple.de","German Translation","Dirk Blasejezak"),		
						new LinkWithDescriptionAndTitle("http://plus.google.com/109272815840179446675","French Translation #1","Sylvain Soliman"),
						new LinkWithDescriptionAndTitle("http://github.com/Zenigata","French Translation #2","Zenigata"),
						new LinkWithDescriptionAndTitle("http://github.com/p3l","Swedish Translation","Peter Lundqvist"),
						new LinkWithDescriptionAndTitle("http://plus.google.com/108208532767895844741","Russian Translation","Dmitriy Sklyar"),
						new LinkWithDescriptionAndTitle("http://plus.google.com/116001545198026111276","feedback & patches" , "Oren Laskin on Google+"),
						new LinkWithDescriptionAndTitle("http://plus.google.com/105303388887291066710","wooden background","Ruth -lironah- Hinckley on Google+"),
						new LinkWithDescriptionAndTitle("http://www.silvestre.com.ar/","GPL'd icons","Silvestre Herrera"),
						new LinkWithDescriptionAndTitle("http://www.sente.ch","FreegGoban stones","sente.ch"),
				};
			
				break;
				
			case R.string.link_tab_about:
				act_tab_str_for_analytics="about";
				links=new LinkWithDescription[] {
					new LinkWithDescriptionAndTitle("http://plus.google.com/106767057593220295403","for news, infos, feedback","Gobandroid Project Page"),
					new LinkWithDescription("http://github.com/ligi/gobandroid","Code/Issues on GitHub"),
					new LinkWithDescription("http://play.google.com/store/apps/details?id=org.ligi.gobandroid_hd","Google Play link"),
					new LinkWithDescription("http://gplv3.fsf.org/","GPLv3 License")					
				};					
			break;
			
			case R.string.link_tab_sgf:
				act_tab_str_for_analytics="sgf";
				links=new LinkWithDescription[] {

					// source pro games
					new LinkWithDescription("http://www.andromeda.com/people/ddyer/age-summer-94/companion.html","Companion"),
					
					new LinkWithDescription("http://homepages.cwi.nl/~aeb/go/games/games/Judan/","Judan"),
					new LinkWithDescription("http://gogameworld.com/gophp/pg_samplegames.php","Commented gogameworld sample games"),
					new LinkWithDescription("http://sites.google.com/site/byheartgo/","byheartgo"),
					new LinkWithDescription("http://gokifu.com/","gokifu"),
					
					// problems
					new LinkWithDescription("http://www.usgo.org/problems/index.html","USGo Problems" ),
					
					
					//mixed
					new LinkWithDescription("http://www.britgo.org/bgj/recent.html","Britgo recent")
					//dead not there anymore			new LinkWithDescription("http://egoban.org/@@recent_games","egoban"),
				};
			break;
		
		}
		changeFragment(new LinkListFragment(links));	
	}


	@Override
	public void onTabSelected(com.actionbarsherlock.app.ActionBar.Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
		EasyTracker.getTracker().trackEvent("ui_action", "links",act_tab_str_for_analytics,null);
		
		setList((Integer)tab.getTag());
	}

	@Override
	public void onTabUnselected(com.actionbarsherlock.app.ActionBar.Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(com.actionbarsherlock.app.ActionBar.Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
	}
    
}
