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
import static org.ligi.gobandroid_hd.logic.GoDefinitions.PLAYER_WHITE;
import static org.ligi.gobandroid_hd.logic.GoDefinitions.STONE_BLACK;
import static org.ligi.gobandroid_hd.logic.GoDefinitions.STONE_WHITE;


/**
 * Class to represent a Go Game with its rules
 */

public class GoGameScorer {

    private int[][] area_groups; // array to build groups
    public byte[][] area_assign; // cache to which player a area belongs in a  finished game

    public int territory_white; // counter for the captures from black
    public int territory_black; // counter for the captures from white

    public int dead_white;
    public int dead_black;

    private final GoGame game;

    public GoGameScorer(GoGame game) {
        this.game = game;
        area_groups = new int[game.getSize()][game.getSize()];
        area_assign = new byte[game.getSize()][game.getSize()];
    }

    public void calculateScore() {

        // reset groups
        final GoBoard calc_board = game.getCalcBoard();
        for (Cell cell : calc_board.getAllCells()) {
            area_groups[cell.x][cell.y] = -1;
            area_assign[cell.x][cell.y] = 0;
        }

        Set<Set<BoardCell>> areas = new HashSet<>();
        Set<Cell> processed = new HashSet<>();

        for (Cell cell : calc_board.getAllCells()) {
            boolean unprocessed = processed.add(cell);

            if (unprocessed) {
                final BoardCell boardCell = calc_board.getCell(cell);
                final AreaCellGatherer areaCellGatherer = new AreaCellGatherer(boardCell);

                if (boardCell.isFree()) {
                    areas.add(areaCellGatherer);
                    processed.addAll(areaCellGatherer.getProcessed());
                }
            }
        }

        int currentArea = 0;
        for (final Set<BoardCell> area : areas) {
            ++currentArea;
            for (final BoardCell boardCell : area) {
                area_groups[boardCell.x][boardCell.y] = currentArea;
            }
        }

        territory_black = 0;
        territory_white = 0;
        for (int x = 0; x < calc_board.getSize(); x++)
            for (int y = 0; y < calc_board.getSize(); y++)
                if (isAreaGroupWhites(area_groups[x][y])) {
                    area_assign[x][y] = PLAYER_WHITE;
                    territory_white++;
                } else if (isAreaGroupBlacks(area_groups[x][y])) {
                    territory_black++;
                    area_assign[x][y] = PLAYER_BLACK;
                }

        calculateDead();
        game.copyVisualBoard();
    }


    public boolean isAreaGroupBlacks(int group2check) {
        if (group2check == -1) return false;
        boolean res = false;
        for (BoardCell cell : game.getCalcBoard().getAllCells()) {
            if (area_groups[cell.x][cell.y] == group2check) if (game.cell_has_neighbour(cell, STONE_WHITE)) return false;
            else res |= (game.cell_has_neighbour(cell, STONE_BLACK));

        }

        return res; // found no stone in the group with liberty
    }

    public boolean isAreaGroupWhites(int group2check) {
        if (group2check == -1) return false;
        boolean res = false;
        for (BoardCell cell : game.getCalcBoard().getAllCells()) {
            if (area_groups[cell.x][cell.y] == group2check) if (game.cell_has_neighbour(cell, STONE_BLACK)) return false;
            else res |= (game.cell_has_neighbour(cell, STONE_WHITE));
        }
        return res; // found no stone in the group with liberty
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