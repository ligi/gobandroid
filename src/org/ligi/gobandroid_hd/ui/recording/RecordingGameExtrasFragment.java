package org.ligi.gobandroid_hd.ui.recording;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.ui.fragments.GobandroidFragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;

public class RecordingGameExtrasFragment extends GobandroidFragment implements GoGameChangeListener {

	private EditText et;
	private Handler hndl=new Handler();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	
		LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		et=new EditText(getActivity());
		
		getGame().addGoGameChangeListener(this);
		
		et.setText(getGame().getActMove().getComment());
		et.setHint(R.string.enter_your_comments_here);
		et.setGravity(Gravity.TOP);
		et.setTextColor(this.getResources().getColor(R.color.text_color_on_board_bg));
		
		et.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				getGame().getActMove().setComment(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}});

		et.setLayoutParams(lp);
		return et;
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
