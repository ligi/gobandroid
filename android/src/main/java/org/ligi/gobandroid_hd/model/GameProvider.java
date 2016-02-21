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
        interactionScope.setAsk_variant_session(true);

        game = p_game;
    }

}
