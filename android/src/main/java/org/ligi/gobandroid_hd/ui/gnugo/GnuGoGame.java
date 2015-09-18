package org.ligi.gobandroid_hd.ui.gnugo;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGameMetadata;

class GnuGoGame {
    public final boolean playingBlack;
    public final boolean playingWhite;
    public final byte level;

    public boolean aiIsThinking = false;

    private final GoGame game;

    GnuGoGame(final boolean playingBlack, final boolean playingWhite, final byte level, final GoGame game) {
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
            metaData.setBlackName(app.getSettings().getUsername());
            metaData.setBlackRank(app.getSettings().getRank());
        }

        if (playingWhite) {
            metaData.setWhiteName(app.getString(R.string.gnugo));
            metaData.setWhiteRank("");
        } else {
            metaData.setWhiteName(app.getSettings().getUsername());
            metaData.setWhiteRank(app.getSettings().getRank());
        }

    }
}
