package org.ligi.gobandroid_hd.ui.sgf_listing;

import java.io.File;
import java.io.IOException;

import org.ligi.android.common.files.FileHelper;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.SGFHelper;
import org.ligi.gobandroid_hd.ui.PreviewView;
import org.ligi.gobandroid_hd.ui.review.SGFMetaData;
import org.ligi.gobandroid_hd.ui.tsumego.TsumegoHelper;
import org.ligi.tracedroid.logging.Log;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

class TsumegoPathViewAdapter extends BaseAdapter {
	
	private Activity activity;
	private String[] menu_items;
	private String path;
	
	public TsumegoPathViewAdapter(Activity activity,String[] menu_items,String path) {
		this.activity=activity;
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
			container.setOrientation(LinearLayout.HORIZONTAL);
			
			container.setVisibility(View.GONE);
			
		}
		else
			v= inflater.inflate(R.layout.sgf_tsumego_list_item,null);
					
		TextView title_tv=(TextView)v.findViewById(R.id.filename);
		
		if (title_tv!=null) {
			title_tv.setText(menu_items[position].replace(".sgf", ""));
		}
		
		String sgf_str="";
		
		if (GoLink.isGoLink(base_fname)) {
			GoLink gl=new GoLink(base_fname);
			sgf_str=gl.getSGFString();
		} else {
			try {
				sgf_str=FileHelper.file2String(new File(base_fname));
			} catch (IOException e) {	}
		}
		
		GoGame game=SGFHelper.sgf2game(sgf_str, null,SGFHelper.BREAKON_FIRSTMOVE);
		LinearLayout container = (LinearLayout)v.findViewById(R.id.thumb_container);
		
		if (game!=null) {
			
			int transform=TsumegoHelper.calcTransform(game);
			
			if (transform!=SGFHelper.DEFAULT_SGF_TRANSFORM)			
				game=SGFHelper.sgf2game(sgf_str, null,SGFHelper.BREAKON_FIRSTMOVE,transform);
			
			game.jump(game.getFirstMove());
			container.addView(new PreviewView(activity,game));
		}
			
		
		Log.i("loadingSGF " + base_fname);
		
		ImageView solve_img=(ImageView)v.findViewById(R.id.solve_status_image);
		
		
		if ((solve_img!=null)&&(new SGFMetaData(base_fname).getIsSolved())) {
			solve_img.setImageResource(R.drawable.solved);
		}
		
		return v;
	}
	
}