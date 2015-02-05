package org.ligi.gobandroid_hd;

import org.ligi.gobandroid_hd.logic.BoardCell;
import org.ligi.gobandroid_hd.logic.Cell;

public class InteractionScope {

    public final static byte MODE_RECORD = 0;
    public final static byte MODE_TSUMEGO = 1;
    public final static byte MODE_REVIEW = 2;
    public final static byte MODE_GNUGO = 3;
    public final static byte MODE_TELEVIZE = 4;
    public final static byte MODE_COUNT = 5;
    public final static byte MODE_EDIT = 6;
    public final static byte MODE_SETUP = 7;

    public Cell touch_position = null;
    private byte mode;
    private boolean is_noif_mode = false;

    public boolean ask_variant_session = true;

    public void setTouchPosition(Cell touchCell) {
        touch_position = touchCell;
    }

    public Cell getTouchCell() {
        return touch_position;
    }

    public boolean hasValidTouchCoord() {
        return touch_position!=null && App.getGame().getCalcBoard().getCell(touch_position)!=null;
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

    /**
     * @return some ensured touch_position - if there was none set to (0,0)
     */
    public Cell getEnsuredTouchPosition() {
        if (touch_position == null) {
            touch_position = new Cell(0, 0);
        }
        return touch_position;
    }
}
