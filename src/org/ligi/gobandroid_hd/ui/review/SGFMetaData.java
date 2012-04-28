package org.ligi.gobandroid_hd.ui.review;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.android.common.files.FileHelper;
import org.ligi.gobandroid_hd.GobandroidApp;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.tracedroid.logging.Log;

public class SGFMetaData {

	public Integer rating=null;
	private String meta_fname=null;
	private boolean has_data=false;
	
	public final static String FNAME_ENDING=".sgfmeta";
	
	public SGFMetaData(String fname) {
		meta_fname=fname;

		try {
			JSONObject jObject=new JSONObject(FileHelper.file2String(new File(meta_fname)));
			rating = (Integer)jObject.getInt("rating");
			has_data=true;
		} catch ( Exception e) {
			
		}
	}
	
	public SGFMetaData(GobandroidApp app) {
		this(app.getGame().getMetaData().getFileName()+FNAME_ENDING);
	}
	
	public void setRating(Integer rating) {
		this.rating=rating;
	}
	
	public Integer getRating() {
		return rating;
	}
	public boolean hasData() {
		return has_data;
	}

	public void persist() {
		try {
			JSONObject object = new JSONObject();
			try {
				if (rating!=null)
					object.put("rating", rating);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			String json_str=object.toString();
			
			FileWriter sgf_writer = new FileWriter(new File(meta_fname));
			
			BufferedWriter out = new BufferedWriter(sgf_writer);
			
			out.write(json_str);
			out.close();
			sgf_writer.close();
		} catch (Exception e ) {
			Log.w("problem writing metadata"+e);
		}

	}
}
