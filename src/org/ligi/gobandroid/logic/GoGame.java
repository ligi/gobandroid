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

/**
 * 
 * Class to represent a Go Game with its rules
 *
 * @author <a href="http://ligi.de">Marcus -LiGi- Bueschleb </a>
 * 
 * This software is licenced with GPLv3         
 */

public class GoGame implements GoDefinitions {

	private byte act_player=PLAYER_BLACK;
    
    private GoBoard visual_board; // the board to show to the user
    private GoBoard calc_board;	  // the board calculations are done in
    private GoBoard last_board;   // board to detect KO situations
    private GoBoard pre_last_board;   // board to detect KO situations
    private GoBoard handicap_board;
    
    
    //private boolean black_to_move = true;

    private boolean last_action_was_pass=false;
   
    private boolean game_finished=false;
    
    private int[][] groups; // array to build groups
        
    public int[][] area_groups; // array to build groups
    public byte[][] area_assign; // cache to which player a area belongs in a finished game
        
    //private boolean[][] dead_stones; // dead stone marker
        
    private int group_count = -1;
        
    private int captures_white; // counter for the captures from black
    private int captures_black; // counter for the captures from white
    
    private int dead_white; // counter for the captures from black
    private int dead_black; // counter for the captures from white
        
    
    public int territory_white; // counter for the captures from black
    public int territory_black; // counter for the captures from white
    
    private byte handicap=0;
    
    private float komi=6.5f;
    //public Vector<byte[]> moves;
    
    
    private GoMove act_move=null;
    
    
    private GnuGoMover go_mover=null;
    
    public float getKomi() {
    	return komi;
    }

    public float getPointsWhite() {
    	return komi+getCapturesWhite()+territory_white;
    }
    
    public float getPointsBlack() {
    	return getCapturesBlack()+territory_black;
    }
    

    public GoGame( byte size ) {
    	construct(size);
    }
    
    public GoGame(byte size,byte handicap) {
    	this.handicap=handicap;
    	construct(size);
    }
    
    private void apply_handicap() {
    	
    	calc_board=handicap_board.clone();
    }

    public byte[][] getHandicapArray() {
        if (getBoardSize()==19)
        	return hoshis19x19;
        else if (getBoardSize()==13)
        	return hoshis13x13;
        else if (getBoardSize()==9)
        	return hoshis9x9;
        else return new byte[0][0];
    }
    
    private void construct(byte size) {
    	// create the boards

    	calc_board = new GoBoard( size );
        
    	handicap_board=calc_board.clone();
    	    
    	for (int i=0;i<handicap;i++)
    		handicap_board.setCellBlack(getHandicapArray()[i][0], getHandicapArray()[i][1]);
    	
        apply_handicap();
        
        visual_board=calc_board.clone();
        last_board=calc_board.clone();
        pre_last_board=calc_board.clone();
        
        
        
        // create the array for group calculations
        groups = new int[size][size];
        
        area_groups = new int[size][size];
        area_assign = new byte[size][size];
                
        
        act_move=new GoMove(null);
        act_move.setIsFirstMove();
        
        //dead_stones=new boolean[size][size];
        
        /*for (int x=0;x<size;x++)
        	for (int y=0;y<size;y++)
        		dead_stones[x][y]=false;
        	*/
        reset();	
        
        
    }
    
    byte start_player=PLAYER_BLACK;
    
    public void reset() {
    	// black always starts
    	
    	if (handicap!=0)
    		start_player=PLAYER_WHITE;
    	
    	act_player=start_player;
    	
    	// create the vector to save the moves
        //moves= new Vector<byte[]>();
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
            //moves.add(new byte[] { -1,-1} );
            setNextPlayer();
        }
    }
/*
    public boolean isStoneDead(byte x,byte y) {
    	return dead_stones[x][y];
    }
*/  
    /**
     *  place a stone on the board
     *
     * @param x
     * @param y
     * @return true if the move was valid - false if invalid move
     */
    public boolean do_move( byte x, byte y ) {
    	Log.i("gobandroid","move " + x + "  " + y );
        if ((x >= 0) && (x <= calc_board.getSize()) && (y >= 0) && (y < calc_board.getSize())) { // if x and y are inside the board
        	
        	if(game_finished)
        	{ // game is finished - players are marking dead stones
        		for (int xg = 0; xg < calc_board.getSize(); xg++)
                    for (int yg = 0; yg < calc_board.getSize(); yg++)
                        if (groups[xg][yg]==groups[x][y])
                        	calc_board.toggleCellDead(xg, yg);
        		buildAreaGroups();
    
        		int _dead_white=0; 
        	    int _dead_black=0; 
        	    
                for (int xg = 0; xg < calc_board.getSize(); xg++)
                    for (int yg = 0; yg < calc_board.getSize(); yg++)
                    	if (calc_board.isCellDead(xg, yg))
                    	{
                    		if (calc_board.isCellDeadBlack(xg, yg))
                    			_dead_black++;
                    		
                    		if (calc_board.isCellDeadWhite(xg, yg))
                    			_dead_white++;
                    	}
                dead_white=_dead_white;
                dead_black=_dead_black;
                
        	}
        	else {
        	
            if (calc_board.isCellFree( x, y )) { // cant place a stone where another is allready
                
            	GoBoard bak_board=calc_board.clone();
            	
            	int tmp_cap=captures_black+captures_white;
            	
                if (isBlackToMove())
                    calc_board.setCellBlack( x, y );
                else
                    calc_board.setCellWhite( x, y );
                
                buildGroups();
                remove_dead(x,y);
                
                if ((hasGroupLibertie(groups[x][y])||isDeadGroupOnBoard(x,y)) // if either a field has libertys or get's one
                		&&!pre_last_board.equals(calc_board)) // and the move is not a ko 
                { 	// valid move -> do things needed to do after a valid move 
                    Log.d("gobandroid", "isDeadGroupOnBoard(x,y)" + isDeadGroupOnBoard(x,y));
                    
                    
                    if (getGoMover()!=null)
                    {
                    	if (isBlackToMove())
                    		getGoMover().processBlackMove(x, y);
                    	else
                    		getGoMover().processWhiteMove(x, y);
                    }
                    
                    setNextPlayer();
                    
                    
                    pre_last_board=last_board.clone();
                    last_board=calc_board.clone();
                    visual_board=calc_board.clone();                    
                    last_action_was_pass=false;
                    //moves.add(new byte[] { x,y} );
              
                    act_move=new GoMove(x,y,act_move);
                    
                    act_move.setDidCaptures((tmp_cap!=(captures_black+captures_white)));
                    //moves_history=(Vector<byte[]>) moves.clone();
                    return true;
                    }
                else { // was an illegal move -> undo
                    calc_board=bak_board.clone();
                    return false;
                }
                
            }
            }
        }
        return false;
    }

    
    public GoMove getActMove() {
    	return act_move;
    }
    public boolean canRedo() {
    	/*if ((moves_history!=null))
    	Log.i("gobandroid","redo"+moves_history.size() + "   " + moves.size());
    	return ((moves_history!=null)&&(moves_history.size()>moves.size()));
    	*/
    	return (act_move!=null)&&(act_move.hasNextMove());
    }
    
    
    public int getPossibleVariationCount() {
    	 if (act_move==null)
    		 return 0;
    	return (act_move.getNextMoveVariationCount());
    }
    /** 
     * moving without checks 
     * usefull  e.g. for undo / recorded games 
     * where we can be sure that the move is valid 
     * 
     **/
    public void do_internal_move( GoMove move ) {
    	
    	act_move=move;
    	if (move.isFirstMove())
    		return;
    	

    	if (move.isPassMove())
    		{
    		setNextPlayer();
    		return;
    		}
    	
        if (isBlackToMove())
            calc_board.setCellBlack( move.getX(), move.getY() );
        else
            calc_board.setCellWhite( move.getX(), move.getY() );
        
        setNextPlayer();
        
        if (move.did_captures) {
        	buildGroups();
        	remove_dead( move.getX(), move.getY() );
        }
        
        //moves.add(new byte[] { x,y} );
        //act_move=move;
    }

    public boolean canUndo() {
        //return (moves.size()>0);
    	return !act_move.isFirstMove();
    }
    
    
    //Vector<byte[]> moves_history=null;
    /**
     * 
     * undo the last move
     * 
     */
    
    public void undo() {
    	//jump(moves.size()-1);
    	jump(act_move.getParent());
    	game_finished=false;
    }
    
    public void redo(int var) {
    	Log.i("gobandroid " , "redoing " +act_move.getnextMove(var).toString());
    	
    	
    	jump(act_move.getnextMove(var));
    }
    
    
    public GoMove getFirstMove() {
    	GoMove move=act_move;
    	
    	while(true)
    	{
    		if (move.isFirstMove()) {
    			return move;
    		}
    		move=move.getParent();
    	}
    }
    
    
    public void jumpFirst() {
    	jump(getFirstMove());
    }
    
    public void jumpLast() {
    	GoMove move=act_move;
    	
    	while(true)
    	{
    		if (!move.hasNextMove()) {
    			jump(move);
    			return;
    		}
    		move=move.getnextMove(0);
    	}

    	
    }
    
    public void jump(GoMove move) {
    	last_action_was_pass=false;
        clear_calc_board();
   
        
        Vector <GoMove> replay_moves=new Vector<GoMove>();
     
        
        	replay_moves.add(move);
        	while (true)
        	{

        		if (replay_moves.lastElement().isFirstMove()) 
        			break;
        		
        		Log.i("gobandroid" , "adding" + replay_moves.lastElement().toString() );
        		replay_moves.add(replay_moves.lastElement().getParent());
        		
        	}
        
        
        
        /*
        
        if (moves_history==null)
        	moves_history=  (Vector<byte[]>)moves.clone();
         */
        
        reset();
        act_move=getFirstMove();
        
        //Log.i("gobandroid"," replaying " + replay_moves.size() +" moves" );
        for (int step=replay_moves.size()-1 ; step>=0;step--)
            do_internal_move(replay_moves.get(step));
        
        visual_board=calc_board.clone();    	
    }
   
    
    public boolean cell_has_liberty(int x , int y )    {
      
      return ( ((x != 0)&&(calc_board.isCellFree( x- 1, y ) ))
          ||
       ((y != 0)&&(calc_board.isCellFree( x, y - 1 )))
          ||  
       ((x != (calc_board.getSize() - 1))&& (calc_board.isCellFree( x + 1, y )))
          ||  
       ((y != (calc_board.getSize() - 1))&& (calc_board.isCellFree( x, y + 1 ) ))
      );  
    }
   

    public boolean cell_has_white_neighbours(int x , int y )    {
      
      return ( ((x != 0)&&(calc_board.isCellWhite( x- 1, y ) ))
          ||
       ((y != 0)&&(calc_board.isCellWhite( x, y - 1 )))
          ||  
       ((x != (calc_board.getSize() - 1))&& (calc_board.isCellWhite( x + 1, y )))
          ||  
       ((y != (calc_board.getSize() - 1))&& (calc_board.isCellWhite( x, y + 1 ) ))
      );  
    }
   
    public boolean cell_has_black_neighbours(int x , int y )    {
        
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
     * @return boolean weather the group has a liberty
     * 
     */
    public boolean hasGroupLibertie(int group2check ) {
        for (int xg = 0; xg < getBoardSize(); xg++)
            for (int yg = 0; yg < getBoardSize(); yg++)
                if ((groups[xg][yg]==group2check)&&(cell_has_liberty(xg,yg)))
                     return true; // if one of the stones in the group has a liberty -> return true because then the group has a liberty
        return false;  // found no stone in the group with a liberty 
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
            		   
        return res;  // found no stone in the group with a liberty 
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
            		   
        return res;  // found no stone in the group with a liberty 
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
            for (int y = 0; y < calc_board.getSize(); y++) {
                groups[x][y] = -1;
            }
        
        for (int x = 0; x < calc_board.getSize(); x++)
            for (int y = 0; y < calc_board.getSize(); y++) {
                if (!calc_board.isCellFree( x, y )) {

                    if (x > 0) {
                        if (!calc_board.areCellsEqual( x, y, x - 1, y )) {
                            group_count++;
                            groups[x][y] = group_count;
                        }
                        else
                            groups[x][y] = groups[x - 1][y];
                    }
                    else {
                        group_count++;
                        groups[x][y] = group_count;
                    }
                    
                    if (y > 0) {
                        if (calc_board.areCellsEqual( x, y, x , y-1 )) {
                            int from_grp=groups[x][y];
                            
                            for (int xg = 0; xg < calc_board.getSize(); xg++)
                                for (int yg = 0; yg < calc_board.getSize(); yg++)
                                    if (groups[xg][yg]==from_grp)
                                        groups[xg][yg]=groups[x][y-1];
                        }
                    }

                }
            }

    }

    
    int area_group_count=0;
    
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
            for (int y = 0; y < calc_board.getSize(); y++) {
            	if (isAreaGroupWhites(area_groups[x][y]))
        			{ 
            		area_assign[x][y]=PLAYER_WHITE;
            		territory_white++;
        			}
            	else if (isAreaGroupBlacks(area_groups[x][y])) {
            		territory_black++;
            		area_assign[x][y]=PLAYER_BLACK;
            	}
        			
            }
        
    }

    
    /**
     * 
     *  detect if there are dead groups on the board
     *   
     * the cell with ignore_x and ignore_y is ignored - e.g. last move
     *  
    **/
    public boolean isDeadGroupOnBoard(byte ignore_x,byte ignore_y) {
                
        for (int grp=0;grp<=group_count;grp++)
        {
            if (groups[ignore_x][ignore_y]==grp)
                    continue;
            
            boolean grp_living=false;
            int grp_members=0;
            for (int xg = 0; xg < calc_board.getSize(); xg++)
                for (int yg = 0; yg < calc_board.getSize(); yg++)
                    if (groups[xg][yg]==grp)
                        {
                    	grp_members++;
                    	grp_living |= cell_has_liberty(xg,yg);
                        }
                        
            
            if ((!grp_living)&&(grp_members>0)) {
            	Log.d("gobandroid " ,"Grp living" + grp);
            	return true;
            	
            }
        }
        
        return false; // found no dead group
    }



    /** 
     * 
     * remove dead groups from the board - e.g. after a move 
     * 
     * the cell with ignore_x and ignore_y is ignored - e.g. last move
     * 
     * **/
    public void remove_dead(byte ignore_x,byte ignore_y) {
                
        for (int grp=0;grp<=group_count;grp++) // iterate over all groups
        {

            if (groups[ignore_x][ignore_y]==grp)
                    continue;
            
            boolean grp_living=false;
            for (int xg = 0; xg < calc_board.getSize(); xg++)
                for (int yg = 0; yg < calc_board.getSize(); yg++)
                    if (groups[xg][yg]==grp)
                        grp_living |= cell_has_liberty(xg,yg);
                        
            if (!grp_living) 
                for (int xg = 0; xg < calc_board.getSize(); xg++)
                    for (int yg = 0; yg < calc_board.getSize(); yg++)
                        if (groups[xg][yg]==grp)
                        	{
                        	
                        	if (calc_board.isCellBlack(xg, yg))
                        			captures_white++;
                        	else
                        			captures_black++;
                        	
                        	calc_board.setCellFree(xg,yg );
                        	}
            
        }

    }

    /** 
     * 
     * return if it's a handicap stone so that the view can visualize it
     * 
     * TODO: - check rename ( general marker ) 
     * 		 - check caching ( in arr cuz speed )
     * 
     * **/
    public boolean isPosHoschi(byte x,byte y) {
    	
    	if ((x==0)||(y==0)||((y+1)==getBoardSize())||((x+1)==getBoardSize()))
    		return false;
    	
    	switch(getBoardSize())
    	{
    	case 9:
    		return (((x%2)==0)&&((y%2)==0));
    	case 13:
    		return (((x%3)==0)&&((y%3)==0));
    	
    	case 19:
    		return (((x==9)&&(y==9))|| 
    				((x==3)&&(y==3)) ||
    				((x==15)&&(y==15)) ||
    				((x==3)&&(y==15)) ||
    				((x==15)&&(y==3)) ||
    				((x==9)&&(y==3)) ||
    				((x==3)&&(y==9)) ||
    				((x==9)&&(y==15))||
    				((x==15)&&(y==9)) );
    		
    		
    	default:	
    		return false;
    	}
    	
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
    	return (act_player==PLAYER_BLACK);
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
    	act_player=(act_player==PLAYER_BLACK)?PLAYER_WHITE:PLAYER_BLACK;
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
		return go_mover;
	}
}