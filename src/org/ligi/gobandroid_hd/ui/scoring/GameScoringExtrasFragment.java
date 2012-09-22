package org.ligi.gobandroid_hd.ui.scoring;

import org.ligi.gobandroid_beta.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.ui.fragments.GobandroidFragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class GameScoringExtrasFragment extends GobandroidFragment implements GoGameChangeListener {

	private EditText et;
	private Handler hndl=new Handler();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	
		View view=inflater.inflate(R.layout.game_result,null);
		
		GoGame game=getGame();
		
		String game_fin_txt="";
		if (game.getPointsBlack()==game.getPointsWhite())
			 game_fin_txt=getResources().getString(R.string.game_ended_in_draw);
					
		if (game.getPointsBlack()>game.getPointsWhite())
			game_fin_txt=(getString(R.string.black_won_with) + (game.getPointsBlack()-game.getPointsWhite()) + getString(R.string._points_));
					
		if (game.getPointsWhite()>game.getPointsBlack())
			game_fin_txt=(getString(R.string.white_won_with_) + (game.getPointsWhite()-game.getPointsBlack()) + getString(R.string._points_));
		
		
		((TextView)view.findViewById(R.id.result_txt)).setText(game_fin_txt);
		
		((TextView)view.findViewById(R.id.territory_black)).setText(""+game.territory_black);
		((TextView)view.findViewById(R.id.territory_white)).setText(""+game.territory_white);
		
		((TextView)view.findViewById(R.id.captures_black)).setText(""+game.getCapturesBlack());
		((TextView)view.findViewById(R.id.captures_white)).setText(""+game.getCapturesWhite());
		
		((TextView)view.findViewById(R.id.komi)).setText(""+game.getKomi());
		
		((TextView)view.findViewById(R.id.final_black)).setText(""+game.getPointsBlack());
		((TextView)view.findViewById(R.id.final_white)).setText(""+game.getPointsWhite());
		
		return view;
	}

	
	@Override
	public void onDestroyView() {
		getGame().removeGoGameChangeListener(this);
		super.onDestroyView();
	}


	@Override
	public void onGoGameChange() {
		hndl.post(new Runnable() {
			@Override
			public void run() {
				if ((et!=null)&&getActivity()!=null)
					et.setText(getGame().getActMove().getComment());
			}
			
		});
	}
	
}
