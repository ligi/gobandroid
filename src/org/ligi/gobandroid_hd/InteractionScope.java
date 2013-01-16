package org.ligi.gobandroid_hd;

import org.ligi.gobandroid_hd.logic.GoGame;

public class InteractionScope {

    public final static byte MODE_RECORD = 0;
    public final static byte MODE_TSUMEGO = 1;
    public final static byte MODE_REVIEW = 2;
    public final static byte MODE_GNUGO = 3;
    public final static byte MODE_TELEVIZE = 4;
    public final static byte MODE_COUNT = 5;
    public final static byte MODE_EDIT = 6;
    public final static byte MODE_SETUP = 7;


    public int touch_position = -1; // negative numbers -> no recent touch
    private byte mode;
    private boolean is_noif_mode = false;

    public boolean ask_variant_session = true;

    private GoGame game; // we will most likely interact with a game ;-)

    /**
     * set the game instance
     *
     * @param p_game the game to set as current
     */
    public void setGame(GoGame p_game) {
        ask_variant_session = true;

        if (game == null)
            game = p_game;
        else // keep listeners and stuff
            game.setGame(p_game);
    }

    /**
     * @return the game instance
     */
    public GoGame getGame() {
        if (game == null)
            game = new GoGame((byte) 9);
        return game;
    }

    public void setTouchPosition(int pos) {
        touch_position = pos;
    }

    public int getTouchPosition() {
        return touch_position;
    }

    public int getTouchX() {
        return touch_position % game.getSize();
    }

    public int getTouchY() {
        return touch_position / game.getSize();
    }

    public boolean hasValidTouchCoord() {
        return ((touch_position >= 0) && (touch_position < game.getSize()
                * game.getSize()));
    }

    public byte getMode() {
        return mode;
    }

    public void setMode(byte mode) {
        this.mode = mode;
    }

    public boolean is_in_noif_mode() {
        return is_noif_mode;
    }

    public void setIs_in_noif_mode(boolean is_noif_mode) {
        this.is_noif_mode = is_noif_mode;
    }

    public static int getModeStringRes(int mode) {

        switch (mode) {
            case InteractionScope.MODE_TSUMEGO:
                return R.string.tsumego;
            case InteractionScope.MODE_REVIEW:
                return R.string.review;
            case InteractionScope.MODE_RECORD:
                return R.string.play;
            case InteractionScope.MODE_TELEVIZE:
                return R.string.go_tv;
            case InteractionScope.MODE_COUNT:
                return R.string.count;
            case InteractionScope.MODE_GNUGO:
                return R.string.gnugo;
            case InteractionScope.MODE_EDIT:
                return R.string.edit;
            case InteractionScope.MODE_SETUP:
                return R.string.setup;
            default:
                return R.string.empty_str;
        }

    }
}
