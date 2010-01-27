package org.ligi.gobandroid.logic;

public interface GoDefinitions {

	public final static byte PLAYER_BLACK=1;
	public final static byte PLAYER_WHITE=2;


	public final static byte STONE_NONE=0;
	public final static byte STONE_BLACK=1;
	public final static byte STONE_WHITE=2;

	
	public final static byte[][] hoshis19x19= { 
			 {15,3}, {3,15}, {15,15} , {3,3} , {9,9}
			,{3,9} , {15,9}, {9,3}   , {9,15}
		};
	
	public final static  byte[][] hoshis13x13= { 
			 {9,3}, {3,9}, {9,9} , {3,3} , {6,6}
			,{3,6} , {9,6}, {6,3}   , {6,9}
		};
	
	public final static byte[][] hoshis9x9= { 
			 {6,2}, {2,6}, {6,6} , {2,2} , {4,4}
			,{2,4} , {6,4}, {4,2}   , {4,6}
		};
	


}	
