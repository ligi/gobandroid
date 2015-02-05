/**
 * gobandroid 
 * by Marcus -Ligi- Bueschleb 
 * http://ligi.de
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as 
 * published by the Free Software Foundation; 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. 
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 **/

package org.ligi.gobandroid_hd.logic;

import org.ligi.gobandroid_hd.logic.cell_gatherer.MustBeConnectedCellGatherer;
import org.ligi.tracedroid.logging.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.ligi.gobandroid_hd.logic.GoDefinitions.PLAYER_BLACK;
import static org.ligi.gobandroid_hd.logic.GoDefinitions.PLAYER_WHITE;
import static org.ligi.gobandroid_hd.logic.GoDefinitions.STONE_BLACK;
import static org.ligi.gobandroid_hd.logic.GoDefinitions.STONE_NONE;
import static org.ligi.gobandroid_hd.logic.GoDefinitions.STONE_WHITE;
import static org.ligi.gobandroid_hd.logic.GoDefinitions.getHandicapArray;

/**
 * Class to represent a Go Game with its rules
 */

public class GoGame {

    public interface GoGameChangeListener {
        public void onGoGameChange();
    }

    private Set<GoGameChangeListener> change_listeners = new HashSet<>();

    public void addGoGameChangeListener(GoGameChangeListener new_l) {
        change_listeners.add(new_l);
    }

    public void removeGoGameChangeListener(GoGameChangeListener l) {
        change_listeners.remove(l);
    }

    public void notifyGameChange() {
        change_listeners.removeAll(Collections.singleton(null));
        for (GoGameChangeListener l : change_listeners) {
            l.onGoGameChange();
        }
    }

    private GoBoard visual_board; // the board to show to the user
    private GoBoard calc_board; // the board calculations are done in
    private GoBoard last_board; // board to detect KO situations
    private GoBoard pre_last_board; // board to detect KO situations
    private GoBoard handicap_board;

    private int[][] groups; // array to build groups

    public int[][] area_groups; // array to build groups
    public byte[][] area_assign; // cache to which player a area belongs in a  finished game

    private int captures_white; // counter for the captures from black
    private int captures_black; // counter for the captures from white

    private int dead_white; // counter for the captures from black
    private int dead_black; // counter for the captures from white

    public int territory_white; // counter for the captures from black
    public int territory_black; // counter for the captures from white

    private int handicap = 0;

    private float komi = 6.5f;

    private GoMove act_move = null;

    private GnuGoMover go_mover = null;

    private GoGameMetadata metadata = null;

    public final static byte MOVE_VALID = 0;
    public final static byte MOVE_INVALID_NOT_ON_BOARD = 1;
    public final static byte MOVE_INVALID_CELL_NOT_FREE = 2;
    public final static byte MOVE_INVALID_CELL_NO_LIBERTIES = 3;
    public final static byte MOVE_INVALID_IS_KO = 4;

    private boolean[][] all_handicap_positions;

    private int local_captures = 0;

    public void setGame(GoGame game) {
        metadata = game.getMetaData();
        all_handicap_positions = game.all_handicap_positions;
        handicap = game.handicap;
        komi = game.komi;
        visual_board = game.visual_board;
        calc_board = game.calc_board;
        last_board = game.last_board;
        pre_last_board = game.pre_last_board;
        handicap_board = game.handicap_board;
        act_move = game.act_move;
        groups = game.groups;
        area_groups = game.area_groups;
        area_assign = game.area_assign;
        captures_black = game.captures_black;
        captures_white = game.captures_white;
        dead_black = game.dead_black;
        dead_white = game.dead_white;

        change_listeners.addAll(game.change_listeners);
    }

    public GoGame(int size) {
        this(size, 0);
    }

    public GoGame(int size, int handicap) {
        init(size, handicap);
    }

    public void init(int size, int handicap) {

        this.handicap = handicap;

        // create the boards

        metadata = new GoGameMetadata();

        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        metadata.setDate(format.format(new Date()));
        calc_board = new GoBoard(size);

        handicap_board = calc_board.clone();

        all_handicap_positions = new boolean[size][size];

        if (handicap > 0)
            setKomi(0.5f);

        if (getHandicapArray(size) != null) {
            byte[][] handicapArray = getHandicapArray(size);
            for (int i = 0; i < 9; i++) {
                if (i < handicap) {
                    handicap_board.setCell(new Cell(handicapArray[i][0], handicapArray[i][1]), STONE_BLACK);
                    if (i == 5 || i == 7) {
                        handicap_board.setCell(new Cell(handicapArray[4][0], handicapArray[4][1]), STONE_NONE);
                        handicap_board.setCell(new Cell(handicapArray[i + 1][0], handicapArray[i + 1][1]), STONE_BLACK);
                    } else if (i == 6 || i == 8)
                        handicap_board.setCell(new Cell(handicapArray[4][0], handicapArray[4][1]), STONE_BLACK);
                }
                all_handicap_positions[handicapArray[i][0]][handicapArray[i][1]] = true;
            }
        }

        apply_handicap();

        copyVisualBoard();
        last_board = calc_board.clone();
        pre_last_board = null;

        // create the array for group calculations
        groups = new int[size][size];

        area_groups = new int[size][size];
        area_assign = new byte[size][size];

        act_move = new GoMove(null);
        act_move.setIsFirstMove();
        act_move.setIsBlackToMove(handicap != 0); // if handicap==null set black
        // to move next - else set
        // white to move next
        reset();
    }

    public float getKomi() {
        return komi;
    }

    public void setKomi(float newKomi) {
        komi = newKomi;
    }

    public float getPointsWhite() {
        return komi + getCapturesWhite() + territory_white;
    }

    public float getPointsBlack() {
        return getCapturesBlack() + territory_black;
    }

    /**
     * set the handicap stones on the calc board
     */
    public void apply_handicap() {
        calc_board = handicap_board.clone();
    }

    public void reset() {
        pre_last_board = null;

        captures_black = 0;
        captures_white = 0;

        dead_black = 0;
        dead_white = 0;
    }

    public void pass() {
        if (act_move.isPassMove()) { // finish game if both passed
            act_move = new GoMove(act_move);
            act_move.setToPassMove();

            buildGroups();
            buildAreaGroups();
        } else {
            act_move = new GoMove(act_move);
            act_move.setToPassMove();
        }
        notifyGameChange();
    }

    /**
     * place a stone on the board
     *
     * @return MOVE_VALID MOVE_INVALID_NOT_ON_BOARD MOVE_INVALID_CELL_NOT_FREE
     * MOVE_INVALID_CELL_NO_LIBERTIES MOVE_INVALID_IS_KO
     */
    public byte do_move(Cell cell) {
        Log.i("do_move x:" + cell);

        // check hard preconditions
        if (!calc_board.isCellOnBoard(cell)) {
            // return with INVALID if x and y are inside the board
            return MOVE_INVALID_NOT_ON_BOARD;
        }

        if (isFinished()) { // game is finished - players are marking dead stones
            return MOVE_VALID;
        }

        if (!calc_board.isCellFree(cell)) { // can never place a stone where another is
            return MOVE_INVALID_CELL_NOT_FREE;
        }

        // check if the "new" move is in the variations - to not have 2 equal
        // move as different variations
        final GoMove matching_move = act_move.getNextMoveOnCell(cell);

        // if there is one matching use this move and we are done
        if (matching_move != null) {
            jump(matching_move);
            return MOVE_VALID;
        }

        final GoBoard bak_board = calc_board.clone();

        calc_board.setCell(cell, isBlackToMove() ? STONE_BLACK : STONE_WHITE);

        remove_dead(cell);

        // move is a KO -> Invalid
        if (calc_board.equals(pre_last_board)) {
            Log.i("illegal move -> KO");
            calc_board = bak_board.clone();
            return MOVE_INVALID_IS_KO;
        }

        if (!hasGroupLiberties(cell)) {
            Log.i("illegal move -> NO LIBERTIES");
            calc_board = bak_board.clone();
            return MOVE_INVALID_CELL_NO_LIBERTIES;
        }

        // if we reach this point it is a valid move
        // -> do things needed to do after a valid move

        if (isBlackToMove())
            getGoMover().processBlackMove(cell);
        else
            getGoMover().processWhiteMove(cell);

        pre_last_board = last_board.clone();
        last_board = calc_board.clone();
        copyVisualBoard();

        act_move = new GoMove(cell, act_move);

        if (!calc_board.isCellKind(cell, STONE_WHITE))
            captures_black += local_captures;
        else
            captures_white += local_captures;

        act_move.setDidCaptures(local_captures > 0);
        notifyGameChange();

        // if we reached this point this move must be valid
        return MOVE_VALID;
    }

    public GoMove getActMove() {
        return act_move;
    }

    public boolean canRedo() {
        return (act_move != null) && (act_move.hasNextMove());
    }

    public int getPossibleVariationCount() {
        if (act_move == null)
            return 0;
        return (act_move.getNextMoveVariationCount());
    }

    /**
     * moving without checks useful e.g. for undo / recorded games where we can
     * be sure that the move is valid and so be faster
     */
    public void do_internal_move(GoMove move) {

        act_move = move;
        if (move.isFirstMove() || move.isPassMove())
            return;

        calc_board.setCell(move.getCell(), move.isBlackToMove() ? STONE_BLACK : STONE_WHITE);

        if (move.didCaptures()) {
            buildGroups();
            remove_dead(move.getCell());

            if (calc_board.isCellKind(move.getCell(), STONE_BLACK))
                captures_black += local_captures;
            else
                captures_white += local_captures;

        }
    }

    public boolean canUndo() {
        return (!act_move.isFirstMove());// &&(!getGoMover().isMoversMove());
    }

    /**
     * undo the last move
     */
    public void undo() {
        _undo(true);
    }

    private void _undo(boolean keep_move) {
        getGoMover().paused = true;

        GoMove mLastMove = act_move;
        jump(mLastMove.getParent());
        if (!keep_move)
            mLastMove.destroy();
        getGoMover().undo();

        getGoMover().paused = false;
    }

    public void undo(boolean keep_move) {
        _undo(keep_move);

        if (canUndo() && (getGoMover().isMoversMove()))
            _undo(keep_move);
    }

    public void redo(int var) {
        Log.i("redoing " + act_move.getnextMove(var).toString());
        jump(act_move.getnextMove(var));
    }

    /**
     * @return the first move of the game
     */
    public GoMove getFirstMove() {
        GoMove move = getActMove();

        while (true) {
            if (move.isFirstMove())
                return move;
            move = move.getParent();
        }
    }

    public void refreshBoards() {
        jump(getActMove());
    }

    public void jumpFirst() {
        jump(getFirstMove());
    }

    public GoMove getLastMove() {
        GoMove move = getActMove();
        while (true) {
            if (!move.hasNextMove())
                return move;
            move = move.getnextMove(0);
        }
    }

    public void jumpLast() {
        jump(getLastMove());
    }

    public void jump(GoMove move) {

        if (move == null) {
            Log.w("move is null #shouldnothappen");
            return;
        }

        clear_calc_board();

        final List<GoMove> replay_moves = new ArrayList<>();

        replay_moves.add(move);
        GoMove tmp_move;
        while (true) {

            tmp_move = replay_moves.get(replay_moves.size() - 1);

            if (tmp_move.isFirstMove() || (tmp_move.getParent() == null))
                break;

            replay_moves.add(tmp_move.getParent());
        }

        reset();
        act_move = getFirstMove();

        for (int step = replay_moves.size() - 1; step >= 0; step--)
            do_internal_move(replay_moves.get(step));

        copyVisualBoard();
        notifyGameChange();
    }

    public void copyVisualBoard() {
        visual_board = calc_board.clone();
    }

    public boolean cell_has_neighbour(BoardCell boardCell, byte kind) {
        for (BoardCell cell : boardCell.getNeighbors())
            if (cell.is(kind))
                return true;

        return false;
    }

    /**
     * check if a group has liberties via flood fill
     *
     * @return boolean weather the group has liberty
     */
    public boolean hasGroupLiberties(Cell cell) {

        final BoardCell startCell = calc_board.getCell(cell);

        final AtomicBoolean found = new AtomicBoolean();

        new MustBeConnectedCellGatherer(startCell) {
            @Override
            public boolean add(BoardCell object) {
                if (cell_has_neighbour(object, STONE_NONE)) {
                    found.set(true);
                }
                return super.add(object);
            }

            @Override
            protected void pushWithCheck(BoardCell cell) {
                if (!found.get()) { // no need to process any more cells
                    super.pushWithCheck(cell);
                }
            }
        };

        return found.get();
    }

    public boolean isAreaGroupBlacks(int group2check) {
        if (group2check == -1)
            return false;
        boolean res = false;
        for (BoardCell cell : calc_board.getAllCells()) {
            if (area_groups[cell.x][cell.y] == group2check)
                if (cell_has_neighbour(cell, STONE_WHITE))
                    return false;
                else
                    res |= (cell_has_neighbour(cell, STONE_BLACK));

        }

        return res; // found no stone in the group with liberty
    }

    public boolean isAreaGroupWhites(int group2check) {
        if (group2check == -1)
            return false;
        boolean res = false;
        for (BoardCell cell : calc_board.getAllCells()) {
            if (area_groups[cell.x][cell.y] == group2check)
                if (cell_has_neighbour(cell, STONE_BLACK))
                    return false;
                else
                    res |= (cell_has_neighbour(cell, STONE_WHITE));
        }
        return res; // found no stone in the group with liberty
    }

    public void clear_calc_board() {
        apply_handicap();
    }

    /**
     * group the stones
     * <p/>
     * the result is written in groups[][]
     */
    public void buildGroups() {
        int group_count = 0;

        // reset groups
        for (int x = 0; x < calc_board.getSize(); x++)
            for (int y = 0; y < calc_board.getSize(); y++)
                groups[x][y] = -1;

        for (BoardCell boardCell : calc_board.getAllCells()) {
            if (groups[boardCell.x][boardCell.y] == -1 && !boardCell.is(GoDefinitions.STONE_NONE)) {

                for (BoardCell groupCell : new MustBeConnectedCellGatherer(boardCell)) {
                    groups[groupCell.x][groupCell.y] = group_count;
                }

                group_count++;
            }
        }
    }

    public void buildAreaGroups() {
        int area_group_count = 0;

        // reset groups
        for (Cell cell : calc_board.getAllCells()) {
            area_groups[cell.x][cell.y] = -1;
            area_assign[cell.x][cell.y] = 0;
        }

        for (Cell cell : calc_board.getAllCells()) {
            if (calc_board.isCellFree(cell)) {

                final BoardCell boardCell = calc_board.getCell(cell);

                if (boardCell.left!=null) {
                    if (!calc_board.areCellsEqual(boardCell, boardCell.left)) {
                        area_group_count++;
                        area_groups[cell.x][cell.y] = area_group_count;
                    } else
                        area_groups[cell.x][cell.y] = area_groups[cell.x - 1][cell.y];
                } else {
                    area_group_count++;
                    area_groups[cell.x][cell.y] = area_group_count;
                }

                if (boardCell.up!=null) {
                    final Cell up = boardCell.up;
                    if (calc_board.areCellsEqual(boardCell, up)) {
                        int from_grp = area_groups[cell.x][cell.y];

                        for (int xg = 0; xg < calc_board.getSize(); xg++)
                            for (int yg = 0; yg < calc_board.getSize(); yg++)
                                if (area_groups[xg][yg] == from_grp)
                                    area_groups[xg][yg] = area_groups[up.x][up.y];
                    }
                }

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
        copyVisualBoard();
        notifyGameChange();
    }

    /**
     * remove dead groups from the board - e.g. after a move
     * the cell with ignore_x and ignore_y is ignored - e.g. last move
     */
    private void remove_dead(Cell where) {
        local_captures = 0;

        BoardCell boardWhere = calc_board.getCell(where);

        for (BoardCell boardCell : boardWhere.getNeighbors())
            if ((!hasGroupLiberties(boardCell)) && (!calc_board.areCellsEqual(boardWhere, boardCell)))
                remove_group(boardCell);
    }

    private void remove_group(Cell where) {

        if (calc_board.isCellFree(where)) // this is no "group" in the sense we want
            return;

        final MustBeConnectedCellGatherer cellGathering = new MustBeConnectedCellGatherer(calc_board.getCell(where));

        for (BoardCell cell : cellGathering) {
            local_captures++;
            calc_board.setCell(cell, STONE_NONE);
        }
    }

    /**
     * return if it's a handicap stone so that the view can visualize it
     * <p/>
     * TODO: check rename ( general marker )
     * <p/>
     * *
     */
    public boolean isCellHoschi(Cell cell) {
        return all_handicap_positions[cell.x][cell.y];
    }

    public GoBoard getVisualBoard() {
        return visual_board;
    }

    public GoBoard getCalcBoard() {
        return calc_board;
    }

    public boolean isFinished() {
        if (getActMove().isFirstMove())
            return false;

        // need at least 2 moves to finish a game ( 2 passes )
        if (getActMove().getParent() == null)
            return false;

        // w passes
        return getActMove().isPassMove() && getActMove().getParent().isPassMove();
    }

    /**
     * @return who has to do the next move
     */
    public boolean isBlackToMove() {
        return (!act_move.isBlackToMove()); // the opposite of wo was to move
        // before
    }

    public int getCapturesBlack() {
        return captures_black + dead_white;
    }

    public int getCapturesWhite() {
        return captures_white + dead_black;
    }

    public int getBoardSize() {
        return calc_board.getSize(); // TODO cache?
    }

    public int getHandicap() {
        return handicap;
    }

    public GoBoard getHandicapBoard() {
        return handicap_board;
    }

    public GnuGoMover getGoMover() {
        if (go_mover == null)
            // an inactive "dummy" go mover
            return new GnuGoMover();
        return go_mover;
    }

    public GoGameMetadata getMetaData() {
        return metadata;
    }

    public void setMetadata(GoGameMetadata metadata) {
        this.metadata = metadata;
    }

    public int getSize() {
        return getVisualBoard().getSize();
    }

    /**
     * just content as state is not checked ( position in game )
     * <p/>
     * checks:
     * - size
     * - moves
     * - metadata ( TODO )
     */
    public boolean isContentEqualTo(GoGame other) {
        return other.getBoardSize() == getBoardSize() && compareMovesRecursive(getFirstMove(), other.getFirstMove());

    }

    public boolean hasNextMove(GoMove move1, GoMove move2) {

        for (GoMove next_move : move1.getNextMoveVariations()) {
            if (next_move.isContentEqual(move2)) {
                return true;
            }
        }
        return false;
    }

    private boolean compareMovesRecursive(GoMove move1, GoMove move2) {
        if (!move1.isContentEqual(move2)) {
            return false;
        }

        if (move1.hasNextMove() != move2.hasNextMove()) {
            return false;
        }

        if (move1.hasNextMove()) {
            for (GoMove next_move : move1.getNextMoveVariations()) {
                if (!hasNextMove(move1, next_move)) {
                    return false;
                }
            }
        }

        return true;
    }

    public void calculateDead() {
        dead_white = 0;
        dead_black = 0;

        for (Cell cell : getCalcBoard().getAllCells()) {
            if (getCalcBoard().isCellDead(cell)) {
                if (getCalcBoard().isCellDeadBlack(cell))
                    dead_black++;

                else if (getCalcBoard().isCellDeadWhite(cell))
                    dead_white++;
            }
        }
    }

}