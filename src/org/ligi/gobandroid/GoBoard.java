package org.ligi.gobandroid;

import android.util.Log;


/**
 * Class to represent a Go Board
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 *         
 * This software is licenced with GPLv3 
**/

public class GoBoard {
    
    private int size;
    public int[][] board;
    
    public GoBoard( int size ) {
        this.size=size;
        board=new int[size][size];
    }

    public GoBoard( int size,int[][] predefined_board ) {
   
    	this.size=size;
        board=new int[size][size];
   
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
    	
    	// if the size is not matching the boards can't be equal
    	if (size!=other.size) 
    		return false;
    	
    	// check if all stones are placed equaly
    	boolean equal=true; // be positive
    	for( int x=0;x<size;x++)
    		for( int y=0;y<size;y++)
    			equal&=(board[x][y]==other.board[x][y]);

    	return equal;
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
        return (board[x][y]==0); 
    }
    public boolean isCellBlack( int x, int y ) {
        return (board[x][y]==1); 
    }
    
    public boolean isCellWhite( int x, int y ) {
        return (board[x][y]==2); 
    }


    public boolean areCellsEqual( int x, int y , int x2 , int y2 ) {
        return (board[x][y]==board[x2][y2]); 
    }


    public void setCellFree( int x, int y ) {
        board[x][y]=0; 
    }

    public void setCellBlack( int x, int y ) {
        board[x][y]=1; 
    }
    public void setCellWhite( int x, int y ) {
        board[x][y]=2; 
    }



}