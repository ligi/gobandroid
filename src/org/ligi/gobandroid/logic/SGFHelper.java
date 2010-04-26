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

import android.util.Log;

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

		Log.i("gobandroid " , "sgf to process:" + sgf);
		byte size=-1;
		GoGame game=null;
		
		byte opener=0;
		
		boolean escape=false;
		//int param_level=0;
		Vector <GoMove> var_vect=new Vector<GoMove>();
		boolean consuming_param=false;
		
		
		String act_param="";
		String act_cmd="";
		String last_cmd="";
		
		
		for (int p=0;p<sgf.length();p++)
		{
			
			//System.out.println("processing " + sgf.charAt(p) + " @ " + p);
			switch(sgf.charAt(p)) {
			case '\r':
			case '\n':
			case ';':
			case '\t':
			case ' ':
				if (consuming_param)
					act_param+=sgf.charAt(p);
				else
					{
					last_cmd=act_cmd;
					act_cmd="";
					}
				break;
			case '(':
				
				if (!consuming_param) {
					// for files without SZ
					if ((opener==1)&&(game==null))
						{
						game=new GoGame((byte)19);
						var_vect.add(game.getActMove());
						}
				
					opener++;
				
				
					// 	push the move we where to the stack to return here after the variation
				
					//	if (param_level!=0) break;
					Log.i("gobandroid","   !!! opening variation");
					if (game!=null)
						var_vect.add(game.getActMove());
			
					last_cmd="";
					act_cmd="";
				}
				break;
			case ')':
				if (var_vect.size()>0) {
					game.jump(var_vect.lastElement());
					var_vect.remove(var_vect.lastElement()); 
					 Log.w("gobandroid" , "popping variaton from stack");	
				}
				else Log.w("gobandroid" , "variation vector underrun!!");
				last_cmd="";
				act_cmd="";
				
		
			
				break;
			case '[':
				if (!consuming_param) {
					consuming_param=true;
					act_param="";
				//	param_level++;
				}
				break;
			case ']':
				
				
				if(var_vect.size()>1)
				Log.i("gobandroid","   command " + act_cmd + " -  act param " + act_param + " esc " + escape + " 1stv " + var_vect.get(1).getNextMoveVariationCount()) ;
		//		Log.i("gobandroid", " esc " + escape +"   (no)move " + last_cmd + " - " + act_cmd);
					
				if (!escape) {
					consuming_param=false;
					//Log.i("","" + last_cmd + " " + act_cmd);
					
					if (act_cmd.length()==0)
						act_cmd=last_cmd;
				
					if (act_cmd.equals("LB"))
						{
						byte x=(byte)(act_param.charAt(0)-'a');
						byte y=(byte)(act_param.charAt(1)-'a');
						
						GoMarker marker=new GoMarker(x,y,act_param.substring(3));
						game.getActMove().addMarker(marker);
						}
						
					// size command
					if (act_cmd.equals("SZ"))
						{
						size=Byte.parseByte(act_param);
						game=new GoGame(size);
						var_vect.add(game.getActMove());
						}	
			
					if (act_cmd.equals("C")) {
						if (game!=null) 
							game.getActMove().setComment(act_param);
					}
					
					//if (variation_depth==1)
					if ((act_cmd.equals("B"))||(act_cmd.equals("W")))
						{
						Log.i("gobandroid","   command " + act_cmd + " -  act param" + act_param);
					
				
						//	Log.i("gobanroid","process move");
						if (act_param.length()==0)
							game.pass();
						else
						{
							
							if (game.getActMove().isFirstMove()&&game.isBlackToMove()&&(act_cmd.equals("W")))
								{
								game.start_player=GoGame.PLAYER_WHITE;
								game.setNextPlayer();								
								}
							
							if (game.isBlackToMove()&&(act_cmd.equals("W")))
								game.pass();
							else if ((!game.isBlackToMove())&&(act_cmd.equals("B")))
								game.pass();
					
							game.do_move((byte)(act_param.charAt(0)-'a'), (byte)(act_param.charAt(1)-'a'));
						
						}
						}
					
						
						
					if ((act_cmd.equals("AB"))||(act_cmd.equals("AW")))
						{
						if ((game==null))
						{
						game=new GoGame((byte)19);
						var_vect.add(game.getActMove());
						}
						
					
						//	Log.i("gobanroid","process move");
						if (act_param.length()==0)
							game.pass();
						else
						{
							/*
							
								game.pass();
							else if ((!game.isBlackToMove())&&(act_cmd.equals("AB")))
								game.pass();
					
							game.do_move((byte)(act_param.charAt(0)-'a'), (byte)(act_param.charAt(1)-'a'));
						*/
							byte x=(byte)(act_param.charAt(0)-'a');
							byte y=(byte)(act_param.charAt(1)-'a');
							
							if (game.isBlackToMove()&&(act_cmd.equals("AB")))
								game.getHandicapBoard().setCellBlack(x, y);
							if (game.isBlackToMove()&&(act_cmd.equals("AW")))
								game.getHandicapBoard().setCellWhite(x, y);
							
						}
						
				}

				last_cmd=act_cmd;
				act_cmd="";	
				act_param="";
				//param_level=0;
				break;
				} // if !escape
				act_cmd=""+act_cmd.subSequence(0, act_cmd.length()-1); // cut the escaper \ 
				// fall wanted to catch the ]
			default:
				
				
				if (consuming_param)
					{
					act_param+=sgf.charAt(p);
					escape=(sgf.charAt(p)=='\\');
					}
				else
					act_cmd+=sgf.charAt(p);
				break;
				
			}
			
			
		}
		Log.i("gobandroid", "var vect after reading" + var_vect.size());
		Log.i("gobandroid", "var vect after reading" + game.getActMove().isFirstMove());
		Log.i("gobandroid", "var vect after reading" + game.getActMove().getNextMoveVariationCount());
		//	
		return game;
	}
	
}
