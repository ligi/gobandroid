package org.ligi.gobandroid_hd.ui.links

class CreditsListFragment : LinkListFragment() {

    override fun getData() = arrayOf(
            LinkWithDescription("http://ligi.de", "idea / concept / code ", "Ligi"),

            LinkWithDescription("https://www.transifex.com/ligi/gobandroid/", "Transifex for managing translations", "Please join here to help localize"),
            LinkWithDescription("http://gogameguru.com/", "source of default Tsumego and commented game SGF's", "gogameguru.com"),
            LinkWithDescription("http://jakewharton.github.io/butterknife/", "Jake Wharton", "ButterKnife"),
            LinkWithDescription("https://developers.google.com/", "Google", "Android, Dagger, .."),
            LinkWithDescription("http://square.github.io/", "Square", "okhttp, assertj-android"),
            LinkWithDescription("https://kotlinlang.org/", "JetBrains Kotlin", "one awesome langueage used"),
            LinkWithDescription("https://github.com/greenrobot", "GreenRobot", "eventbus"),
            LinkWithDescription("https://github.com/N3TWORK/alphanum", "N3TWORK", "Alphanum comparator"),
            LinkWithDescription("http://jchardet.sourceforge.net/index.html", "jchardet", "jchardet is a java port of the source from mozilla's automatic charset detection algorithm"),
            LinkWithDescription("http://plus.google.com/107941390233680026764", "Sounds", "Sebastian Blumtritt"),
            LinkWithDescription("http://plus.google.com/107473613683165260026", "Japanese Translation", "Hiroki Ino"),
            LinkWithDescription("http://plus.google.com/102354246669329539149", "Chinese Translation", "Noorudin (玉錚爸), Ma"),
            LinkWithDescription("http://transimple.de", "German Translation", "Dirk Blasejezak"),
            LinkWithDescription("http://plus.google.com/109272815840179446675", "French Translation #1", "Sylvain Soliman"),
            LinkWithDescription("http://github.com/Zenigata", "French Translation #2", "Zenigata"),
            LinkWithDescription("http://github.com/p3l", "Swedish Translation", "Peter Lundqvist"),
            LinkWithDescription("http://plus.google.com/108208532767895844741", "Russian Translation", "Dmitriy Sklyar"),
            LinkWithDescription("http://plus.google.com/104678898719261371574", "Spanish and Catalan", "Toni Garcia-Die"),
            LinkWithDescription("http://plus.google.com/105766576009856509183", "Italian Translation", "Livio Lo Verso"),
            LinkWithDescription("http://plus.google.com/116001545198026111276", "feedback & patches", "Oren Laskin on Google+"),
            LinkWithDescription("https://plus.google.com/u/0/114810044949660525922", "PullRequests", "徐鸿 ( aka icehong )"),
            LinkWithDescription("http://plus.google.com/105303388887291066710", "wooden background", "Ruth -lironah- Hinckley on Google+"),
            LinkWithDescription("http://www.sente.ch", "FreegGoban stones", "sente.ch"),
            LinkWithDescription("https://github.com/gthazmatt", "Code contributions", "gthazmatt")
    )
}
