package org.ligi.gobandroid_hd.ui.links;

public class LinkWithDescription implements TwoLinedWithLink {
    public final String link;
    protected final String description;

    public LinkWithDescription(final String link, final String description) {
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


    @Override
    public String getLink() {
        return link;
    }
}
