package org.ligi.gobandroid_hd.model

import org.ligi.gobandroid_hd.InteractionScope
import org.ligi.gobandroid_hd.logic.GoGame

class GameProvider(private val interactionScope: InteractionScope) {

    private var game: GoGame ? = null

    fun get(): GoGame {
        if (game == null) {
            game = GoGame(9.toByte().toInt())
        }
        return game!!
    }

    fun set(p_game: GoGame) {
        interactionScope.ask_variant_session = true

        game = p_game
    }

}
