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
    private File metaFile = null;
    private Integer hints_used = 0;

    private boolean has_data = false;

    public final static String FNAME_ENDING = ".sgfmeta";

    public SGFMetaData(String fileName) {
        this(new File(sanitizeFileName(fileName)));
    }

    public static String sanitizeFileName(String fileName) {
        String result = fileName;

        if (fileName.startsWith("file://")) {
            result = fileName.substring(8);
        }

        if (!fileName.endsWith(FNAME_ENDING)) {
            result += FNAME_ENDING;
        }

        return result;
    }

    private SGFMetaData(File metaFile) {

        this.metaFile = metaFile;

        if (metaFile.exists()) {
            try {

                JSONObject jObject = new JSONObject(AXT.at(metaFile).readToString());

                try {
                    rating = jObject.getInt("rating");
                } catch (org.json.JSONException jse) {
                } // don't care if not there

                try {
                    is_solved = jObject.getBoolean("is_solved");
                } catch (org.json.JSONException jse) {
                } // don't care if not there

                try {
                    hints_used = jObject.getInt("hints_used");
                } catch (org.json.JSONException jse) {
                } // don't care if not there

                has_data = true;
            } catch (Exception e) {
                Log.i("got json file " + e);
            }
        }
    }

    public SGFMetaData() {
        this(App.getGame().getMetaData().getFileName() + FNAME_ENDING);
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
            final JSONObject object = new JSONObject();
            try {
                if (rating != null) {
                    object.put("rating", rating);
                }
                object.put("is_solved", is_solved);
                object.put("hints_used", hints_used);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String json_str = object.toString();
            final FileWriter sgf_writer = new FileWriter(metaFile);
            final BufferedWriter out = new BufferedWriter(sgf_writer);

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
