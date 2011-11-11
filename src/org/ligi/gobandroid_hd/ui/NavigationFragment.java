package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.logic.GoGameProvider;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NavigationFragment extends Fragment implements GoGameChangeListener {

	private Button next_btn,prev_btn,first_btn,last_btn;
	private GoGame game;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		View res=inflater.inflate(R.layout.nav_button_container, container, false);
		first_btn=(Button)res.findViewById(R.id.btn_first);
		last_btn=(Button)res.findViewById(R.id.btn_last);
		next_btn=(Button)res.findViewById(R.id.btn_next);
		prev_btn=(Button)res.findViewById(R.id.btn_prev);
		

		game=GoGameProvider.getGame();
		game.addGoGameChangeListener(this);

		first_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				game.jumpFirst();
			}
			
		});

		last_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				game.jumpLast();
			}
			
		});
		
		next_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				gameNavNext();
			}
			
		});

		prev_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				gameNavPrev();
			}
			
		});
		


		updateButtonStates();
        return res;
    }
	
	@Override
	public void onGoGameChange() {
		updateButtonStates();				
	}
	
	private void updateButtonStates() {
		first_btn.setVisibility( game.canUndo()?View.VISIBLE:View.INVISIBLE );
		prev_btn.setVisibility( game.canUndo()?View.VISIBLE:View.INVISIBLE );
		next_btn.setVisibility( game.canRedo()?View.VISIBLE:View.INVISIBLE );
		last_btn.setVisibility( game.canRedo()?View.VISIBLE:View.INVISIBLE );
	}

	@Override
	public void onDestroyView() {
		game.removeGoGameChangeListener(this);
		super.onDestroyView();
	}
		
	public void gameNavNext() {
		if (!game.canRedo())
			return ;

		if (game.getPossibleVariationCount()>0)	{
			LinearLayout lin=new LinearLayout(this.getActivity());
			LinearLayout li=new LinearLayout(this.getActivity());

			TextView txt =new TextView(this.getActivity());
	
			// show the comment when there is one - useful for SGF game problems
			if (game.getActMove().hasComment())
				txt.setText(game.getActMove().getComment());
			else
				txt.setText("" +( game.getPossibleVariationCount()+1) + " Variations found for this move - which should we take?");
		
			txt.setPadding(10, 2, 10, 23);
			lin.addView(txt);
			lin.addView(li);
			lin.setOrientation(LinearLayout.VERTICAL);
			
			final Dialog select_dlg=new Dialog(this.getActivity());
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
				}
			};
			
			li.setWeightSum(1.0f*(game.getPossibleVariationCount()+1));
			li.setLayoutParams(new LinearLayout.LayoutParams( LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			
			for (Integer i=0;i<game.getPossibleVariationCount()+1;i++)
				{
				Button var_btn=new Button(this.getActivity());
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
	}
	
	public void gameNavPrev() {
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
	}
	

}
