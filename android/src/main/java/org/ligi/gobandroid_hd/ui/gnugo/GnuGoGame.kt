package org.ligi.gobandroid_hd.ui.gnugo

import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.ui.GoPrefs

class GnuGoGame internal constructor(val playingBlack: Boolean, val playingWhite: Boolean, val level: Byte, private val game: GoGame) {

    var aiIsThinking = false

    fun gnugoNowWhite(): Boolean {
        return !game.isBlackToMove && playingWhite
    }

    fun gnugoNowBlack(): Boolean {
        return game.isBlackToMove && playingBlack
    }

    fun setMetaDataForGame(app: App) {
        val metaData = game.metaData
        if (playingBlack) {
            metaData.blackName = app.getString(R.string.gnugo)
            metaData.blackRank = ""
        } else {
            metaData.blackName = GoPrefs.username
            metaData.blackRank = GoPrefs.rank
        }

        if (playingWhite) {
            metaData.whiteName = app.getString(R.string.gnugo)
            metaData.whiteRank = ""
        } else {
            metaData.whiteName = GoPrefs.username
            metaData.whiteRank = GoPrefs.rank
        }

    }
}
