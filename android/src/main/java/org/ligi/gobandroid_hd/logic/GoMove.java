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
import org.ligi.gobandroid_hd.logic.GoGame.MoveStatus;
import org.ligi.gobandroid_hd.logic.markers.GoMarker;
import org.ligi.tracedroid.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.ligi.gobandroid_hd.logic.GoDefinitions.*;

/**
 * Class to represent a Go Move
 */
public class GoMove {
    private Cell cell;
    private String comment = "";
    private GoMove parent = null;

    private int move_pos = 0;
    private byte player = PLAYER_BLACK;
    private boolean isPassMove = false;
    private boolean isFirstMove = false;

    private final List<GoMove> next_move_variations = new ArrayList<>();
    private final List<GoMarker> markers = new ArrayList<>();
    private final List<Cell> captures = new ArrayList<>();

    public GoMove(GoMove parent) {
        this.parent = parent;
        if (parent != null) {
            player = parent.player == PLAYER_BLACK ? PLAYER_WHITE : PLAYER_BLACK;
            move_pos = parent.move_pos + 1;
        }
    }

    public GoMove(Cell cell, GoMove parent) {
        this(parent);
        this.cell = cell;
    }

    public GoMove(Cell cell, GoMove parent, StatefulGoBoard board) {
        this(cell, parent);
        if (!board.isCellOnBoard(cell)) {
            return;
        }

        buildCaptures(board);
    }

    public void apply(StatefulGoBoard board) {
        parent.addNextMove(this);
        board.setCell(cell, (byte) getCellStatus());
        board.setCellGroup(captures, STONE_NONE);
    }

    public GoMove undo(StatefulGoBoard board, boolean keepMove) {
        if(parent == null) {
            return this;
        } else if(!keepMove) {
            parent.next_move_variations.remove(this);
        }

        if(cell != null) {
            board.setCell(cell, STONE_NONE);
            board.setCellGroup(captures, (byte) getCapturedCellStatus());
        }

        return parent;
    }

    public GoMove redo(StatefulGoBoard board, GoMove next) {
        if(!next_move_variations.contains(next)) {
            return this;
        }

        next.apply(board);
        return next;
    }

    public MoveStatus repostition(StatefulGoBoard board, Cell cell) {
        if(cell == null || !board.isCellOnBoard(cell)) {
            return MoveStatus.INVALID_NOT_ON_BOARD;
        }

        undo(board, false);
        Cell previousCell = this.cell;
        this.cell = cell;
        buildCaptures(board);
        MoveStatus errorStatus = getErrorStatus(board);
        if(errorStatus == null) {
            apply(board);
            return MoveStatus.VALID;
        } else {
            //the move is invalid, so back out the changes
            this.cell = previousCell;
            buildCaptures(board);
            apply(board);
            return errorStatus;
        }
    }

    public MoveStatus getErrorStatus(StatefulGoBoard board) {
        // check hard preconditions
        if (!board.isCellOnBoard(cell)) {
            // return with INVALID if x and y are inside the board
            return MoveStatus.INVALID_NOT_ON_BOARD;
        } else if (!board.isCellFree(cell)) {
            // can never place a stone where another is
            return MoveStatus.INVALID_CELL_NOT_FREE;
        } else if (isIllegalKo()) {
            Log.i("illegal move -> KO");
            return MoveStatus.INVALID_IS_KO;
        } else if (isIllegalNoLiberties(board)) {
            Log.i("illegal move -> NO LIBERTIES");
            return MoveStatus.INVALID_CELL_NO_LIBERTIES;
        }

        return null;
    }

    private boolean isIllegalKo() {
        return parent != null &&
            captures.size() == 1 &&
            parent.captures.size() == 1 &&
            parent.captures.get(0).isEqual(cell);
    }

    private boolean isIllegalNoLiberties(StatefulGoBoard board) {
        if(!captures.isEmpty()) {
            return false;
        }

        byte previousStatus = board.getCellKind(cell);
        board.setCell(cell, (byte) getCellStatus());
        boolean hasLiberties = board.doesCellGroupHaveLiberty(cell);
        board.setCell(cell, previousStatus);
        return !hasLiberties;
    }

    private void buildCaptures(StatefulGoBoard board) {
        captures.clear();

        //temporarily apply the move in order to calculate captures
        byte previousStatus = board.getCellKind(cell);
        board.setCell(cell, (byte) getCellStatus());

        StatelessBoardCell boardCell = board.getCell(cell);
        for(Cell neighbor : boardCell.getNeighbors()) {
            if(!captures.contains(neighbor) && !board.isCellFree(neighbor) && !board.areCellsEqual(neighbor, cell) && !board.doesCellGroupHaveLiberty(neighbor)) {
                Set<StatelessBoardCell> cellGroup = board.getCellGroup(neighbor);
                captures.addAll(cellGroup);
            }
        }

        //reset the cell to its original position
        board.setCell(cell, previousStatus);
    }

    public int getMovePos() {
        return move_pos;
    }

    public byte getPlayer() {
        return player;
    }

    public void setPlayer(byte player) {
        this.player = player;
    }

    public boolean hasNextMove() {
        return !next_move_variations.isEmpty();
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
        return next_move_variations.size() > 1;
    }

    public int getNextMoveVariationCount() {
        return next_move_variations.size();
    }

    public void addNextMove(GoMove move) {
        if (!next_move_variations.contains(move)) {
            next_move_variations.add(move);
        }
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

    public Cell getCell() {
        return cell;
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
        return next_move_variations.size() > pos ? next_move_variations.get(pos) : null;
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

    public List<Cell> getCaptures() {
        return captures;
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

    public boolean isContentEqual(GoMove other) {
        if(!other.isOnCell(cell) || !comment.equals(other.comment) || markers.size() != other.markers.size()) {
            return false;
        }

        for (GoMarker marker : getMarkers()) {
            if (!other.getMarkers().contains(marker)) {
                return false;
            }
        }

        if (player != other.player || move_pos != other.move_pos) {
            return false;
        }

        // TODO check if we are complete
        return true;
    }

    public boolean isFinalMove() {
        return !isFirstMove && parent != null && isPassMove && parent.isPassMove;

    }

    @CellStatus
    private int getCellStatus() {
        return player == PLAYER_BLACK ? STONE_BLACK : STONE_WHITE;
    }

    @CellStatus
    private int getCapturedCellStatus() {
        return player == PLAYER_BLACK ? STONE_WHITE : STONE_BLACK;
    }

}
