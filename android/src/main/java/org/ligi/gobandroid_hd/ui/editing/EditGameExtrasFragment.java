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
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.ui.fragments.GobandroidFragment;

import java.util.List;

public class EditGameExtrasFragment extends GobandroidFragment implements GoGameChangeListener {

    private EditText editText;
    private EditModeItemPool editModePool;

    public EditGameExtrasFragment(EditModeItemPool editModePool) {
        this.editModePool = editModePool;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.edit_extras, null);

        final Context ctx = container.getContext();

        final GridView mode_grid = (GridView) view.findViewById(R.id.gridView);

        class ModeAdapter extends ArrayAdapter<EditModeItem> {

            public ModeAdapter(Context context, int textViewResourceId, List<EditModeItem> objects) {
                super(context, textViewResourceId, objects);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                final View view = inflater.inflate(R.layout.edit_mode_item, null);
                final ImageView img_v = (ImageView) view.findViewById(R.id.imageView);
                img_v.setImageResource(getItem(position).icon_resId);

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
        editText = (EditText) view.findViewById(R.id.comment_et);

        getGame().addGoGameChangeListener(this);

        editText.setText(getGame().getActMove().getComment());
        editText.setHint(R.string.enter_your_comments_here);
        editText.setGravity(Gravity.TOP);
        editText.setTextColor(getResources().getColor(R.color.text_color_on_board_bg));

        editText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                getGame().getActMove().setComment(s.toString());
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
        if (editText == null || getActivity() == null) {
            return; // no user facing action
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                editText.setText(getGame().getActMove().getComment());
            }
        });
    }

}
