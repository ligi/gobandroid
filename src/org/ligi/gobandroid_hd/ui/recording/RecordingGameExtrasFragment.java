package org.ligi.gobandroid_hd.ui.recording;

import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.logic.GoGameProvider;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;

public class RecordingGameExtrasFragment extends Fragment implements GoGameChangeListener {

	private EditText et;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	
		LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		et=new EditText(this.getActivity());
		
		GoGameProvider.getGame().addGoGameChangeListener(this);
		
		et.setText(GoGameProvider.getGame().getActMove().getComment());
		et.setHint("enter your comments here");
	
		et.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				GoGameProvider.getGame().getActMove().setComment(s.toString());
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
	public void onGoGameChange() {
		if (et!=null)
			et.setText(GoGameProvider.getGame().getActMove().getComment());
	}
	
}
