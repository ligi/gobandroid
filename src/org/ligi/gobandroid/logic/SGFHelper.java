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

import java.util.Vector;

//import android.util.Log;

public class SGFHelper {

	
	private static String move2string(GoMove move , boolean black_to_move) {
		String res="";
		

			
		
			if (!move.isFirstMove())
			{
				res+=";" + (black_to_move?"B":"W");
			
				if (move.isPassMove())
					res+="[]";
				else	
					res+= "[" + (char)('a'+move.getX()) +(char)('a'+move.getY())+ "]\n";
			
				black_to_move=!black_to_move;
				
			}
			
			if (move.hasNextMove())
				{
					if (move.hasNextMoveVariations()) {
					for (GoMove var: move.getNextMoveVariations())
						res+="("+move2string(var , black_to_move)+")" ;
					}
					else
						res+=move2string(move.getnextMove(0) , black_to_move) ;
				}

			
		return res;
	}
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

		
		GoMove move=game.getFirstMove();
		
		
		/*
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
			*/
		res+=move2string(move ,black_to_move)+")"; // close
		return res;
	}
	
	
	public static GoGame sgf2game(String sgf) {
		String act_cmd="";
		byte size=-1;
		GoGame game=null;
		
		String last_cmd="";
		
		boolean escape=false;
		int param_level=0;
		Vector <GoMove> var_vect=new Vector<GoMove>();
		
		for (int p=0;p<sgf.length();p++)
			switch(sgf.charAt(p)) {
			case '\r':
			case '\n':
			case ';':
				act_cmd="";
				break;
			case '(':
				if (game!=null)
					var_vect.add(game.getActMove());
				if (param_level!=0) break;
				act_cmd="";
				
				break;
			case ')':
				if (var_vect.size()>0) {
					game.jump(var_vect.lastElement());
					var_vect.remove(var_vect.lastElement()); }
				act_cmd="";
		
			
				break;
			case '[':
				last_cmd=act_cmd;
				act_cmd="";
				param_level++;
				break;
			case ']':
				
			
				
				if (!escape) {
					//Log.i("","" + last_cmd + " " + act_cmd);
				if (last_cmd.equals("SZ"))
					{
					size=Byte.parseByte(act_cmd);
					game=new GoGame(size);
					}
			
				//if (variation_depth==1)
				if ((last_cmd.equals("B"))||(last_cmd.equals("W")))
				{
					
					//Log.i("gobanroid","process move");
					if (act_cmd.length()==0)
						game.pass();
					else
					{
						if (game.isBlackToMove()&&(last_cmd.equals("W")))
							game.pass();
						else if ((!game.isBlackToMove())&&(last_cmd.equals("B")))
							game.pass();
					
						game.do_move((byte)(act_cmd.charAt(0)-'a'), (byte)(act_cmd.charAt(1)-'a'));
						
					}
					
				}

				act_cmd="";
				param_level=0;
				break;
				} // if !escape
				act_cmd=""+act_cmd.subSequence(0, act_cmd.length()-1); // cut the \ 
				// fall wanted to catch the ]
			default:
				escape=(sgf.charAt(p)=='\\');
				act_cmd+=sgf.charAt(p);
				break;
				
			}
		
	//Log.i("gobandroid", "loading game with size" + size);
		return game;
	}
	
}
