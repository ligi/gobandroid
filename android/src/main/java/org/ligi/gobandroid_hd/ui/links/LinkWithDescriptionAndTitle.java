package org.ligi.gobandroid_hd.ui.links;

public class LinkWithDescriptionAndTitle extends LinkWithDescription {

    private final String title;

    public LinkWithDescriptionAndTitle(final String link, final String description, final String title) {
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
