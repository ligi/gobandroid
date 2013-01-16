package org.ligi.gobandroid_hd.ui.editing;

import java.util.List;

import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.ui.fragments.GobandroidFragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

public class EditGameExtrasFragment extends GobandroidFragment implements GoGameChangeListener {

	private EditText et;
	private Handler hndl = new Handler();
	private Context ctx;
	private LayoutInflater mLayoutInflater;
	private EditModeItemPool editModePool;

	public EditGameExtrasFragment(EditModeItemPool editModePool) {
		this.editModePool = editModePool;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mLayoutInflater = inflater;
		View view = inflater.inflate(R.layout.edit_extras, null);

		ctx = container.getContext();

		GridView mode_grid = (GridView) view.findViewById(R.id.gridView);

		class ModeAdapter extends ArrayAdapter<EditModeItem> {

			public ModeAdapter(Context context, int textViewResourceId, List<EditModeItem> objects) {
				super(context, textViewResourceId, objects);
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				EditModeItem item = getItem(position);

				View view = mLayoutInflater.inflate(R.layout.edit_mode_item, null);
				ImageView img_v = (ImageView) view.findViewById(R.id.imageView);
				img_v.setImageResource(item.icon_resId);

				if (editModePool.getActivatedItem() == position) {
					view.setBackgroundColor(ctx.getResources().getColor(R.color.dividing_color));
				}

				return view;
			}

		}

		mode_grid.setAdapter(new ModeAdapter(ctx, R.layout.edit_mode_item, editModePool.getList()));

		mode_grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View arg1, int postion, long arg3) {
				editModePool.setActivateItem(postion);
				((ArrayAdapter<?>) adapter.getAdapter()).notifyDataSetChanged();
			}

		});
		et = (EditText) view.findViewById(R.id.comment_et);

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
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});

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
				if ((et != null) && getActivity() != null)
					et.setText(getGame().getActMove().getComment());
			}

		});
	}

}
