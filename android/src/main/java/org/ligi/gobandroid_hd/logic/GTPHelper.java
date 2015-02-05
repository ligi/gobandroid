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

import org.ligi.tracedroid.logging.Log;

/**
 * Class to help with GTP ( Go Text Protocol )
 *
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         <p/>
 *         This software is licenced with GPLv3
 */
public class GTPHelper {

    /**
     * @param gtp_str - the GTP string to process
     * @param game    - the game on which we execute the commands
     * @return - true if we understood the command / false if there was a
     * problem
     */
    public static boolean doMoveByGTPString(String gtp_str, GoGame game) {

        Log.i("processing gtp str" + gtp_str);

        // remove chars we do not need
        for (String c : new String[]{" ", "=", "\r", "\n", "\t"})
            gtp_str = gtp_str.replace(c, "");

        if (gtp_str.toUpperCase().equals("RESIGN")) {
            game.pass(); // TODO handle this case better
            return true;
        } else if (gtp_str.toUpperCase().equals("PASS")) {
            game.getActMove().setComment(game.getActMove().getComment() + "passes");
            game.pass();
            return true;
        }

        try {
            BoardCell boardCell = new BoardCell((byte) (gtp_str.charAt(0) - 'A'), (byte) (game.getBoardSize() - (Byte.parseByte(gtp_str.substring(1)))), game.getCalcBoard());
            if (boardCell.x > 8)
                boardCell = boardCell.left; // the I is missing ^^ - took me some time to find that out

            game.do_move(boardCell); // internal here?
            return true;
        } catch (Exception e) {
            Log.w("Problem parsing coordinates from GTP " + e.toString());
        }

        // if we got here we could not make sense of the command
        Log.w("could not make sense of the GTP command: " + gtp_str);
        return false;

    }
}
