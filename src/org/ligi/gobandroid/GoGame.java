package org.ligi.gobandroid;

import java.util.Vector;

/**
 * 
 * Class to represent a Go Game with its rules
 *
 * @author <a href="http://ligi.de">Marcus -LiGi- Bueschleb </a>
 * 
 * This software is licenced with GPLv3         
 */

public class GoGame {

    
    private GoBoard visual_board; // the board to show to the user
    private GoBoard calc_board;

    
    private boolean black_to_move = true;

    private boolean last_action_was_pass=false;
   
    private boolean game_finished=false;
    
    
    private int[][] groups;

    private int group_count = -1;
        
    Vector moves;
    
    public void pass() {
        if (last_action_was_pass) 
            game_finished=true; // finish game if both passed
        else {
            last_action_was_pass=true;
            black_to_move=!black_to_move; // next player
        }
    }
    
    public GoGame( int size ) {
        calc_board = new GoBoard( size );
        visual_board=calc_board.clone();
        moves= new Vector();
        groups = new int[calc_board.getSize()][calc_board.getSize()];
    }

    /**
     *  place a stone on the board
     *
     * @param x
     * @param y
     * @return true if the move was valid - false if invalid move
     */
    public boolean do_move( byte x, byte y ) {
        if ((x >= 0) && (x <= calc_board.getSize()) && (y >= 0) && (y < calc_board.getSize())) { // if x and y are inside the board
            if (calc_board.isCellFree( x, y )) { // cant place a stone where another is allready
                
                if (black_to_move)
                    calc_board.setCellBlack( x, y );
                else
                    calc_board.setCellWhite( x, y );
                
                build_groups();
                
                if (group_has_liberty(groups[x][y])||isDeadGroupOnBoard(x,y)) { // valid move -> do things needed to do 
                    black_to_move = !black_to_move;
                    remove_dead(x,y); }
                else { // was an illegal move -> undo
                    calc_board.setCellFree(x,y );
                    return false;
                }
                visual_board=calc_board.clone();
                last_action_was_pass=false;
                moves.add(new byte[] { x,y} );
                return true;
            }
        }
        return false;
    }

    public boolean canUndo() {
        return (moves.size()>0); 
    }
    public void undo() {
        clear_calc_board();
        Vector _moves=(Vector)moves.clone();
        moves=new Vector();
        black_to_move=true;
        for (int step=0 ; step<_moves.size()-1;step++)
        {
            byte move_x=((byte[])_moves.get(step))[0];
            byte move_y=((byte[])_moves.get(step))[1];
            do_move(move_x,move_y);
        }
        
    }
    
    public int getGroup(byte x,byte y) {
        return groups[x][y];
    }
    public boolean cell_has_liberty(int x , int y )
    {
      
      if ((x != 0)&&(calc_board.isCellFree( x- 1, y ) ))
          return true;
      if ((y != 0)&&(calc_board.isCellFree( x, y - 1 )))
          return true;  
      if ((x != (calc_board.getSize() - 1))&& (calc_board.isCellFree( x + 1, y )))
          return true;  
      if ((y != (calc_board.getSize() - 1))&& (calc_board.isCellFree( x, y + 1 ) ))
          return true;  
      
      return false;
    }
   
    public boolean group_has_liberty(int group2check ) {
        for (int xg = 0; xg < calc_board.getSize(); xg++)
            for (int yg = 0; yg < calc_board.getSize(); yg++)
                if ((groups[xg][yg]==group2check)&&(cell_has_liberty(xg,yg)))
                     return true;
        return false;  // found no stone in the group with a liberty 
    }
    
    
    public void clear_calc_board() {
        for (byte x = 0; x < calc_board.getSize(); x++)
            for (byte y = 0; y < calc_board.getSize(); y++) 
                calc_board.setCellFree(x,y );
    }
    
    public void build_groups() {
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
    
    /** detect dead groups **/
    public boolean isDeadGroupOnBoard(byte ignore_x,byte ignore_y) {
                
        for (int grp=0;grp<=group_count;grp++)
        {
            if (groups[ignore_x][ignore_y]==grp)
                    continue;
            
            boolean grp_living=false;
            for (int xg = 0; xg < calc_board.getSize(); xg++)
                for (int yg = 0; yg < calc_board.getSize(); yg++)
                    if (groups[xg][yg]==grp)
                        grp_living |= cell_has_liberty(xg,yg);
                        
            if (!grp_living) return true;
        }
        
        return false; // found no dead group
    }



    /** remove dead groups **/
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
                            calc_board.setCellFree(xg,yg );
            
        }

    }

    public GoBoard getVisualBoard() {
        return visual_board;
    }

}