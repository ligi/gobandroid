package org.ligi.gobandroid.logic;

import android.util.Log;

public class SGFHelper {

	public static String game2sgf(GoGame game) {
		String res="";
		res="(;FF[4]GM[1]AP[gobandroid:0]"; // header
		res+="SZ[" + game.getBoardSize() + "]"; // board_size;
		res+="\n";
		
		boolean black_to_move=true;
		
		if (game.getHandicap()>0)
			{
			black_to_move=false; // white begins on a handicap game - not black
			res+="AB";
			for ( int handicap=0;handicap<game.getHandicap();handicap++)
				res+="["+(char)('a' + game.getHandicapArray()[handicap][0])+(char)('a' + game.getHandicapArray()[handicap][1]) + "]";
			res+="\n";
			}
		
		for (int move=0;move<game.moves.size();move++)
			{
			byte[] act_move=game.moves.get(move);
			
			res+=";" + (black_to_move?"B":"W");
			
			if (act_move[0]==-1)
				res+="[]";
			else	
				res+= "[" + (char)('a'+act_move[0]) +(char)('a'+act_move[1])+ "]\n";
			
			black_to_move=!black_to_move;
			}
			
		res+=")"; // close
		return res;
	}
	
	
	public static GoGame sgf2game(String sgf) {
		String act_cmd="";
		byte size=-1;
		GoGame game=null;
		
		String last_cmd="";
		
		
		for (int p=0;p<sgf.length();p++)
			switch(sgf.charAt(p)) {
			case '\r':
			case '\n':
			case ';':
				act_cmd="";
				break;
			case '[':
				last_cmd=act_cmd;
				act_cmd="";
				break;
			case ']':
				
				Log.i("","" + last_cmd + " " + act_cmd);
				if (last_cmd.equals("SZ"))
					{
					size=Byte.parseByte(act_cmd);
					game=new GoGame(size);
					}
			
				if ((last_cmd.equals("B"))||(last_cmd.equals("W")))
				{
					game.do_move((byte)(act_cmd.charAt(0)-'a'), (byte)(act_cmd.charAt(1)-'a'));
				}
				
		
				
				act_cmd="";
				
				break;
			default:
				act_cmd+=sgf.charAt(p);
				break;
				
			}
		
		Log.i("gobandroid", "loading game with size" + size);
		return game;
	}
	
}
