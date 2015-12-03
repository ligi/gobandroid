package org.ligi.gobandroid_hd.model;

import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.logic.GoGame;

public class GameProvider {

    private final InteractionScope interactionScope;

    private GoGame game;

    public GameProvider(InteractionScope interactionScope) {
        this.interactionScope = interactionScope;
    }

    public GoGame get() {
        if (game == null) {
            game = new GoGame((byte) 9);
        }
        return game;
    }

    public void set(GoGame p_game) {
        interactionScope.ask_variant_session = true;

        if (game == null) {
            game = p_game;
        } else { // keep listeners and stuff
            game.setGame(p_game);
        }
    }

}
