/**
 * gobandroid
 * by Marcus -Ligi- Bueschleb
 * http://ligi.de
 * <p/>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 **/

package org.ligi.gobandroid_hd.logic;

import java.util.HashSet;
import java.util.Set;
import org.ligi.gobandroid_hd.logic.cell_gatherer.AreaCellGatherer;
import static org.ligi.gobandroid_hd.logic.GoDefinitions.PLAYER_BLACK;
import static org.ligi.gobandroid_hd.logic.GoDefinitions.PLAYER_NONE;
import static org.ligi.gobandroid_hd.logic.GoDefinitions.PLAYER_WHITE;

/**
 * Class to calculate score a GoGame
 */
public class GoGameScorer {

    public byte[][] area_assign; // cache to which player a area belongs in a  finished game

    public int territory_white; // counter for the captures from black
    public int territory_black; // counter for the captures from white

    public int dead_white;
    public int dead_black;

    private final GoGame game;

    public GoGameScorer(GoGame game) {
        this.game = game;
        area_assign = new byte[game.getSize()][game.getSize()];
    }

    public void calculateScore() {

        // reset groups
        final GoBoard calc_board = game.getCalcBoard();
        for (Cell cell : calc_board.getAllCells()) {
            area_assign[cell.x][cell.y] = 0;
        }

        territory_black = 0;
        territory_white = 0;

        Set<Cell> processed = new HashSet<>();

        for (Cell cell : calc_board.getAllCells()) {
            boolean unprocessed = processed.add(cell);

            if (unprocessed) {
                final BoardCell boardCell = calc_board.getCell(cell);
                final AreaCellGatherer areaCellGatherer = new AreaCellGatherer(boardCell);

                if (boardCell.isFree()) {

                    final byte assign;

                    if (containsOneOfButNotTheOther(areaCellGatherer.getProcessed(), PLAYER_BLACK)) {
                        assign = PLAYER_BLACK;
                        territory_black += areaCellGatherer.size();
                    } else if (containsOneOfButNotTheOther(areaCellGatherer.getProcessed(), PLAYER_WHITE)) {
                        assign = PLAYER_WHITE;
                        territory_white += areaCellGatherer.size();
                    } else {
                        assign = PLAYER_NONE;
                    }

                    if (assign > 0) for (final BoardCell areaBoardCell : areaCellGatherer) {
                        area_assign[areaBoardCell.x][areaBoardCell.y] = assign;
                    }
                    processed.addAll(areaCellGatherer.getProcessed());
                }
            }
        }


        calculateDead();
        game.copyVisualBoard();
    }


    private static boolean containsOneOfButNotTheOther(Set<BoardCell> set, byte kind) {
        boolean res = false;
        for (final BoardCell boardCell : set) {
            if (boardCell.is(GoDefinitions.theOtherKind(kind))) {
                return false;
            }
            res |= boardCell.is(kind);
        }

        return res;
    }

    public void calculateDead() {
        dead_white = 0;
        dead_black = 0;

        for (Cell cell : game.getCalcBoard().getAllCells()) {
            if (game.getCalcBoard().isCellDead(cell)) {
                if (game.getCalcBoard().isCellDeadBlack(cell)) {
                    dead_black++;
                } else if (game.getCalcBoard().isCellDeadWhite(cell)) {
                    dead_white++;
                }
            }
        }
    }

    public float getPointsWhite() {
        return game.getKomi() + game.getCapturesWhite() + territory_white + dead_black;
    }

    public float getPointsBlack() {
        return game.getCapturesBlack() + territory_black + dead_white;
    }

}