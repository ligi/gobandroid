package org.ligi.gobandroid_hd.ui;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.logic.GoGameProvider;
import org.ligi.gobandroid_hd.ui.alerts.GameForwardAlert;

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
		GameForwardAlert.show(this.getActivity(), game);
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
