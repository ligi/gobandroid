package org.ligi.gobandroid_hd.ui.sgf_listing;

import org.ligi.axt.AXT;
import org.ligi.gobandroid_hd.FileEncodeDetector;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.tracedroid.logging.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GoLink {

    public static boolean isGoLink(String fname) {
        return fname.endsWith(".golink");
    }

    public static boolean isGoLink(File file) {
        return file.getName().endsWith(".golink");
    }


    private String fname = "";
    private int move_pos = 0;

    public GoLink(String fname) {
        this(new File(fname.replace("file://", "")));
    }

    public GoLink(File file) {

        try {
            String go_lnk = AXT.at(file).readToString();
            go_lnk = go_lnk.replace("\n", "").replace("\r", "");
            fname = go_lnk; // backup
            String[] arr_content = go_lnk.split(":#");
            fname = arr_content[0];
            fname = fname.replace("file://", "");
            move_pos = Integer.parseInt(arr_content[1]);
        } catch (Exception e) {
        }
    }

    /**
     * returns the move Depth/pos
     * should only be used for displaying
     *
     * @return
     */
    public int getMoveDepth() {
        return move_pos;
    }


    public boolean linksToDirectory() {
        return new File(fname).isDirectory();
    }

    /**
     * TODO care for remote content
     *
     * @return
     */
    public String getSGFString() {
        try {
            return AXT.at(new File(fname)).readToString(FileEncodeDetector.detect(fname));
        } catch (IOException e) {
            return "";
        }
    }

    public String getFileName() {
        return fname;
    }

    public static void saveGameToGoLink(GoGame game, String golink_path, String golink_fname) {

        int move_pos = game.getActMove().getMovePos();

        File f = new File(golink_path);

        if (!f.isDirectory())
            f.mkdirs();

        try {
            f.createNewFile();
            f = new File(golink_path + "/" + golink_fname);

            FileWriter sgf_writer = new FileWriter(f);

            BufferedWriter out = new BufferedWriter(sgf_writer);

            out.write(game.getMetaData().getFileName() + ":#" + move_pos);
            out.close();
            sgf_writer.close();
        } catch (IOException e) {
            Log.i("" + e);
        }
    }
}
