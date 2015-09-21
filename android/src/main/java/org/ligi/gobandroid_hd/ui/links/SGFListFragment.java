package org.ligi.gobandroid_hd.ui.links;

public class SGFListFragment extends LinkListFragment {
    @Override
    TwoLinedWithLink[] getData() {
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
