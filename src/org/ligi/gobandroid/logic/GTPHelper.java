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

		if (gtp_str.equals("PASS"))
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
