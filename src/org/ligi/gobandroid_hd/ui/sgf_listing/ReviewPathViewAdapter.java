package org.ligi.gobandroid_hd.ui.sgf_listing;

import java.io.File;
import java.io.IOException;

import org.ligi.android.common.files.FileHelper;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGameMetadata;
import org.ligi.gobandroid_hd.logic.MetaDataFormater;
import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.tracedroid.logging.Log;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

class ReviewPathViewAdapter extends BaseAdapter {
	
	private Activity activity;
	private int listItem;
	private String[] menu_items;
	private String path;
	
	public ReviewPathViewAdapter(Activity activity, int listItem,
			String[] menu_items,String path) {
		this.activity=activity;
		this.listItem=listItem;
		this.menu_items=menu_items;
		this.path=path;
	}

	@Override
	public int getCount() {
		return menu_items.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		//View v = activity.getLayoutInflater().inflate(R.layout.sgf_tsumego_list_item,null);
		String base_fname=path+"/"+menu_items[position];
		
		
		View v;
		
		if (new File(base_fname).isDirectory()) {
			v= inflater.inflate(R.layout.sgf_dir_list_item,null);
			
			LinearLayout container = (LinearLayout)v.findViewById(R.id.thumb_container);
			//container.setOrientation(LinearLayout.HORIZONTAL);
			container.setVisibility(View.GONE);
			// TODO really remove the image container
		}
		else {
			v= inflater.inflate(R.layout.review_game_details_list_item,null);
			
			GoGame game=null;
			
			try {
				game=SGFHelper.sgf2game(FileHelper.file2String(new File(base_fname)), null,SGFHelper.BREAKON_FIRSTMOVE);

				if (game!=null) {
					MetaDataFormater meta=new MetaDataFormater(game);
					
					TextView player_white_tv=(TextView)v.findViewById(R.id.player_white);
					
					if (player_white_tv!=null) {
						player_white_tv.setText(meta.getWhitePlayerString());
					}
					
					TextView player_black_tv=(TextView)v.findViewById(R.id.player_black);
					
					if (player_black_tv!=null) {
						player_black_tv.setText(meta.getBlackPlayerString());
					}
					
					TextView title_tv=(TextView)v.findViewById(R.id.game_extra_infos);
				
					if (title_tv!=null) {
						title_tv.setText(meta.getExtrasString());
					}
				}	

			} catch (IOException e) {
			}
			
		}
		String img_fname=path+"/"+menu_items[position]+".png";

		TextView title_tv=(TextView)v.findViewById(R.id.filename);
		
		if (title_tv!=null) {
			title_tv.setText(menu_items[position]);
		}
		
		return v;
	}
	
	
	
	
}