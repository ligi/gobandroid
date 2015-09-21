package org.ligi.gobandroid_hd.ui.links;

public class AboutListFragment extends LinkListFragment {
    @Override
    TwoLinedWithLink[] getData() {
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
