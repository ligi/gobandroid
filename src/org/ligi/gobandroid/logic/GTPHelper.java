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

package org.ligi.gobandroid.logic;

import android.util.Log;

public class GTPHelper {

	
	public static void doMoveByGTPString(String gtp_str,GoGame game) {
		
		Log.i("gobandroid","processing gtp str" + gtp_str);
		gtp_str=gtp_str.replace(" ", "");
		gtp_str=gtp_str.replace("=", "");
		gtp_str=gtp_str.replace("\r", "");
		gtp_str=gtp_str.replace("\n", "");
		gtp_str=gtp_str.replace("\t", "");

		if (gtp_str.equals("resign"))
			game.pass(); // TODO handle better
		else if (gtp_str.equals("PASS"))
			game.pass();
		else {
			
			byte x=(byte) (gtp_str.charAt(0)-'A');
			if (x>8)
				x--; // the I is missing ^^ - took me some time to find that out 
			gtp_str=gtp_str.substring(1);
			byte y= (byte)(game.getBoardSize()-(Byte.parseByte(gtp_str)));
			game.do_move(x, y); // internal here?
			

		}
	}
}
