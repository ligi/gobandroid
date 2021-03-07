package org.ligi.gobandroid_hd.ui.links

import org.ligi.gobandroid_hd.R


class AboutListFragment : LinkListFragment() {
    override fun getData() = arrayOf(
            LinkWithDescription("http://plus.google.com/106767057593220295403", "for news, infos, feedback", "Gobandroid Project Page"),
            LinkWithDescription("https://plus.google.com/u/0/communities/113554258125816193874", "for questions and participation", "Gobandroid Community"),
            LinkWithDescription("http://github.com/ligi/gobandroid", "Code/Issues on GitHub"),
            LinkWithDescription("http://play.google.com/store/apps/details?id=org.ligi.gobandroid_hd", "Google Play link"),
            LinkWithDescription(requireContext().getString(R.string.wikipedia_rules_link), requireContext().getString(R.string.wikipedia_rules)),
            LinkWithDescription("http://gplv3.fsf.org/", "GPLv3 License")
    )
}
