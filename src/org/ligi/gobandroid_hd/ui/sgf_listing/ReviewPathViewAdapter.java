package org.ligi.gobandroid_hd.ui.sgf_listing;

import java.io.File;
import java.io.IOException;

import org.ligi.android.common.files.FileHelper;
import org.ligi.gobandroid_beta.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.MetaDataFormater;
import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.gobandroid_hd.ui.review.SGFMetaData;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

class ReviewPathViewAdapter extends BaseAdapter {

	private Activity activity;
	private String[] menu_items;
	private String path;

	public ReviewPathViewAdapter(Activity activity, String[] menu_items, String path) {
		this.activity = activity;
		this.menu_items = menu_items;
		this.path = path;
	}

	@Override
	public int getCount() {
		return menu_items.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// View v =
		// activity.getLayoutInflater().inflate(R.layout.sgf_tsumego_list_item,null);
		String base_fname = path + "/" + menu_items[position];

		View v;

		if (new File(base_fname).isDirectory()) {
			v = inflater.inflate(R.layout.sgf_dir_list_item, null);

			LinearLayout container = (LinearLayout) v.findViewById(R.id.thumb_container);
			// container.setOrientation(LinearLayout.HORIZONTAL);
			container.setVisibility(View.GONE);
			// TODO really remove the image container
		} else {
			v = inflater.inflate(R.layout.review_game_details_list_item, null);

			GoGame game = null;

			try {
				String sgf_str;

				if (GoLink.isGoLink(base_fname)) {
					GoLink gl = new GoLink(base_fname);

					base_fname = gl.getFileName();
					((TextView) v.findViewById(R.id.game_link_extra_infos)).setText("Move #" + gl.getMoveDepth());
				} else {
					((TextView) v.findViewById(R.id.game_link_extra_infos)).setVisibility(View.GONE);

				}

				sgf_str = FileHelper.file2String(new File(base_fname));
				game = SGFHelper.sgf2game(sgf_str, null, SGFHelper.BREAKON_FIRSTMOVE);
				SGFMetaData sgf_meta = new SGFMetaData(base_fname + SGFMetaData.FNAME_ENDING);

				if (game != null) {
					MetaDataFormater meta = new MetaDataFormater(game);

					TextView player_white_tv = (TextView) v.findViewById(R.id.player_white);
					if (player_white_tv != null) {
						if (meta.getWhitePlayerString().equals("")) {
							((ImageView) v.findViewById(R.id.player_white_stone_img)).setVisibility(View.GONE);
							player_white_tv.setVisibility(View.GONE);
						} else
							player_white_tv.setText(meta.getWhitePlayerString());
					}

					TextView player_black_tv = (TextView) v.findViewById(R.id.player_black);

					if (player_black_tv != null) {

						if (meta.getBlackPlayerString().equals("")) {
							((ImageView) v.findViewById(R.id.player_black_stone_img)).setVisibility(View.GONE);
							player_black_tv.setVisibility(View.GONE);
						} else
							player_black_tv.setText(meta.getBlackPlayerString());
					}

					TextView title_tv = (TextView) v.findViewById(R.id.game_extra_infos);

					if (title_tv != null) {
						title_tv.setText(meta.getExtrasString());
					}

					RatingBar rating_bar = (RatingBar) v.findViewById(R.id.game_rating);
					if (!sgf_meta.hasData())
						rating_bar.setVisibility(View.GONE);
					else if (sgf_meta.getRating() != null)
						rating_bar.setRating(.5f * sgf_meta.getRating());
				}

			} catch (IOException e) {
			}

		}

		TextView title_tv = (TextView) v.findViewById(R.id.filename);

		if (title_tv != null) {
			title_tv.setText(menu_items[position]);
		}

		return v;
	}

}