package org.ligi.gobandroid_hd.ui.tsumego;

import org.ligi.gobandroid_hd.logic.Cell;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoMove;

public class TsumegoHelper {


    static class Max {

        private int value;

        public Max(int value) {
            this.value = value;
        }

        public Max update(int candidate) {
            if (candidate > value) {
                value = candidate;
            }

            return this;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * find how big the action is on the board - assuming it is top left corner
     *
     * @param game
     * @return
     */
    public static int calcSpan(GoGame game, boolean with_moves) {
        Max max = new Max(0);
        for (Cell cell : game.getCalcBoard().getAllCells()) {
            if (!game.getHandicapBoard().isCellFree(cell)) {
                max.update(cell.x).update(cell.y);
            }
        }

        if (with_moves) {
            max.update(calcMaxMove(game.getFirstMove(), max).getValue());
        }

        return max.getValue();
    }

    public static Cell calcSpanAsPoint(GoGame game) {
        Max maxX=new Max(0);
        Max maxY=new Max(0);

        for (Cell cell : game.getCalcBoard().getAllCells()) {
            if (!game.getHandicapBoard().isCellFree(cell)) {
                maxX.update(cell.x);
                maxY.update(cell.y);
            }
        }

        return new Cell(maxX.getValue(),maxY.getValue());
    }


    public static Max calcMaxMove(GoMove move, Max act_max) {
        if (move == null)
            return act_max;

        for (GoMove variatonMove : move.getNextMoveVariations())
            act_max.update(calcMaxMove(variatonMove, act_max).getValue());

        if (move.getCell()!=null) {
            act_max.update(move.getCell().x).update(move.getCell().y);
        }

        return act_max;
    }

    /**
     * calculate a Zoom factor so that all stones in handicap fit on bottom
     * right area
     *
     * @return - the calculated Zoom factor
     */
    public static float calcZoom(GoGame game, boolean with_moves) {

        int max_span_size = calcSpan(game, with_moves);

        if (max_span_size == 0) // no predefined stones -> no zoom
            return 1.0f;

        float calculated_zoom = (float) game.getSize() / (max_span_size + 2);

        if (calculated_zoom < 1.0f)
            return 1.0f;
        else
            return calculated_zoom;
    }


    public static Cell calcPOI(GoGame game, boolean with_moves) {
        final int poi = (int) (game.getSize() / 2f / calcZoom(game, with_moves));
        return new Cell(poi,poi);
    }

    public static int calcTransform(GoGame game) {
        // we count 4 quadrants to find the hot spot
        int[] count_h = new int[2];
        int[] count_v = new int[2];

        for (Cell cell : game.getCalcBoard().getAllCells()) {
            if (!game.getVisualBoard().isCellFree(cell)) {
                count_h[(cell.x > (game.getSize() / 2)) ? 1 : 0]++;
                count_v[(cell.y > (game.getSize() / 2)) ? 1 : 0]++;
            }
        }

        return ((count_v[0] > count_v[1]) ? 0 : 1) + ((count_h[0] > count_h[1]) ? 0 : 2);
    }
}
