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

package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGameProvider;
import org.ligi.tracedroid.logging.Log;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * Activity for a Go Game
 * 
 * @author <a href="http://ligi.de">Marcus -Ligi- Bueschleb</a>
 * 
 * License: This software is licensed with GPLv3
 * 
 **/

public 	class GoActivity 
		extends Activity 
{

	private GoBoardViewHD go_board=null;
	private GoGame game;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.game);
		
		go_board=(GoBoardViewHD)findViewById(R.id.go_board);
		Log.i(" Board" + go_board);
		
		game=GoGameProvider.getGame();
	}

	public void gameNavFirst(View v) {
		game.jumpFirst();
		go_board.invalidate();
	}
	
	public void gameNavNext(View v) {
		if (!game.canRedo())
			return game.redo(1); 

		if (game.getPossibleVariationCount()>0)	{
			LinearLayout lin=new LinearLayout(this);
			LinearLayout li=new LinearLayout(this);

			TextView txt =new TextView(this);
	
			// show the comment when there is one - useful for SGF game problems
			if (game.getActMove().hasComment())
				txt.setText(game.getActMove().getComment());
			else
				txt.setText("" +( game.getPossibleVariationCount()+1) + " Variations found for this move - which should we take?");
		
			txt.setPadding(10, 2, 10, 23);
			lin.addView(txt);
			lin.addView(li);
			lin.setOrientation(LinearLayout.VERTICAL);
			
			final Dialog select_dlg=new Dialog(this);
			final Boolean redoing=false;
			View.OnClickListener var_select_listener=new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (redoing)
						return;
					select_dlg.hide();
					if (!v.isEnabled()) return;
					v.setEnabled(false	);
					
					game.redo((Integer)(v.getTag()));
				
					//updateButtonState();
					go_board.invalidate();
				}
			};
			
			li.setWeightSum(1.0f*(game.getPossibleVariationCount()+1));
			li.setLayoutParams(new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			
			for (Integer i=0;i<game.getPossibleVariationCount()+1;i++)
				{
				Button var_btn=new Button(this);
				var_btn.setTag(i);
				var_btn.setOnClickListener(var_select_listener );
				if (game.getActMove().getnextMove(i).isMarked())
					var_btn.setText(game.getActMove().getnextMove(i).getMarkText());
				else
					var_btn.setText(""+(i+1));
					
				li.addView(var_btn);
		
				var_btn.setLayoutParams(new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
				}

			select_dlg.setTitle(R.string.variations);
			select_dlg.setContentView(lin);
			
			select_dlg.show();
		}
		else
			game.redo(0);
		go_board.invalidate();
	}
	
	public void gameNavPrev(View v) {
		if (!game.canUndo())
			return;
		
		// dont do it if the mover has to move at the moment
		if (game.getGoMover().isMoversMove())
			return;
	
		game.getGoMover().paused=true;
		game.undo();

		// 	undo twice if there is a mover
		if (game.canUndo()&&(game.getGoMover().isMoversMove()))
			game.undo();	
	
		game.getGoMover().paused=false;
		go_board.invalidate();
	}
	
	public void gameNavLast(View v) {
		game.jumpLast();
		go_board.invalidate();
	}
}