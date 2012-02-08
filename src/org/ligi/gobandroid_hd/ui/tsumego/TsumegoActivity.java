package org.ligi.gobandroid_hd.ui.tsumego;

import java.util.Vector;

import org.ligi.android.common.dialogs.ActivityFinishOnDialogClickListener;
import org.ligi.android.common.dialogs.DialogDiscarder;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.logic.GoMove;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.tracedroid.logging.Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;

public class TsumegoActivity extends GoActivity implements GoGameChangeListener {
	
	private GoMove finishing_move;

	private TsumegoGameExtrasFragment myTsumegoExtrasFragment;
	private Vector<GoMove> on_path_moves;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setTitle(R.string.tsumego);
		
		// build a on path Vector to do a fast isOnPath() later 
		on_path_moves=new Vector<GoMove>();
		
		recursive_add_on_path_moves(game.getFirstMove());
				
		// try to find the correct solution
		
		if (!isFinishingMoveKnown()) 
			new AlertDialog.Builder(this).setMessage(R.string.tsumego_sgf_no_solution)
			.setNegativeButton("OK",new DialogDiscarder())
			.setPositiveButton("go back",new ActivityFinishOnDialogClickListener(this))
			.show();

		game.addGoGameChangeListener(this);
		
		float myZoom=calcZoom(game);
		
		getBoard().setZoom(myZoom);
		int poi=game.getSize()-(int)(game.getSize()/2f/myZoom);
		getBoard().setZoomPOI(poi+poi*game.getSize());
    }
    
    private GoMove getFinishingMove() {
    	if (finishing_move==null)
    		finishing_move=getCorrectMove(game.getFirstMove());
		
    	return finishing_move;
    }
    private boolean isFinishingMoveKnown() {
    	return getFinishingMove()!=null;
    }
    
    public static int calcPOI(GoGame game) {
    	int poi=game.getSize()-(int)(game.getSize()/2f/calcZoom(game));
		return poi+poi*game.getSize();
    }
    /**
     * calculate a Zoom factor so that all stones in handicap fit on bottom right area
     * 
     * @return - the calculated Zoom factor
     */    
    public static float calcZoom(GoGame game) {
    	int min_x=game.getSize();
		int min_y=game.getSize();
		for (int x=0;x<game.getSize();x++)
			for (int y=0;y<game.getSize();y++) {
				if ((x<min_x)&&!game.getHandicapBoard().isCellFree(x, y))
					min_x=x;
				if ((y<min_y)&&!game.getHandicapBoard().isCellFree(x, y))
					min_y=y;
			}
		
		int max_span_size=Math.max(game.getSize()-min_x, game.getSize()-min_y);
	
		float res=(float)game.getSize()/(max_span_size+2);
		
		if (res<1.0f)
			return 1.0f;
		else
			return res;
    }
    
    private void recursive_add_on_path_moves(GoMove act) {
    	on_path_moves.add(act);
    	if (act.hasNextMove())
    		for (GoMove child:act.getNextMoveVariations())
    			recursive_add_on_path_moves(child);
    }
    
    @Override
	protected void onDestroy() {
    	game.removeGoGameChangeListener(this);
    	super.onDestroy();
    }

	public boolean isOnPath() {
		return on_path_moves.contains(game.getActMove());
    }
    
	public byte doMoveWithUIFeedback(byte x,byte y) {
		
		byte res=super.doMoveWithUIFeedback(x,y);
		if (res==GoGame.MOVE_VALID)
			if (game.getActMove().hasNextMove())
				game.jump(game.getActMove().getnextMove(0));
			

		game.notifyGameChange();
		return res;
	}


	public GoMove getCorrectMove(GoMove act_mve) {
		if (act_mve.getComment().equals("Correct"))
			return act_mve;
		
		for (GoMove next_moves:act_mve.getNextMoveVariations()) {
			GoMove res=getCorrectMove(next_moves);
			if (res!=null)
				return res;
		}
			
		return null;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	this.getMenuInflater().inflate(R.menu.ingame_tsumego, menu);
    	menu.findItem(R.id.menu_game_hint).setVisible(isFinishingMoveKnown());
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {                                                                                                 
        if (!super.onOptionsItemSelected(item))
        switch (item.getItemId()) {                                                                                                                   
                                                                                                                                                      
        case R.id.menu_game_hint:
        	
        	TsumegoHintAlert.show(this,getFinishingMove());
            break;                                                                                                                                
		}
		
		return false;
	}

    @Override
    public void requestUndo() {
    	// we do not want to keep user-variations in tsumego mode- but we want to keep tsumego variation
    	game.undo(isOnPath());
    	
    	// remove the counter-move if any
    	if (!game.isBlackToMove())
    		game.undo(isOnPath());
    }
    
    @Override
	public Fragment getGameExtraFragment() {
    	//if(myTsumegoExtrasFragment==null)
    	myTsumegoExtrasFragment=new TsumegoGameExtrasFragment();

		return myTsumegoExtrasFragment;
	}

	@Override
	public void onGoGameChange() {
		if (myTsumegoExtrasFragment!=null) {
			myTsumegoExtrasFragment.setOffPathVisibility(!isOnPath());
			myTsumegoExtrasFragment.setCorrectVisibility(game.getActMove().equals(getFinishingMove()));
		}
		if (game.getActMove().equals(getFinishingMove())) {
			this.getBaseContext().getSharedPreferences("tsumego_stats", Activity.MODE_PRIVATE)
			.edit().putInt(game.getMetaData().getFileName(), 100).commit();
		Log.i("finished"+	game.getMetaData().getFileName());
		}
	}

	@Override
	public boolean isAsk4QuitEnabled() {
		return false;
	}
	
}
