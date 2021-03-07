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

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * some definitions for the game of Go
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         <p/>
 *         This software is licenced with GPLv3
 */
public class GoDefinitions {

    public final static byte PLAYER_NONE = 0;
    public final static byte PLAYER_BLACK = 1;
    public final static byte PLAYER_WHITE = 2;


    /**
     * @param kind - the kind to turn around
     * @return if white -> black
     * if black -> white
     * anything else -> black ( should not be used )
     */
    public static byte theOtherKind(byte kind) {
        if (kind == PLAYER_BLACK) {
            return PLAYER_WHITE;
        }
        return PLAYER_BLACK;
    }

    @IntDef({STONE_NONE, STONE_BLACK, STONE_WHITE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CellStatus {
    }


    public final static byte STONE_NONE = 0;
    public final static byte STONE_BLACK = 1;
    public final static byte STONE_WHITE = 2;

    private final static byte[][] hoshis19x19 = {{15, 3}, {3, 15}, {15, 15}, {3, 3}, {9, 9}, {3, 9}, {15, 9}, {9, 3}, {9, 15}};

    private final static byte[][] hoshis13x13 = {{9, 3}, {3, 9}, {9, 9}, {3, 3}, {6, 6}, {3, 6}, {9, 6}, {6, 3}, {6, 9}};

    private final static byte[][] hoshis9x9 = {{6, 2}, {2, 6}, {6, 6}, {2, 2}, {4, 4}, {2, 4}, {6, 4}, {4, 2}, {4, 6}};

    /**
     * @param board_size - return handicap array for which board_size
     * @return handicap array
     */
    @Nullable
    public static byte[][] getHandicapArray(int board_size) {
        switch (board_size) {
            case 9:
                return hoshis9x9;
            case 13:
                return hoshis13x13;
            case 19:
                return hoshis19x19;
        }
        return null; // no handicap definition for this size
    }


    public static char getStringFromCellStatus(@CellStatus int cellStatus, boolean unicode) {
        switch (cellStatus) {
            case GoDefinitions.STONE_BLACK:
                return unicode ? '⚫' : 'B';
            case GoDefinitions.STONE_WHITE:
                return unicode ? '⚪' : 'W';
            case -GoDefinitions.STONE_BLACK:
                return unicode ? '➕' : 'b';
            case -GoDefinitions.STONE_WHITE:
                return unicode ? '➕' : 'w';

            case -GoDefinitions.STONE_NONE:
            default:
                return unicode ? '➕' : '.';
        }
    }

}
