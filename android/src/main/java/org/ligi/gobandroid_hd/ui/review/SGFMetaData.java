package org.ligi.gobandroid_hd.ui.review;

import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.axt.AXT;
import org.ligi.gobandroid_hd.App;
import org.ligi.tracedroid.logging.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * stores and gives access to metatata to an SGF file
 *
 * @author ligi
 */
public class SGFMetaData {

    public Integer rating = null;
    public boolean is_solved = false;
    private String meta_fname = null;
    private Integer hints_used = 0;

    private boolean has_data = false;

    public final static String FNAME_ENDING = ".sgfmeta";

    public SGFMetaData(String fname) {

        if (fname.startsWith("file://"))
            fname = fname.substring(8);

        if (!fname.endsWith(FNAME_ENDING))
            fname += FNAME_ENDING;

        meta_fname = fname;

        try {
            Log.i("got json file " + AXT.at(new File(meta_fname)).loadToString());
            JSONObject jObject = new JSONObject(AXT.at(new File(meta_fname)).loadToString());

            try {
                rating = (Integer) jObject.getInt("rating");
            } catch (org.json.JSONException jse) {
            } // don't care if not there

            try {
                is_solved = (Boolean) jObject.getBoolean("is_solved");
            } catch (org.json.JSONException jse) {
            } // don't care if not there

            try {
                hints_used = (Integer) jObject.getInt("hints_used");
            } catch (org.json.JSONException jse) {
            } // don't care if not there

            has_data = true;
        } catch (Exception e) {
            Log.i("got json file " + e);
        }
    }

    public SGFMetaData(App app) {
        this(app.getGame().getMetaData().getFileName() + FNAME_ENDING);
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getRating() {
        return rating;
    }

    public boolean getIsSolved() {
        return is_solved;
    }

    public boolean hasData() {
        return has_data;
    }

    public void persist() {
        try {
            JSONObject object = new JSONObject();
            try {
                if (rating != null)
                    object.put("rating", rating);
                object.put("is_solved", is_solved);
                object.put("hints_used", hints_used);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String json_str = object.toString();

            FileWriter sgf_writer = new FileWriter(new File(meta_fname));

            BufferedWriter out = new BufferedWriter(sgf_writer);

            out.write(json_str);
            out.close();
            sgf_writer.close();
        } catch (Exception e) {
            Log.w("problem writing metadata" + e);
        }

    }

    public void setIsSolved(boolean b) {
        is_solved = b;
    }

    public void setHintsUsed(int hints_used) {
        this.hints_used = hints_used;
    }

    public void incHintsUsed() {
        hints_used++;
    }

    public int getHintsUsed() {
        return hints_used;
    }
}
