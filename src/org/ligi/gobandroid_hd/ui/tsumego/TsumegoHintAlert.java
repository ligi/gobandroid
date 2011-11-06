package org.ligi.gobandroid_hd.ui.tsumego;


import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGameProvider;
import org.ligi.gobandroid_hd.logic.GoMarker;
import org.ligi.gobandroid_hd.logic.GoMove;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class TsumegoHintAlert {
	
	public static void show(Activity activity,final GoMove finishing_move) {
		
		new AlertDialog.Builder(activity)
			.setTitle(R.string.hint)
			.setItems(R.array.hint_options,new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int item) {
        			switch(item) {
        			case 0:
        				mark_path(finishing_move,true);
        				break;

        			case 1:
        				mark_path(finishing_move,true);
        				break;
        				
        			case 2:
        				show_numbered_solution(finishing_move);
        				break;
        			}
         	    }

        	})

			.show();
	}
	

	private static void show_numbered_solution(GoMove finishing_move) {

    	GoMove myActMove=finishing_move;
    	int p=myActMove.getMovePos();
    	while (true) {
    		if(myActMove.isFirstMove())
    			break;
    		finishing_move.addMarker(new GoMarker(myActMove.getX(),myActMove.getY(),""+p));
    		p--;
    		myActMove=myActMove.getParent();
    		
    	}
    	
    	GoGameProvider.getGame().jump(finishing_move);
	}
	
	private static void mark_path(GoMove finishing_move,boolean complete) {

    	GoMove myActMove=finishing_move;
    	while (true) {
    		if(myActMove.isFirstMove())
    			break;
    		if (complete||( myActMove.getParent()==GoGameProvider.getGame().getActMove()))
    			myActMove.getParent().addMarker(new GoMarker(myActMove.getX(),myActMove.getY(),"X"));
    		myActMove=myActMove.getParent();
    	}
    	
    	GoGameProvider.getGame().notifyGameChange();
	}
	
}
