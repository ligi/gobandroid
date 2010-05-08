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

import org.ligi.tracedroid.logging.Log;


/**
 * class for serializing and deserializing SGF
 * 
 * @author ligi
 *
 */
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

	public static String escapeSGF(String txt) {
		txt.replace("]", "\\]");
		txt.replace(")", "\\)");
		txt.replace("\\", "\\\\");
		return txt;
	}
	
	public static String game2sgf(GoGame game) {
		String res="";
		res="(;FF[4]GM[1]AP[gobandroid:0]"; // header
		res+="SZ[" + game.getBoardSize() + "]"; // board_size;
		res+="GN[" + escapeSGF(game.getMetaData().getName()) + "]";
		res+="PB[" + escapeSGF(game.getMetaData().getBlackName()) + "]";
		res+="PW[" + escapeSGF(game.getMetaData().getWhiteName()) + "]";
		res+="BR[" + escapeSGF(game.getMetaData().getBlackRank()) + "]";
		res+="WR[" + escapeSGF(game.getMetaData().getWhiteRank()) + "]";
		res+="RE[" + escapeSGF(game.getMetaData().getResult()) + "]";
		
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

		Log.i("sgf to process:" + sgf);
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
		
		GoGameMetadata metadata=new GoGameMetadata();
		
		
		for (int p=0;p<sgf.length();p++)
		{
			char act_char=sgf.charAt(p);
			//Logger.i("act_char:" + (char)act_char + "  " + act_cmd + "  " + act_param);
			//System.out.println("processing " + sgf.charAt(p) + " @ " + p);
			if (!consuming_param)
				// consuming command
				switch(act_char) {
				case '\r':
				case'\n':
				case ';':
				case '\t':
				case ' ':
					last_cmd=act_cmd;
					act_cmd="";
					break;

				case '[':
					consuming_param=true;
					act_param="";
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
						Log.i("   !!! opening variation" + game);
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
						Log.w( "popping variaton from stack");	
						}
						else 
						Log.w( "variation vector underrun!!");
					
					last_cmd="";
					act_cmd="";
					
					break;
				
				default:
					act_cmd+=	sgf.charAt(p);
						
				}
			else
				// consuming param
				switch(act_char) {
				case ']':	// closing command parameter -> can process command now
				if (!escape) {
					consuming_param=false;
					
					byte param_x=0,	param_y=0;
					
					if (act_param.length()>2) {
						param_x=(byte)(act_param.charAt(0)-'a');
						param_y=(byte)(act_param.charAt(1)-'a');
					}

					// if command is empty -> use the last command
					if (act_cmd.length()==0)
						act_cmd=last_cmd;
				
					// marker section - infos here http://www.red-bean.com/sgf/properties.html
					
					// marker with text
					if (act_cmd.equals("LB"))
						game.getActMove().addMarker(new GoMarker(param_x,param_y,act_param.substring(3)));

					// mark with x
					if (act_cmd.equals("MA"))
						game.getActMove().addMarker(new GoMarker(param_x,param_y,"X"));
					
					// mark with triangle - fake by |>
					if (act_cmd.equals("TR"))
						game.getActMove().addMarker(new GoMarker(param_x,param_y,"|>"));
					
					// mark with Square - fake by [] atm
					if (act_cmd.equals("TR"))
						game.getActMove().addMarker(new GoMarker(param_x,param_y,"[]"));
					
					// mark with Circle - fake by O atm
					if (act_cmd.equals("CR"))
						game.getActMove().addMarker(new GoMarker(param_x,param_y,"O"));
					
					
					if (act_cmd.equals("GN")) // Game Name
						metadata.setName(act_param);

					if (act_cmd.equals("PW")) // Player White Name
						metadata.setWhiteName(act_param);

					if (act_cmd.equals("PB")) // Player Black Name
						metadata.setBlackName(act_param);

					if (act_cmd.equals("WR")) // Player White Rank
						metadata.setWhiteRank(act_param);

					if (act_cmd.equals("BR")) // Player Black Rank
						metadata.setBlackRank(act_param);

					if (act_cmd.equals("RE")) // Game Result
						metadata.setResult(act_param);

					
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
						Log.i("   command " + act_cmd + " -  act param" + act_param);
					
						// if still no game open -> open one with default size
						if (game==null)
						{
							game=new GoGame((byte)19);
							var_vect.add(game.getActMove());
						}
				
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
				
				} // if !escape
//				act_cmd=""+act_cmd.subSequence(0, act_cmd.length()-1); // cut the escaper \ 
				// fall wanted to catch the ]
			case '\\':
				if (escape)
				{
					act_param+=(char)act_char;
					escape=false;
				}
				else
					escape=true;
				break;
			default:
				act_param+=(char)act_char;
				escape=false;
				break;
				
			}
			
			
		}
		game.setMetadata(metadata);
		return game;
	}
	
}
