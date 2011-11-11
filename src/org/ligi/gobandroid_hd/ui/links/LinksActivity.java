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
import org.ligi.tracedroid.logging.Log;

import android.os.Bundle;
import android.support.v4.app.ActionBar.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentTransaction;

/**
 * Activity to load SGF's from SD card
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         
**/

public class LinksActivity extends FragmentActivity implements ActionBar.TabListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.list);
        //getSupportFragmentManager().beginTransaction().add(R.id.list_fragment,new SGFOnlineListFragment()).commit();
  

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        String[] tabs=new String[] {"About","Help","SGF's"};
        int i=0;
        for (String tab_str : tabs ) {
        	ActionBar.Tab tab = getSupportActionBar().newTab();
			tab.setText(tab_str);
			tab.setTag(i++);
			tab.setTabListener(this);
			getSupportActionBar().addTab(tab);
        }
	
        
        setList(0);
    }

	public void changeFragment(Fragment newFragment) {
		getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment,newFragment).commit();
	}
	
	public void setList(int list) {
		Log.i("list"+ list);
		LinkWithDescription[] links=null;
		switch (list) {
			case 0:
			links=new LinkWithDescription[] {
					new LinkWithDescriptionAndTitle("https://plus.google.com/b/106767057593220295403/","for news, infos, feedback","Gobandroid Project Page"),
					new LinkWithDescriptionAndTitle("https://plus.google.com/104849265443273982798","idea / concept / code ","Ligi on Google+"),
					new LinkWithDescriptionAndTitle("http://gogameguru.com/","source of default Tsumego and commented game SGF's","gogameguru.com"),
					new LinkWithDescriptionAndTitle("https://plus.google.com/116001545198026111276","feedback & patches" , "Oren Laskin on Google+"),
					new LinkWithDescriptionAndTitle("https://plus.google.com/105303388887291066710","wooden background","Ruth -lironah- Hinckley on Google+"),
					new LinkWithDescriptionAndTitle("http://www.sente.ch","FreegGoban stones","sente.ch"),
					new LinkWithDescription("https://github.com/ligi/gobandroid","Code/Issues on GitHub"),
					new LinkWithDescription("http://gplv3.fsf.org/","GPLv3 License")					
			};					
		break;
	
		case 2:
			links=new LinkWithDescription[] {

					// source pro games
					new LinkWithDescription("http://www.andromeda.com/people/ddyer/age-summer-94/companion.html","Companion"),
					new LinkWithDescription("http://homepages.cwi.nl/~aeb/go/games/games/Judan/","Judan"),
					new LinkWithDescription("http://gogameworld.com/gophp/pg_samplegames.php","Commented gogameworld sample games"),
					new LinkWithDescription("http://sites.google.com/site/byheartgo/","byheartgo"),
								
					// problems
					new LinkWithDescription("http://www.usgo.org/problems/index.html","USGo Problems" ),
					
					//mixed
					new LinkWithDescription("http://www.britgo.org/bgj/recent.html","Britgo recent")
					//dead not there anymore			new LinkWithDescription("http://egoban.org/@@recent_games","egoban"),
			};
			break;
			case 1:
				links=new LinkWithDescription[] {
						new LinkWithDescription("http://en.wikipedia.org/wiki/Rules_of_Go","Wikipedia Article"),
						new LinkWithDescription("http://www.youtube.com/watch?v=gECcsSeRcNo","Tutorial on YouTube 1"),
						new LinkWithDescription("http://www.youtube.com/watch?v=UW8822OoihY","Tutorial on YouTube 2"),
						new LinkWithDescription("http://www.youtube.com/watch?v=t_ZRe3wGIUM","Tutorial on YouTube 3"),
						new LinkWithDescription("http://www.youtube.com/watch?v=rCi9vgvLdI0","Tutorial on YouTube 3a")
				};
			break;
					}
		changeFragment(new LinkListFragment(links));	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		setList((Integer)tab.getTag());
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
    
}
