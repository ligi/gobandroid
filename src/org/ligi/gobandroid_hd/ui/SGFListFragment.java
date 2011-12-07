package org.ligi.gobandroid_hd.ui;

import java.io.File;
import org.ligi.android.common.files.FileHelper;
import org.ligi.gobandroid_hd.R;
import org.ligi.tracedroid.logging.Log;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SGFListFragment extends ListFragment {

	private String[] menu_items;
    private String dir;
	
    public SGFListFragment() {
	}
	
	public SGFListFragment(String[] menu_items,File dir) {
		this.menu_items=menu_items;
		this.dir=dir.getAbsolutePath();
	}
	    	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		if(menu_items==null) 
			menu_items=savedInstanceState.getStringArray("menu_items");
		
		if(dir==null) 
			dir=savedInstanceState.getString("dir");
		
        this.setListAdapter(new PathViewAdapter(this.getActivity(),
        		R.layout.list_item, menu_items,dir));
        
        this.getListView().setCacheColorHint(0);
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
		Log.i("onlistitemclick p");
		super.onListItemClick(l, v, position, id);
    	
        Log.i("onlistitemclick a");
        
        Intent intent2start=new Intent(this.getActivity(),SGFLoadActivity.class);
        String fname=dir + "/" + menu_items[position];
        
        
        if (fname.endsWith(".golink")) {
        	fname=FileHelper.file2String(new File(fname));
        }
        
        if (fname.contains(":#")) {
        	String[] arr_content=fname.split(":#");
        	int move_id=Integer.parseInt(arr_content[1]);
        	fname=arr_content[0];
        	intent2start.putExtra("move_num",move_id);
        }
        
        if (!fname.endsWith(".sgf")) {
        	intent2start=new Intent(this.getActivity(),SGFSDCardListActivity.class);
        }
        
        if (!fname.contains("://"))
        	fname="file://"+fname;
                
        intent2start.setData(Uri.parse( fname));
        
        startActivity(intent2start);
        
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putStringArray("menu_items", menu_items);
		outState.putString("dir",dir);
	 }

	
	class PathViewAdapter extends BaseAdapter {
		
		private Activity activity;
		private int listItem;
		private String[] menu_items;
		private String path;
		
		public PathViewAdapter(Activity activity, int listItem,
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
			String base_fname=dir+"/"+menu_items[position];
			
			
			View v;
			
			if (new File(base_fname).isDirectory()) {
				v= inflater.inflate(R.layout.sgf_dir_list_item,null);
				
				LinearLayout container = (LinearLayout)v.findViewById(R.id.thumb_container);
				container.setOrientation(LinearLayout.HORIZONTAL);
				
				int fcount=0;
				
				for ( File act_file:new File(base_fname).listFiles())
					if (act_file.getName().endsWith(".png")) {
						fcount++;
						if (fcount>3)
							break;
						ImageView img=new ImageView(activity);
						img.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
						//img.setBackgroundDrawable(d)
						
						Log.i("processing -- " + act_file.getPath());


						BitmapFactory.Options options=new BitmapFactory.Options();
						options.inSampleSize = 2;
						options.inPurgeable=true;
						Bitmap img_bmp=BitmapFactory.decodeFile(act_file.getPath(),options);
						//img_bmp=Bitmap.createScaledBitmap(img_bmp, 200, 200, true);
						//todo cache this resizing - creates a very laggy experience
						if (img_bmp!=null) {img.setImageBitmap(img_bmp);}
						img.setScaleType(ScaleType.FIT_XY);
						container.addView(img);
					}
				
			}
			else
				v= inflater.inflate(R.layout.sgf_tsumego_list_item,null);
				
			
			String img_fname=dir+"/"+menu_items[position]+".png";

			TextView title_tv=(TextView)v.findViewById(R.id.filename);
			
			if (title_tv!=null) {
				title_tv.setText(menu_items[position].replace(".sgf", ""));
			}
			
			if (new File(img_fname).exists()) {
				ImageView img=(ImageView)v.findViewById(R.id.thumbnail);

				if (img!=null) {
					Bitmap img_bmp=BitmapFactory.decodeFile(img_fname);
					img_bmp=Bitmap.createScaledBitmap(img_bmp, 200, 200, true);
					//todo cache this resizing - creates a very laggy experience
					
					if (img_bmp!=null) {img.setImageBitmap(img_bmp);}
					Log.i("setting image file://"+img_fname );
					img.invalidate();
				}
			}
			
			
			ImageView solve_img=(ImageView)v.findViewById(R.id.solve_status_image);
			if ((solve_img!=null)&&(activity.getBaseContext().getSharedPreferences("tsumego_stats", Activity.MODE_PRIVATE).getInt("file://"+base_fname, -1)>0)) {
				solve_img.setImageResource(R.drawable.solved);
			}
			/*
			LinearLayout lin=new LinearLayout(activity);
			lin.setOrientation(LinearLayout.HORIZONTAL);
			TextView tv=new TextView(activity);
			tv.setText("!"+menu_items[position]);
			lin.addView(tv);
			
			
			String base_fname=dir+"/"+menu_items[position];
			String img_fname=dir+"/"+menu_items[position]+".png";
			Log.i("checking fname " + img_fname);
			
			
			if (activity.getBaseContext().getSharedPreferences("tsumego_stats", Activity.MODE_PRIVATE).getInt("file://"+base_fname, -1)>0) {
				TextView tvso=new TextView(activity);
				tvso.setText("solved!");
				lin.addView(tvso);
				
			}
    		
			
			if (new File(img_fname).exists()) {
				ImageView img=new ImageView(activity);
				
				Bitmap img_bmp=BitmapFactory.decodeFile(img_fname);
				img_bmp=Bitmap.createScaledBitmap(img_bmp, 200, 200, true);
				// todo replace 200 with dimen
				
				if (img_bmp!=null) {
					img.setImageBitmap(img_bmp);
					lin.addView(img);
				}
			}
			
			
			*/
			return v;
		}
		
		
		
		
	}
}