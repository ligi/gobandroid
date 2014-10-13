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

import android.graphics.Point;

import org.ligi.tracedroid.logging.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Class to represent a Go Game with its rules
 *
 * @authors <a href="http://ligi.de">Marcus -LiGi- Bueschleb </a> oren laskin
 * <p/>
 * This software is licenced with GPLv3
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
    public byte[][] area_assign; // cache to which player a area belongs in a
    // finished game

    private int group_count = -1;

    private int captures_white; // counter for the captures from black
    private int captures_black; // counter for the captures from white

    private int dead_white; // counter for the captures from black
    private int dead_black; // counter for the captures from white

    public int territory_white; // counter for the captures from black
    public int territory_black; // counter for the captures from white

    private byte handicap = 0;

    private float komi = 6.5f;

    private GoMove act_move = null;

    private GnuGoMover go_mover = null;

    private GoGameMetadata metadata = null;

    private int area_group_count = 0;

    public final static byte MOVE_VALID = 0;
    public final static byte MOVE_INVALID_NOT_ON_BOARD = 1;
    public final static byte MOVE_INVALID_CELL_NOT_FREE = 2;
    public final static byte MOVE_INVALID_CELL_NO_LIBERTIES = 3;
    public final static byte MOVE_INVALID_IS_KO = 4;
    public final static byte MOVE_INVALID = 5;

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

    public GoGame(byte size) {
        this(size, (byte) 0);
    }

    public GoGame(byte size, byte handicap) {
        init(size, handicap);
    }

    public void init(byte size, byte handicap) {

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

        if (GoDefinitions.getHandicapArray(size) != null) {
            byte[][] handicapArray = GoDefinitions.getHandicapArray(size);
            for (int i = 0; i < 9; i++) {
                if (i < handicap) {
                    handicap_board.setCellBlack(handicapArray[i][0], handicapArray[i][1]);
                    if (i == 5 || i == 7) {
                        handicap_board.setCellFree(handicapArray[4][0], handicapArray[4][1]);
                        handicap_board.setCellBlack(handicapArray[i + 1][0], handicapArray[i + 1][1]);
                    } else if (i == 6 || i == 8)
                        handicap_board.setCellBlack(handicapArray[4][0], handicapArray[4][1]);
                }
                all_handicap_positions[handicapArray[i][0]][handicapArray[i][1]] = true;
            }
        }

        apply_handicap();

        visual_board = calc_board.clone();
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
     * @param x
     * @param y
     * @return MOVE_VALID MOVE_INVALID_NOT_ON_BOARD MOVE_INVALID_CELL_NOT_FREE
     * MOVE_INVALID_CELL_NO_LIBERTIES MOVE_INVALID_IS_KO
     */
    public byte do_move(byte x, byte y) {
        Log.i("do_move x:" + x + " y:" + y);

        // check hard preconditions
        if ((x < 0) || (x >= calc_board.getSize()) || (y < 0) || (y >= calc_board.getSize())) {
            // return with INVALID if x and y are inside the board
            return MOVE_INVALID_NOT_ON_BOARD;
        }

        if (isFinished()) { // game is finished - players are marking dead stones
            return MOVE_VALID;
        }

        if (!calc_board.isCellFree(x, y)) { // can never place a stone where another is
            return MOVE_INVALID_CELL_NOT_FREE;
        }

        // check if the "new" move is in the variations - to not have 2 equal
        // move as different variations
        GoMove matching_move = null;

        for (GoMove move_matcher : act_move.getNextMoveVariations())
            if ((move_matcher.getX() == x) && (move_matcher.getY() == y))
                matching_move = move_matcher;

        // if there is one matching use this move and we are done
        if (matching_move != null) {
            jump(matching_move);
            return MOVE_VALID;
        }

        GoBoard bak_board = calc_board.clone();

        // int tmp_cap=captures_black+captures_white;

        if (isBlackToMove())
            calc_board.setCellBlack(x, y);
        else
            calc_board.setCellWhite(x, y);

        // buildGroups();
        remove_dead(x, y);
        // remove_dead((byte)0,(byte)0);

        // move is a KO -> Invalid
        if (calc_board.equals(pre_last_board)) {
            Log.i("illegal move -> KO");
            calc_board = bak_board.clone();
            return MOVE_INVALID_IS_KO;
        }

        if (!hasGroupLiberties(x, y)) {
            Log.i("illegal move -> NO LIBERTIES");
            calc_board = bak_board.clone();
            return MOVE_INVALID_CELL_NO_LIBERTIES;
        }

        // if we reach this point it is avalid move
        // -> do things needed to do after a valid move

        if (isBlackToMove())
            getGoMover().processBlackMove(x, y);
        else
            getGoMover().processWhiteMove(x, y);

        pre_last_board = last_board.clone();
        last_board = calc_board.clone();
        visual_board = calc_board.clone();

        act_move = new GoMove(x, y, act_move);

        if (!calc_board.isCellWhite(x, y))
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
        if (move.isFirstMove())
            return;

        if (move.isPassMove()) {
            return;
        }

        if (move.isBlackToMove())
            calc_board.setCellBlack(move.getX(), move.getY());
        else
            calc_board.setCellWhite(move.getX(), move.getY());

        if (move.didCaptures()) {
            buildGroups();
            remove_dead(move.getX(), move.getY());

            if (!calc_board.isCellWhite(move.getX(), move.getY()))
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

        List<GoMove> replay_moves = new ArrayList<GoMove>();

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

        visual_board = calc_board.clone();
        notifyGameChange();
    }

    public boolean cell_has_libertie(int x, int y) {

        return (((x != 0) && (calc_board.isCellFree(x - 1, y))) || ((y != 0) && (calc_board.isCellFree(x, y - 1))) || ((x != (calc_board.getSize() - 1)) && (calc_board.isCellFree(x + 1, y))) || ((y != (calc_board.getSize() - 1)) && (calc_board.isCellFree(
                x, y + 1))));
    }

    public boolean cell_has_white_neighbours(int x, int y) {

        return (((x != 0) && (calc_board.isCellWhite(x - 1, y))) || ((y != 0) && (calc_board.isCellWhite(x, y - 1))) || ((x != (calc_board.getSize() - 1)) && (calc_board.isCellWhite(x + 1, y))) || ((y != (calc_board.getSize() - 1)) && (calc_board
                .isCellWhite(x, y + 1))));
    }

    public boolean cell_has_black_neighbours(int x, int y) {

        return (((x != 0) && (calc_board.isCellBlack(x - 1, y))) || ((y != 0) && (calc_board.isCellBlack(x, y - 1))) || ((x != (calc_board.getSize() - 1)) && (calc_board.isCellBlack(x + 1, y))) || ((y != (calc_board.getSize() - 1)) && (calc_board
                .isCellBlack(x, y + 1))));
    }

    /**
     * check if a group has liberties
     *
     * @return boolean weather the group has liberty
     */
    public boolean hasGroupLiberties(int x, int y) {        /*
         * do a depth search first from point if (calc_board.isCellFree(x,y))
		 * return true;
		 */

        boolean checked_pos[][] = new boolean[calc_board.getSize()][calc_board.getSize()];
        final Stack<Integer> ptStackX = new Stack<>();
        final Stack<Integer> ptStackY = new Stack<>();

		/* Replace previous code with more efficient flood fill */
        ptStackX.push(x);
        ptStackY.push(y);

        while (!ptStackX.empty()) {
            int newx = ptStackX.pop();
            int newy = ptStackY.pop();

            if (cell_has_libertie(newx, newy)) {
                return true;
            } else {
                checked_pos[newx][newy] = true;
            }

			/* check to the left */
            if (newx > 0)
                if (calc_board.areCellsEqual(newx - 1, newy, newx, newy) && (checked_pos[newx - 1][newy] == false)) {
                    ptStackX.push(newx - 1);
                    ptStackY.push(newy);
                }
            /* check to the right */
            if (newx < calc_board.getSize() - 1)
                if (calc_board.areCellsEqual(newx + 1, newy, newx, newy) && (checked_pos[newx + 1][newy] == false)) {
                    ptStackX.push(newx + 1);
                    ptStackY.push(newy);
                }
            /* check down */
            if (newy > 0)
                if (calc_board.areCellsEqual(newx, newy - 1, newx, newy) && (checked_pos[newx][newy - 1] == false)) {
                    ptStackX.push(newx);
                    ptStackY.push(newy - 1);
                }
            /* check up */
            if (newy < calc_board.getSize() - 1)
                if (calc_board.areCellsEqual(newx, newy + 1, newx, newy) && (checked_pos[newx][newy + 1] == false)) {
                    ptStackX.push(newx);
                    ptStackY.push(newy + 1);
                }
        }

        return false;
    }

    public boolean isAreaGroupBlacks(int group2check) {
        if (group2check == -1)
            return false;
        boolean res = false;
        for (int xg = 0; xg < getBoardSize(); xg++)
            for (int yg = 0; yg < getBoardSize(); yg++)
                if (area_groups[xg][yg] == group2check)
                    if (cell_has_white_neighbours(xg, yg))
                        return false;
                    else
                        res |= (cell_has_black_neighbours(xg, yg));

        return res; // found no stone in the group with liberty
    }

    public boolean isAreaGroupWhites(int group2check) {
        if (group2check == -1)
            return false;
        boolean res = false;
        for (int xg = 0; xg < getBoardSize(); xg++)
            for (int yg = 0; yg < getBoardSize(); yg++)
                if (area_groups[xg][yg] == group2check)
                    if (cell_has_black_neighbours(xg, yg))
                        return false;
                    else
                        res |= (cell_has_white_neighbours(xg, yg));

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
        group_count = 0;

        // reset groups
        for (int x = 0; x < calc_board.getSize(); x++)
            for (int y = 0; y < calc_board.getSize(); y++)
                groups[x][y] = -1;

        Stack<Integer> ptStackX = new Stack<Integer>();
        Stack<Integer> ptStackY = new Stack<Integer>();

		/* Replace previous code with more efficient flood fill */
        for (int x = 0; x < calc_board.getSize(); x++)
            for (int y = 0; y < calc_board.getSize(); y++) {
                if (groups[x][y] == -1) {
                    ptStackX.push(x);
                    ptStackY.push(y);

                    while (!ptStackX.empty()) {
                        int newx = ptStackX.pop();
                        int newy = ptStackY.pop();
                        groups[newx][newy] = group_count;
                        /* check to the left */
                        if (newx > 0)
                            if (calc_board.areCellsEqual(newx - 1, newy, newx, newy) && (groups[newx - 1][newy] == -1)) {
                                ptStackX.push(newx - 1);
                                ptStackY.push(newy);
                            }
                        /* check to the right */
                        if (newx < calc_board.getSize() - 1)
                            if (calc_board.areCellsEqual(newx + 1, newy, newx, newy) && (groups[newx + 1][newy] == -1)) {
                                ptStackX.push(newx + 1);
                                ptStackY.push(newy);
                            }
						/* check down */
                        if (newy > 0)
                            if (calc_board.areCellsEqual(newx, newy - 1, newx, newy) && (groups[newx][newy - 1] == -1)) {
                                ptStackX.push(newx);
                                ptStackY.push(newy - 1);
                            }
						/* check up */
                        if (newy < calc_board.getSize() - 1)
                            if (calc_board.areCellsEqual(newx, newy + 1, newx, newy) && (groups[newx][newy + 1] == -1)) {
                                ptStackX.push(newx);
                                ptStackY.push(newy + 1);
                            }
                    }
                    group_count++;
                }
            }
    }

    public void buildAreaGroups() {
        area_group_count = 0;

        // reset groups
        for (int x = 0; x < calc_board.getSize(); x++)
            for (int y = 0; y < calc_board.getSize(); y++) {
                area_groups[x][y] = -1;
                area_assign[x][y] = 0;
            }

        for (byte x = 0; x < calc_board.getSize(); x++)
            for (byte y = 0; y < calc_board.getSize(); y++) {
                if (calc_board.isCellFree(x, y)) {

                    if (x > 0) {
                        if (!calc_board.areCellsEqual(x, y, (byte) (x - 1), y)) {
                            area_group_count++;
                            area_groups[x][y] = area_group_count;
                        } else
                            area_groups[x][y] = area_groups[x - 1][y];
                    } else {
                        area_group_count++;
                        area_groups[x][y] = area_group_count;
                    }

                    if (y > 0) {
                        if (calc_board.areCellsEqual(x, y, x, (byte) (y - 1))) {
                            int from_grp = area_groups[x][y];

                            for (int xg = 0; xg < calc_board.getSize(); xg++)
                                for (int yg = 0; yg < calc_board.getSize(); yg++)
                                    if (area_groups[xg][yg] == from_grp)
                                        area_groups[xg][yg] = area_groups[x][y - 1];
                        }
                    }

                }
            }

        territory_black = 0;
        territory_white = 0;
        for (int x = 0; x < calc_board.getSize(); x++)
            for (int y = 0; y < calc_board.getSize(); y++)
                if (isAreaGroupWhites(area_groups[x][y])) {
                    area_assign[x][y] = GoDefinitions.PLAYER_WHITE;
                    territory_white++;
                } else if (isAreaGroupBlacks(area_groups[x][y])) {
                    territory_black++;
                    area_assign[x][y] = GoDefinitions.PLAYER_BLACK;
                }
    }

    /**
     * remove dead groups from the board - e.g. after a move
     * <p/>
     * the cell with ignore_x and ignore_y is ignored - e.g. last move
     * <p/>
     * *
     */
    private void remove_dead(byte ignore_x, byte ignore_y) {
        local_captures = 0;

		/* check left */
        if (ignore_x > 0)
            if ((!hasGroupLiberties(ignore_x - 1, ignore_y)) && (!calc_board.areCellsEqual(ignore_x, ignore_y, ignore_x - 1, ignore_y)))
                remove_group(ignore_x - 1, (int) ignore_y);
		/* check right */
        if (ignore_x < calc_board.getSize() - 1)
            if ((!hasGroupLiberties(ignore_x + 1, ignore_y)) && (!calc_board.areCellsEqual(ignore_x, ignore_y, ignore_x + 1, ignore_y)))
                remove_group(ignore_x + 1, (int) ignore_y);
		/* check down */
        if (ignore_y > 0) {
            if ((!hasGroupLiberties(ignore_x, ignore_y - 1)) && (!calc_board.areCellsEqual(ignore_x, ignore_y, ignore_x, ignore_y - 1)))
                remove_group((int) ignore_x, ignore_y - 1);

        }
		/* check up */
        if (ignore_y < calc_board.getSize() - 1) {
            if ((!hasGroupLiberties(ignore_x, ignore_y + 1)) && (!calc_board.areCellsEqual(ignore_x, ignore_y, ignore_x, ignore_y + 1)))
                remove_group((int) ignore_x, ignore_y + 1);

        }
    }

    private void remove_group(int x, int y) {

        if (calc_board.isCellFree(x, y)) // this is no "group" in the sense we
            // want
            return;

        boolean checked_pos[][] = new boolean[calc_board.getSize()][calc_board.getSize()];
        Stack<Integer> ptStackX = new Stack<Integer>();
        Stack<Integer> ptStackY = new Stack<Integer>();

		/* Replace previous code with more efficient flood fill */
        ptStackX.push(x);
        ptStackY.push(y);
        checked_pos[x][y] = true;

        while (!ptStackX.empty()) {
            int newx = ptStackX.pop();
            int newy = ptStackY.pop();

			/* check to the left */
            if (newx > 0)
                if (calc_board.areCellsEqual(newx - 1, newy, newx, newy) && (checked_pos[newx - 1][newy] == false)) {
                    ptStackX.push(newx - 1);
                    ptStackY.push(newy);
                    checked_pos[newx - 1][newy] = true;
                }
			/* check to the right */
            if (newx < calc_board.getSize() - 1)
                if (calc_board.areCellsEqual(newx + 1, newy, newx, newy) && (checked_pos[newx + 1][newy] == false)) {
                    ptStackX.push(newx + 1);
                    ptStackY.push(newy);
                    checked_pos[newx + 1][newy] = true;
                }
			/* check down */
            if (newy > 0)
                if (calc_board.areCellsEqual(newx, newy - 1, newx, newy) && (checked_pos[newx][newy - 1] == false)) {
                    ptStackX.push(newx);
                    ptStackY.push(newy - 1);
                    checked_pos[newx][newy - 1] = true;
                }
			/* check up */
            if (newy < calc_board.getSize() - 1)
                if (calc_board.areCellsEqual(newx, newy + 1, newx, newy) && (checked_pos[newx][newy + 1] == false)) {
                    ptStackX.push(newx);
                    ptStackY.push(newy + 1);
                    checked_pos[newx][newy + 1] = true;
                }

            local_captures++;

            calc_board.setCellFree(newx, newy);

        }

    }

    /**
     * return if it's a handicap stone so that the view can visualize it
     * <p/>
     * TODO: check rename ( general marker )
     * <p/>
     * *
     */
    public boolean isPosHoschi(byte x, byte y) {
        return all_handicap_positions[x][y];
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

    public int getGroup(byte x, byte y) {
        return groups[x][y];
    }

    public byte getHandicap() {
        return handicap;
    }

    public GoBoard getHandicapBoard() {
        return handicap_board;
    }

    public void setGoMover(GnuGoMover go_mover) {
        this.go_mover = go_mover;
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

    public Point linear_coordinate2Point(int lin) {
        return new Point(lin % getSize(), lin / getSize());
    }

    public void setDeadWhite(int _dead_white) {
        dead_white = _dead_white;
    }

    public void setDeadBlack(int _dead_black) {
        dead_black = _dead_black;
    }

    /**
     * just content as state is not checked ( position in game )
     * <p/>
     * checks:
     * - size
     * - moves
     * - metadata ( TODO )
     *
     * @param other
     * @return
     */
    public boolean isContentEqualTo(GoGame other) {
        if (other.getBoardSize() != getBoardSize()) {
            return false;
        }

        return compareMovesRecusive(getFirstMove(), other.getFirstMove());
    }

    public boolean hasNextMove(GoMove move1, GoMove move2) {

        for (GoMove next_move : move1.getNextMoveVariations()) {
            if (next_move.isContentEqual(move2)) {
                return true;
            }
        }
        return false;
    }

    private boolean compareMovesRecusive(GoMove move1, GoMove move2) {
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
}