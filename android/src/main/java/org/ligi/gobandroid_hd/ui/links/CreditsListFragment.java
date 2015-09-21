package org.ligi.gobandroid_hd.ui.links;

public class CreditsListFragment extends LinkListFragment {

    @Override
    TwoLinedWithLink[] getData() {
        return new LinkWithDescription[]{new LinkWithDescriptionAndTitle("http://ligi.de", "idea / concept / code ", "Ligi"),

                                         new LinkWithDescriptionAndTitle("http://gogameguru.com/",
                                                                         "source of default Tsumego and commented game SGF's",
                                                                         "gogameguru.com"),
                                         new LinkWithDescriptionAndTitle("http://jakewharton.github.io/butterknife/", "library used", "ButterKnife"),
                                         new LinkWithDescriptionAndTitle("http://plus.google.com/107941390233680026764", "Sounds", "Sebastian Blumtritt"),
                                         new LinkWithDescriptionAndTitle("http://plus.google.com/107473613683165260026", "Japanese Translation", "Hiroki Ino"),
                                         new LinkWithDescriptionAndTitle("http://plus.google.com/102354246669329539149", "Chinese Translation", "Noorudin Ma"),
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
