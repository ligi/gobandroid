package org.ligi.gobandroid.logic;

public class SGFHelper {

	public static String game2sgf(GoGame game) {
		String res="";
		res="(;FF[4]GM[1]"; // header
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
	
	
	
}
