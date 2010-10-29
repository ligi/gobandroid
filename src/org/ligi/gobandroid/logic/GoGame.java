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
import java.util.Stack;

import org.ligi.tracedroid.logging.Log;

/**
 * 
 * Class to represent a Go Game with its rules
 *
 * @authors
 *  <a href="http://ligi.de">Marcus -LiGi- Bueschleb </a>
 *  oren laskin
 *  
 * This software is licenced with GPLv3         
 */

public class GoGame  {

	private byte act_player=GoDefinitions.PLAYER_BLACK;
    
    private GoBoard visual_board; // the board to show to the user
    private GoBoard calc_board;	  // the board calculations are done in
    private GoBoard last_board;   // board to detect KO situations
    private GoBoard pre_last_board;   // board to detect KO situations
    private GoBoard handicap_board;
    
    private boolean last_action_was_pass=false;
   
    private boolean game_finished=false;
    
    private int[][] groups; // array to build groups
        
    public int[][] area_groups; // array to build groups
    public byte[][] area_assign; // cache to which player a area belongs in a finished game
        
    private int group_count = -1;
        
    private int captures_white; // counter for the captures from black
    private int captures_black; // counter for the captures from white
    
    private int dead_white; // counter for the captures from black
    private int dead_black; // counter for the captures from white
        
    public int territory_white; // counter for the captures from black
    public int territory_black; // counter for the captures from white
    
    private byte handicap=0;
    
    private float komi=6.5f;
    
    private GoMove act_move=null;
    
    private GnuGoMover go_mover=null;
    
    private GoGameMetadata metadata=null;
    
    private int area_group_count=0;

    public final static byte MOVE_VALID=0;
    public final static byte MOVE_INVALID_NOT_ON_BOARD=1;
    public final static byte MOVE_INVALID_CELL_NOT_FREE=2;
    public final static byte MOVE_INVALID_CELL_NO_LIBERTIES=3;
    public final static byte MOVE_INVALID_IS_KO=4;

    public byte start_player=GoDefinitions.PLAYER_BLACK;

    private boolean[][] all_handicap_positions;

    private int local_captures = 0;    

    public GoGame( byte size ) {
    	construct(size);
    }
    
    public GoGame(byte size,byte handicap) {
    	this.handicap=handicap;
    	construct(size);
    }
    
    public float getKomi() {
    	return komi;
    }

    public float getPointsWhite() {
    	return komi+getCapturesWhite()+territory_white;
    }
    
    public float getPointsBlack() {
    	return getCapturesBlack()+territory_black;
    }
    
    /**
     * set the handicap stones on the calc board 
     */
    private void apply_handicap() {
    	calc_board=handicap_board.clone();
    }

    private void construct(byte size) {
    	// create the boards

    	metadata=new GoGameMetadata();
    	
    	calc_board = new GoBoard( size );
        
    	handicap_board=calc_board.clone();
    	    
    	all_handicap_positions=new boolean[size][size];
    	
    	if (GoDefinitions.getHandicapArray(size)!=null)
    		for (int i=0;i<handicap;i++) {
    				if (i<handicap)
    					handicap_board.setCellBlack(GoDefinitions.getHandicapArray(size)[i][0], GoDefinitions.getHandicapArray(size)[i][1]);
    				all_handicap_positions[GoDefinitions.getHandicapArray(size)[i][0]][GoDefinitions.getHandicapArray(size)[i][1]]=true;
    			}
    		
        apply_handicap();
        
        visual_board=calc_board.clone();
        last_board=calc_board.clone();
        pre_last_board=null;
        
        // create the array for group calculations
        groups = new int[size][size];
        
        area_groups = new int[size][size];
        area_assign = new byte[size][size];
        
        act_move=new GoMove(null);
        act_move.setIsFirstMove();
        
        reset();	
    }
    
    public void reset() {
    	if (handicap!=0)
    		start_player=GoDefinitions.PLAYER_WHITE;
    	
    	act_player=start_player;
    	
    	pre_last_board=null;
    	
        captures_black=0;
    	captures_white=0;
    }
    
    public void pass() {
        if (last_action_was_pass) {   	// finish game if both passed  
            game_finished=true; 
        	buildGroups();	
        	buildAreaGroups();	
        }
        else {
            last_action_was_pass=true;
            
            act_move=new GoMove(act_move);
            act_move.setToPassMove();
            setNextPlayer();
        }
    }
    
    /**
     *  place a stone on the board
     *
     * @param x
     * @param y
     * @return 	MOVE_VALID 
     * 			MOVE_INVALID_NOT_ON_BOARD 
     * 		   	MOVE_INVALID_CELL_NOT_FREE 
     *         	MOVE_INVALID_CELL_NO_LIBERTIES 
     *         	MOVE_INVALID_IS_KO
     */
    public byte do_move( byte x, byte y ) {
    	Log.i("do_move x:" + x + "  y:" + y );
    	
    	// return with INVALID if x and y are inside the board 
        if ((x < 0) || (x > calc_board.getSize()) || (y < 0) || (y > calc_board.getSize()))
        	return MOVE_INVALID_NOT_ON_BOARD;
        
        // check if the "new" move is in the variations - to not have 2 equal move as different variations
        GoMove matching_move=null;
        	
        for (GoMove move_matcher:act_move.getNextMoveVariations())
        	if ((move_matcher.getX()==x)&&(move_matcher.getY()==y))
        		matching_move=move_matcher;

        // if there is one matching use this move and we are done
        if (matching_move!=null) {
        	jump(matching_move);
        	return MOVE_VALID;
        }
        		
        if(game_finished) { // game is finished - players are marking dead stones
        	for (int xg = 0; xg < calc_board.getSize(); xg++)
        		for (int yg = 0; yg < calc_board.getSize(); yg++)
                     if (groups[xg][yg]==groups[x][y])
                      	calc_board.toggleCellDead(xg, yg);
        	
        	buildAreaGroups();
    
        	int _dead_white=0; 
        	int _dead_black=0; 
        	    
            for (int xg = 0; xg < calc_board.getSize(); xg++)
                for (int yg = 0; yg < calc_board.getSize(); yg++)
                  	if (calc_board.isCellDead(xg, yg))	{
                    		if (calc_board.isCellDeadBlack(xg, yg))
                    			_dead_black++;
                    		
                    		if (calc_board.isCellDeadWhite(xg, yg))
                    			_dead_white++;
                    	}
            dead_white=_dead_white;
            dead_black=_dead_black;
            
            return MOVE_VALID;       
        }
        
        if (!calc_board.isCellFree( x, y ))  // cant place a stone where another is allready
        	return MOVE_INVALID_CELL_NOT_FREE;
                
        GoBoard bak_board=calc_board.clone();
            	
       	int tmp_cap=captures_black+captures_white;
            	
        if (isBlackToMove())
        	calc_board.setCellBlack( x, y );
        else
            calc_board.setCellWhite( x, y );
        
        remove_dead(x,y);
        
        // move is a KO -> Invalid
        if (calc_board.equals(pre_last_board)) {
        	Log.i("illegal move -> KO");
            calc_board=bak_board.clone();
            return MOVE_INVALID_IS_KO;
        }

        if (!hasGroupLiberties(x, y)) {
        	Log.i("illegal move -> NO LIBERTIES");
            calc_board=bak_board.clone();
        	return MOVE_INVALID_CELL_NO_LIBERTIES;
        }
        
        // if we reach this point it is avalid move 
        // -> do things needed to do after a valid move 
                
        if (isBlackToMove())
        	getGoMover().processBlackMove(x, y);
        else
        	getGoMover().processWhiteMove(x, y);
        
        setNextPlayer();
                    
        pre_last_board=last_board.clone();
        last_board=calc_board.clone();
        visual_board=calc_board.clone();                    
        last_action_was_pass=false;
        
        act_move=new GoMove(x,y,act_move);
        
        if (!calc_board.isCellWhite(x, y))
   			captures_black += local_captures;
   		else
   			captures_white += local_captures;
        
        act_move.setDidCaptures((tmp_cap!=(captures_black+captures_white)));

        // if we reached this point this move must be valid
        return MOVE_VALID;
    }
    
    public GoMove getActMove() {
    	return act_move;
    }

    public boolean canRedo() {
    	return (act_move!=null)&&(act_move.hasNextMove());
    }
    
    public int getPossibleVariationCount() {
    	 if (act_move==null)
    		 return 0;
    	return (act_move.getNextMoveVariationCount());
    }
    
    /** 
     * moving without checks 
     * useful  e.g. for undo / recorded games 
     * where we can be sure that the move is valid 
     * and so be faster
     **/
    public void do_internal_move( GoMove move ) {
    	
    	act_move=move;
    	if (move.isFirstMove())
    		return;

    	if (move.isPassMove()) {
    		setNextPlayer();
    		return;
    		}
    	
        if (isBlackToMove())
            calc_board.setCellBlack( move.getX(), move.getY() );
        else
            calc_board.setCellWhite( move.getX(), move.getY() );
        
        setNextPlayer();
        
        if (move.didCaptures()) {
        	buildGroups();
        	remove_dead( move.getX(), move.getY() );
        }
    }

    public boolean canUndo() {
    	return (!act_move.isFirstMove());//&&(!getGoMover().isMoversMove());
    }
    
    /**
     * undo the last move
     */
    public void undo() {
    	jump(act_move.getParent());
    	getGoMover().undo();
    	game_finished=false;
    }
    
    private void _undo(boolean keep_move) {
    	getGoMover().paused=true;
		
	   	GoMove mLastMove=act_move;
    	jump(mLastMove.getParent());
    	if (!keep_move)
    		mLastMove.destroy();
    	getGoMover().undo();
    	game_finished=false;
    	
    	getGoMover().paused=false;
    }

    public void undo(boolean keep_move) {
    	_undo(keep_move);
    	
    	if (canUndo()&&(getGoMover().isMoversMove()))
    		_undo(keep_move);
    }

    public void redo(int var) {
    	Log.i("redoing " +act_move.getnextMove(var).toString());
    	jump(act_move.getnextMove(var));
    }

    /**
     * @return the first move of the game 
     */
    public GoMove getFirstMove() {
    	GoMove move=act_move;
    	
    	while(true)	{
    		if (move.isFirstMove()) 
    			return move;
    		move=move.getParent();
    	}
    }
    
    public void refreshBoards() {
    	jump(getActMove());
    }
    
    public void jumpFirst() {
    	jump(getFirstMove());
    }

    public GoMove getLastMove() {
    	GoMove move=act_move;
    	while(true) {
    		if (!move.hasNextMove()) 
    			return move;
    		move=move.getnextMove(0);
    	}
    }

    public void jumpLast() {
    	jump(getLastMove());
    }
    
    public void jump(GoMove move) {
    	last_action_was_pass=false;
        clear_calc_board();
   
        Vector <GoMove> replay_moves=new Vector<GoMove>();
        
        replay_moves.add(move);
        while (true) {
        	if (replay_moves.lastElement().isFirstMove()) 
        		break;
        		
        	Log.i( "adding" + replay_moves.lastElement().toString() );
        	replay_moves.add(replay_moves.lastElement().getParent());
        }
        
        reset();
        act_move=getFirstMove();
        
        for (int step=replay_moves.size()-1 ; step>=0;step--)
            do_internal_move(replay_moves.get(step));
        
        visual_board=calc_board.clone();    	
    }
   
    public boolean cell_has_libertie(int x , int y ) {
      
      return ( ((x != 0)&&(calc_board.isCellFree( x- 1, y ) ))
          ||
       ((y != 0)&&(calc_board.isCellFree( x, y - 1 )))
          ||  
       ((x != (calc_board.getSize() - 1))&& (calc_board.isCellFree( x + 1, y )))
          ||  
       ((y != (calc_board.getSize() - 1))&& (calc_board.isCellFree( x, y + 1 ) ))
      );  
    }
   
    public boolean cell_has_white_neighbours(int x , int y ) {
      
      return ( ((x != 0)&&(calc_board.isCellWhite( x- 1, y ) ))
          ||
       ((y != 0)&&(calc_board.isCellWhite( x, y - 1 )))
          ||  
       ((x != (calc_board.getSize() - 1))&& (calc_board.isCellWhite( x + 1, y )))
          ||  
       ((y != (calc_board.getSize() - 1))&& (calc_board.isCellWhite( x, y + 1 ) ))
      );  
    }
   
    public boolean cell_has_black_neighbours(int x , int y ) {
        
        return ( ((x != 0)&&(calc_board.isCellBlack( x- 1, y ) ))
            ||
         ((y != 0)&&(calc_board.isCellBlack( x, y - 1 )))
            ||  
         ((x != (calc_board.getSize() - 1))&& (calc_board.isCellBlack( x + 1, y )))
            ||  
         ((y != (calc_board.getSize() - 1))&& (calc_board.isCellBlack( x, y + 1 ) ))
        );  
      }
    
    /**
     * check if a group has liberties
     * 
     * @param group2check - the group to check
     * @return boolean weather the group has liberty
     * 
     */
    public boolean hasGroupLiberties(int x, int y ) {
		 /* do a depth search first from point */
    	if (calc_board.isCellFree(x,y)) 
    		return true;
    	
        boolean checked_pos[][] = new boolean[calc_board.getSize()][calc_board.getSize()];
        Stack <Integer>ptStackX = new Stack<Integer>();
        Stack <Integer>ptStackY = new Stack<Integer>();

        /* Replace previous code with more efficient flood fill */
   		ptStackX.push(x);
   		ptStackY.push(y);
           		
   		while (!ptStackX.empty()) {
   			int newx = ptStackX.pop();
   			int newy = ptStackY.pop();
   			
   	        if (cell_has_libertie(newx,newy)) return true;
   	        else checked_pos[newx][newy] = true;
   			
   			/* check to the left */
   			if (newx > 0)
   				if (calc_board.areCellsEqual(newx-1,newy,newx,newy) && (checked_pos[newx-1][newy] == false)) {
   					ptStackX.push(newx-1);
   					ptStackY.push(newy);
   				}
   			/* check to the right */
   			if (newx < calc_board.getSize() - 1)
   				if (calc_board.areCellsEqual(newx+1,newy,newx,newy) && (checked_pos[newx+1][newy] == false)) {
   					ptStackX.push(newx+1);
   					ptStackY.push(newy);
   				}
   			/* check down */
   			if (newy > 0)
   				if (calc_board.areCellsEqual(newx,newy-1,newx,newy) && (checked_pos[newx][newy-1] == false)) {
   					ptStackX.push(newx);
   					ptStackY.push(newy-1);
   				}
   			/* check up */
   			if (newy < calc_board.getSize() - 1)
   				if (calc_board.areCellsEqual(newx,newy+1,newx,newy) && (checked_pos[newx][newy+1] == false)) {
   					ptStackX.push(newx);
   					ptStackY.push(newy+1);
   				}
   		}
    	
    	return false;
    }
        
    public boolean isAreaGroupBlacks(int group2check ) {
    	if(group2check==-1) return false;
    	boolean res=false;
        for (int xg = 0; xg < getBoardSize(); xg++)
            for (int yg = 0; yg < getBoardSize(); yg++)
               if (area_groups[xg][yg]==group2check)
            	   if (cell_has_white_neighbours(xg,yg))
            		   return false;
            	   else
            		   res|=(cell_has_black_neighbours(xg,yg));
            		   
        return res;  // found no stone in the group with liberty 
    }
   
    public boolean isAreaGroupWhites(int group2check ) {
    	if(group2check==-1) return false;
    	boolean res=false;
        for (int xg = 0; xg < getBoardSize(); xg++)
            for (int yg = 0; yg < getBoardSize(); yg++)
               if (area_groups[xg][yg]==group2check)
            	   if (cell_has_black_neighbours(xg,yg))
            		   return false;
            	   else
            		   res|=(cell_has_white_neighbours(xg,yg));
            		   
        return res;  // found no stone in the group with liberty 
    }
       
    public void clear_calc_board() {
      
    	for (byte x = 0; x < calc_board.getSize(); x++)
            for (byte y = 0; y < calc_board.getSize(); y++) 
                calc_board.setCellFree(x,y );
        apply_handicap();
    }
  
    /**
     * group the stones 
     * 
     * the result is written in groups[][]
     * 
     */
    public void buildGroups() {
        group_count=0;
        
        // reset groups
        for (int x = 0; x < calc_board.getSize(); x++)
            for (int y = 0; y < calc_board.getSize(); y++) 
                groups[x][y] = -1;

        Stack <Integer>ptStackX = new Stack<Integer>();
        Stack <Integer>ptStackY = new Stack<Integer>();
        
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
            				if (calc_board.areCellsEqual(newx-1,newy,newx,newy) && (groups[newx-1][newy] == -1)) {
            						ptStackX.push(newx-1);
            						ptStackY.push(newy);
            				}
            			/* check to the right */
            			if (newx < calc_board.getSize() - 1)
            				if (calc_board.areCellsEqual(newx+1,newy,newx,newy) && (groups[newx+1][newy] == -1)) {
            						ptStackX.push(newx+1);
            						ptStackY.push(newy);
            				}
            			/* check down */
            			if (newy > 0)
            				if (calc_board.areCellsEqual(newx,newy-1,newx,newy) && (groups[newx][newy-1] == -1)) {
            						ptStackX.push(newx);
            						ptStackY.push(newy-1);
            				}
            			/* check up */
            			if (newy < calc_board.getSize() - 1)
            				if (calc_board.areCellsEqual(newx,newy+1,newx,newy) && (groups[newx][newy+1] == -1)) {
            						ptStackX.push(newx);
            						ptStackY.push(newy+1);
            				}
            		}
            	group_count++;
            	}
            }
    }

    public void buildAreaGroups() {
        area_group_count=0;
                
        // reset groups
        for (int x = 0; x < calc_board.getSize(); x++)
            for (int y = 0; y < calc_board.getSize(); y++) {
            	   area_groups[x][y] = -1;
            	   area_assign[x][y] = 0;
            }
        
        for (byte x = 0; x < calc_board.getSize(); x++)
            for (byte y = 0; y < calc_board.getSize(); y++) {
                if (calc_board.isCellFree( x, y )) {

                    if (x > 0) {
                        if (!calc_board.areCellsEqual( x, y, (byte)( x - 1), y )) {
                        	area_group_count++;
                        	area_groups[x][y] = area_group_count;
                        }
                        else
                        	area_groups[x][y] = area_groups[x - 1][y];
                    }
                    else {
                    	area_group_count++;
                    	area_groups[x][y] = area_group_count;
                    }
                    
                    if (y > 0) {
                        if (calc_board.areCellsEqual( x, y, x , (byte)(y-1) )) {
                            int from_grp=area_groups[x][y];
                            
                            for (int xg = 0; xg < calc_board.getSize(); xg++)
                                for (int yg = 0; yg < calc_board.getSize(); yg++)
                                    if (area_groups[xg][yg]==from_grp)
                                    	area_groups[xg][yg]=area_groups[x][y-1];
                        }
                    }

                }
            }

        territory_black=0;
        territory_white=0;
        for (int x = 0; x < calc_board.getSize(); x++)
            for (int y = 0; y < calc_board.getSize(); y++) 
            	if (isAreaGroupWhites(area_groups[x][y])) { 
            		area_assign[x][y]=GoDefinitions.PLAYER_WHITE;
            		territory_white++;
        			}
            	else if (isAreaGroupBlacks(area_groups[x][y])) {
            		territory_black++;
            		area_assign[x][y]=GoDefinitions.PLAYER_BLACK;
            	}
    }
    
    /** 
     * 
     * remove dead groups from the board - e.g. after a move 
     * 
     * the cell with ignore_x and ignore_y is ignored - e.g. last move
     * 
     * **/
    private void remove_dead(byte ignore_x,byte ignore_y) {
    	local_captures = 0;
    	
    	/* check left */
    	if (ignore_x > 0)
    		if ((!hasGroupLiberties(ignore_x-1, ignore_y))&&(!calc_board.areCellsEqual(ignore_x, ignore_y, ignore_x-1, ignore_y)))
    			remove_group(ignore_x-1, (int)ignore_y);
    	/* check right */
    	if (ignore_x < calc_board.getSize()-1)
    		if ((!hasGroupLiberties(ignore_x+1, ignore_y))&&(!calc_board.areCellsEqual(ignore_x, ignore_y, ignore_x+1, ignore_y)))
    			remove_group(ignore_x+1, (int)ignore_y);
    	/* check down */
    	if (ignore_y > 0)
    		if ((!hasGroupLiberties(ignore_x, ignore_y-1))&&(!calc_board.areCellsEqual(ignore_x, ignore_y, ignore_x, ignore_y-1)))
    			remove_group((int)ignore_x, ignore_y-1);
    	/* check up */
    	if (ignore_y < calc_board.getSize()-1)
    		if ((!hasGroupLiberties(ignore_x, ignore_y+1))&&(!calc_board.areCellsEqual(ignore_x, ignore_y, ignore_x, ignore_y+1)))
    			remove_group((int)ignore_x, ignore_y+1);
    }
    
    private void remove_group(int x,int y)  {
    	
        boolean checked_pos[][] = new boolean[calc_board.getSize()][calc_board.getSize()];
        Stack <Integer>ptStackX = new Stack<Integer>();
        Stack <Integer>ptStackY = new Stack<Integer>();

        /* Replace previous code with more efficient flood fill */
   		ptStackX.push(x);
   		ptStackY.push(y);
		checked_pos[x][y] = true;
           		
   		while (!ptStackX.empty()) {
   			int newx = ptStackX.pop();
   			int newy = ptStackY.pop();
   			
   			/* check to the left */
   			if (newx > 0)
   				if (calc_board.areCellsEqual(newx-1,newy,newx,newy) && (checked_pos[newx-1][newy] == false)) {
   					ptStackX.push(newx-1);
   					ptStackY.push(newy);
   					checked_pos[newx-1][newy]=true;
   				}
   			/* check to the right */
   			if (newx < calc_board.getSize() - 1)
   				if (calc_board.areCellsEqual(newx+1,newy,newx,newy) && (checked_pos[newx+1][newy] == false)) {
   					ptStackX.push(newx+1);
   					ptStackY.push(newy);
   					checked_pos[newx+1][newy]=true;
   				}
   			/* check down */
   			if (newy > 0)
   				if (calc_board.areCellsEqual(newx,newy-1,newx,newy) && (checked_pos[newx][newy-1] == false)) {
   					ptStackX.push(newx);
   					ptStackY.push(newy-1);
   					checked_pos[newx][newy-1]=true;
   				}
   			/* check up */
   			if (newy < calc_board.getSize() - 1)
   				if (calc_board.areCellsEqual(newx,newy+1,newx,newy) && (checked_pos[newx][newy+1] == false)) {
   					ptStackX.push(newx);
   					ptStackY.push(newy+1);
   					checked_pos[newx][newy+1]=true;
   				}

   			calc_board.setCellFree(newx,newy);
   			local_captures++;
   		}
 
    }
    
    /** 
     * 
     * return if it's a handicap stone so that the view can visualize it
     * 
     * TODO: check rename ( general marker ) 
     * 		  
     * **/
    public boolean isPosHoschi(byte x,byte y) {
    	return all_handicap_positions[x][y];
    }
    
    public GoBoard getVisualBoard() {
        return visual_board;
    }
    
    public GoBoard getCalcBoard() {
        return calc_board;
    }
    
    public boolean isLastActionPass() {
    	return last_action_was_pass;
    }

    public boolean isFinished() {
    	return game_finished;
    }
    
    public boolean isBlackToMove() {
    	return (act_player==GoDefinitions.PLAYER_BLACK);
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

    public int getGroup(byte x,byte y) {
        return groups[x][y];
    }
    
    public void setNextPlayer() {
    	act_player=(act_player==GoDefinitions.PLAYER_BLACK)?GoDefinitions.PLAYER_WHITE:GoDefinitions.PLAYER_BLACK;
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
		if (go_mover==null)
			// an inactive "dummy" go mover
			return new GnuGoMover(); 
		return go_mover;
	}
	
	public GoGameMetadata getMetaData() {
		return metadata;
	}
	
	public void setMetadata(GoGameMetadata metadata) {
		this.metadata=metadata;
	}
}