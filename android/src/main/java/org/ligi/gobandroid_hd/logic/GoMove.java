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

import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import org.ligi.gobandroid_hd.logic.markers.GoMarker;
import java.util.Set;

import static org.ligi.gobandroid_hd.logic.GoDefinitions.*;

/**
 * Class to represent a Go Move
 */
public class GoMove {
    private Cell cell;
    private String comment = "";
    private boolean did_captures = false;
    private GoMove parent = null;

    private int move_pos = 0;
    private byte player = PLAYER_BLACK;
    private boolean black_to_move = true;
    private boolean isPassMove = false;
    private boolean isFirstMove = false;

    private final List<GoMove> next_move_variations = new ArrayList<>();
    private final List<GoMarker> markers = new ArrayList<>();
    private final List<Cell> captures = new ArrayList<>();

    public GoMove(GoMove parent) {
        this.parent = parent;
        if (parent != null) {
            black_to_move = !parent.isBlackToMove();
            player = parent.player == PLAYER_BLACK ? PLAYER_WHITE : PLAYER_BLACK;
            GoMove move = this;
            while (move != null && !move.isFirstMove()) {
                move_pos++;
                move = move.parent;
            }
        }
    }

    public GoMove(Cell cell, GoMove parent) {
        this(parent);

        this.cell = cell;
    }

    public GoMove(Cell cell, GoMove parent, StatefulGoBoard board) {
        this(cell, parent);
        //temporary
        byte previousStatus = board.getCellKind(cell);
        board.setCell(cell, getCellStatus());
        buildCaptures(board);
        board.setCell(cell, previousStatus);
    }

    public boolean isIllegalKo() {
        return parent != null &&
            captures.size() == 1 &&
            parent.captures.size() == 1 &&
            cell.isEqual(parent.captures.get(0));
    }

    public boolean isIllegalNoLiberties(StatefulGoBoard board) {
        if(!captures.isEmpty()) {
            return false;
        }

        //temporary
        byte previousStatus = board.getCellKind(cell);
        board.setCell(cell, getCellStatus());
        boolean hasLiberties = board.doesCellGroupHaveLiberty(cell);
        board.setCell(cell, previousStatus);
        return !hasLiberties;
    }

    public void apply(StatefulGoBoard board) {
        parent.addNextMove(this);
        //board.setCell(cell, getCellStatus());
        //board.setCellGroup(captures, STONE_NONE);
    }

    public void buildCaptures(StatefulGoBoard board) {
        StatelessBoardCell boardCell = board.getCell(cell);
        for(Cell neighbor : boardCell.getNeighbors()) {
            if(!captures.contains(neighbor) && !board.isCellFree(neighbor) && !board.areCellsEqual(neighbor, cell) && !board.doesCellGroupHaveLiberty(neighbor)) {
                Set<StatelessBoardCell> cellGroup = board.getCellGroup(neighbor);
                captures.addAll(cellGroup);
            }
        }
    }

    public void setDidCaptures(boolean did) {
        did_captures = did;
    }

    public boolean isCapturesMove() {
        return did_captures;
    }

    public int getMovePos() {
        return move_pos;
    }

    public boolean hasNextMove() {
        return (next_move_variations.size() > 0);
    }

    public GoMove getNextMoveOnCell(Cell cell) {
        for (GoMove next_move_variation : next_move_variations) {
            if (next_move_variation.getCell() != null && next_move_variation.getCell().equals(cell)) {
                return next_move_variation;
            }
        }
        return null;
    }

    public boolean hasNextMoveVariations() {
        return (next_move_variations.size() > 1);
    }

    public int getNextMoveVariationCount() {
        return next_move_variations.size();
    }

    public void addNextMove(GoMove move) {
        next_move_variations.add(move);
    }

    public void setToPassMove() {
        cell = null;
        isPassMove = true;
    }

    public boolean isPassMove() {
        return isPassMove;
    }

    public void setIsFirstMove() {
        isFirstMove = true;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public boolean isOnCell(Cell cell) {
        return cell == this.cell || this.cell != null && this.cell.equals(cell);
    }

    public GoMove getParent() {
        return parent;
    }

    public GoMove getnextMove(int pos) {
        return next_move_variations.get(pos);
    }

    public String toString() {
        String s = "{ cell=";
        if (cell != null) {
            s += cell.toString();
        } else {
            s += "null";
        }
        s += "; comment=" + comment;
        return s + "}";
    }

    public boolean hasComment() {
        return !comment.isEmpty();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String newComment) {
        comment = newComment;
    }

    public void addComment(String newComment) {
        comment += newComment;
    }

    public boolean didCaptures() {
        return did_captures;
    }

    public List<GoMove> getNextMoveVariations() {
        return next_move_variations;
    }

    /**
     * @return the markers - e.g. from SGF Problems
     */
    public List<GoMarker> getMarkers() {
        return markers;
    }

    public void addMarker(GoMarker marker) {
        markers.add(marker);
    }

    @Nullable
    public GoMarker getGoMarker() {
        if (parent != null) {
            for (GoMarker marker : parent.getMarkers()) {
                if (marker.equals(cell)) {
                    return marker;
                }
            }
        }
        return null;
    }

    public boolean isMarked() {
        return getGoMarker() == null;
    }

    public void destroy() {
        this.getParent().getNextMoveVariations().remove(this);
    }

    public boolean isBlackToMove() {
        return black_to_move;
    }

    public void setIsBlackToMove(boolean black_to_move) {
        this.black_to_move = black_to_move;
    }

    public boolean isContentEqual(GoMove other) {
        if (!other.isOnCell(cell)) {
            return false;
        }

        if (!comment.equals(other.getComment())) {
            return false;
        }

        if (getMarkers().size() != other.getMarkers().size()) {
            return false;
        }

        for (GoMarker marker : getMarkers()) {
            if (!other.getMarkers().contains(marker)) {
                return false;
            }
        }

        if (isBlackToMove() != other.isBlackToMove()) {
            return false;
        }

        if (getMovePos() != other.getMovePos()) {
            return false;
        }

        // TODO check if we are complete

        return true;
    }

    public Cell getCell() {
        return cell;
    }

    @CellStatus
    private byte getCellStatus() {
        return player == PLAYER_BLACK ? STONE_BLACK : STONE_WHITE;
    }
}
