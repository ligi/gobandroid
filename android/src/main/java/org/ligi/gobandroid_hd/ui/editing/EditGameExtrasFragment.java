package org.ligi.gobandroid_hd.ui.editing;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
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

import org.ligi.axt.simplifications.SimpleTextWatcher;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.ui.fragments.GobandroidFragment;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EditGameExtrasFragment extends GobandroidFragment implements GoGameChangeListener {

    @InjectView(R.id.comment_et)
    EditText editText;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final StatefulEditModeItems editModePool = ((EditGameActivity) getActivity()).getStatefulEditModeItems();
        final View view = inflater.inflate(R.layout.edit_extras, container, false);

        final Context ctx = container.getContext();

        final GridView mode_grid = (GridView) view.findViewById(R.id.gridView);

        mode_grid.setAdapter(new ArrayAdapter<EditModeItem>(ctx, R.layout.edit_mode_item, editModePool.getList()) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                final ImageView view = (ImageView) inflater.inflate(R.layout.edit_mode_item, parent, false);
                view.setImageResource(getItem(position).iconResId);
                view.setContentDescription(getString(getItem(position).contentDescriptionResId));
                if (editModePool.getActivatedItem() == position) {
                    view.setBackgroundColor(ctx.getResources().getColor(R.color.dividing_color));
                }

                return view;
            }
        });

        mode_grid.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {
                editModePool.setActivatedItem(position);
                ((ArrayAdapter<?>) adapter.getAdapter()).notifyDataSetChanged();
            }

        });

        ButterKnife.inject(this, view);

        App.getGame().addGoGameChangeListener(this);

        editText.setText(App.getGame().getActMove().getComment());
        editText.setHint(R.string.enter_your_comments_here);
        editText.setGravity(Gravity.TOP);
        editText.setTextColor(getResources().getColor(R.color.text_color_on_board_bg));

        editText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                App.getGame().getActMove().setComment(s.toString());
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        App.getGame().removeGoGameChangeListener(this);
        super.onDestroyView();
    }

    @Override
    public void onGoGameChange() {
        if (getActivity() == null) {
            return; // no user facing action
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                editText.setText(App.getGame().getActMove().getComment());
            }
        });
    }

}
