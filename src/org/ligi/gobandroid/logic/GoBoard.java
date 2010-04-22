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


/**
 * Class to represent a Go Board
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         
 * This software is licenced with GPLv3 
**/

public class GoBoard implements GoDefinitions{
    
    private byte size;
    public byte[][] board;
    
    public GoBoard( byte size ) {
        this.size=size;
        board=new byte[size][size];
    }

    public GoBoard( byte size,byte[][] predefined_board ) {
   
    	this.size=size;
        board=new byte[size][size];
   
        // copy the board
        for( int x=0;x<size;x++)
    		for( int y=0;y<size;y++)
    			board[x][y]=predefined_board[x][y];
        
    }

    public GoBoard clone() {
        return new GoBoard(size,board);
    }
    
    /*
     * check if two boards are equal
     */
    public boolean equals(GoBoard other) {
    	
    	// cannot be the same if board is null
    	if (other==null)
    		return false;
    	
    	// if the size is not matching the boards can't be equal
    	if (size!=other.size) 
    		return false;
    	
    	// check if all stones are placed equaly
    	
    	for( int x=0;x<size;x++)
    		for( int y=0;y<size;y++)
    			if (board[x][y]!=other.board[x][y])
    			  return false;
    			  
    	return true;
    }
    
    public void logBoard() {	
    	String tmp_str="";
    	
    	for( int y=0;y<size;y++)
    	{
    	for( int x=0;x<size;x++)
    		{
    		if (board[x][y]==0)
        		tmp_str+=" ";
    		else if (board[x][y]==1)
	    		tmp_str+="B";
    		else if (board[x][y]==2)
	    		tmp_str+="W";
    		}
    	Log.d("gobandroid Board",tmp_str);
    	tmp_str="";
    	}
    }
    
    public int getSize() {
        return size;
    }

    public boolean isCellFree( int x, int y ) {
        return (board[x][y]==STONE_NONE) // no stone on board
        		||(board[x][y]<0);  // or dead stone; 
    }
    public boolean isCellBlack( int x, int y ) {
        return (board[x][y]==STONE_BLACK); 
    }
    
    public boolean isCellWhite( int x, int y ) {
        return (board[x][y]==STONE_WHITE); 
    }

    
    public boolean isCellDeadBlack( int x, int y ) {
        return (-board[x][y]==STONE_BLACK); 
    }
    
    public boolean isCellDeadWhite( int x, int y ) {
        return (-board[x][y]==STONE_WHITE); 
    }


    public boolean areCellsEqual( int x, int y , int x2 , int y2 ) {
        return ((board[x][y]==board[x2][y2])||
        		(isCellFree(x,y)&&isCellFree(x2,y2)));
    }


    public void setCellFree( int x, int y ) {
        board[x][y]=STONE_NONE; 
    }

    public void setCellBlack( int x, int y ) {
        board[x][y]=STONE_BLACK; 
    }

    public void setCellWhite( int x, int y ) {
        board[x][y]=STONE_WHITE; 
    }
    
    

    public void toggleCellDead( int x, int y ) {
        board[x][y]*=-1; 
    }

    public boolean isCellDead( int x, int y ) {
        return (board[x][y]<0); 
    }



}