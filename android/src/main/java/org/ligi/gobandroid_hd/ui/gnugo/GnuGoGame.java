package org.ligi.gobandroid_hd.ui.gnugo;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGameMetadata;
import org.ligi.gobandroid_hd.ui.application.GobandroidSettings;

import javax.inject.Inject;

public class GnuGoGame {

    public final boolean playingBlack;
    public final boolean playingWhite;
    public final byte level;
    private final GoGame game;

    public boolean aiIsThinking = false;

    @Inject
    GobandroidSettings settings;

    GnuGoGame(final boolean playingBlack, final boolean playingWhite, final byte level, final GoGame game) {
        App.component().inject(this);
        this.playingBlack = playingBlack;
        this.playingWhite = playingWhite;
        this.level = level;
        this.game = game;
    }

    public boolean gnugoNowWhite() {
        return !game.isBlackToMove() && playingWhite;
    }

    public boolean gnugoNowBlack() {
        return game.isBlackToMove() && playingBlack;
    }

    public void setMetaDataForGame(App app) {
        final GoGameMetadata metaData = game.getMetaData();
        if (playingBlack) {
            metaData.setBlackName(app.getString(R.string.gnugo));
            metaData.setBlackRank("");
        } else {
            metaData.setBlackName(settings.getUsername());
            metaData.setBlackRank(settings.getRank());
        }

        if (playingWhite) {
            metaData.setWhiteName(app.getString(R.string.gnugo));
            metaData.setWhiteRank("");
        } else {
            metaData.setWhiteName(settings.getUsername());
            metaData.setWhiteRank(settings.getRank());
        }

    }
}
