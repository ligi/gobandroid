package org.ligi.gobandroid_hd.logic.sgf;

import org.ligi.gobandroid_hd.logic.Cell;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoMove;
import org.ligi.gobandroid_hd.logic.markers.CircleMarker;
import org.ligi.gobandroid_hd.logic.markers.GoMarker;
import org.ligi.gobandroid_hd.logic.markers.SquareMarker;
import org.ligi.gobandroid_hd.logic.markers.TextMarker;
import org.ligi.gobandroid_hd.logic.markers.TriangleMarker;
import org.ligi.tracedroid.logging.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * logic to save SGF files ( serialize )
 */
public class SGFWriter {

    public static String escapeSGF(String txt) {
        txt.replace("]", "\\]");
        txt.replace(")", "\\)");
        txt.replace("\\", "\\\\");
        return txt;
    }

    private static String getSGFSnippet(String cmd, String param) {
        if ((param == null) || (param.equals("")) || (cmd == null) || (cmd.equals(""))) {
            return "";
        }
        return cmd + "[" + escapeSGF(param) + "]";
    }

    public static String game2sgf(GoGame game) {
        String res = "(;FF[4]GM[1]AP[gobandroid:0]"; // header
        res += getSGFSnippet("SZ", "" + game.getBoardSize()); // board_size;
        res += getSGFSnippet("GN", escapeSGF(game.getMetaData().getName()));
        res += getSGFSnippet("DT", escapeSGF(game.getMetaData().getDate()));
        res += getSGFSnippet("PB", escapeSGF(game.getMetaData().getBlackName()));
        res += getSGFSnippet("PW", escapeSGF(game.getMetaData().getWhiteName()));
        res += getSGFSnippet("BR", escapeSGF(game.getMetaData().getBlackRank()));
        res += getSGFSnippet("WR", escapeSGF(game.getMetaData().getWhiteRank()));
        res += getSGFSnippet("KM", escapeSGF(Float.toString(game.getKomi())));
        res += getSGFSnippet("RE", escapeSGF(game.getMetaData().getResult()));
        res += getSGFSnippet("SO", escapeSGF(game.getMetaData().getSource()));
        res += "\n";

        for (Cell cell : game.getCalcBoard().getAllCells()) {
            if (game.getHandicapBoard().isCellWhite(cell)) {
                res += "AW" + SGFWriter.coords2SGFFragment(cell) + "\n";
            } else if (game.getHandicapBoard().isCellBlack(cell)) {
                res += "AB" + SGFWriter.coords2SGFFragment(cell) + "\n";
            }
        }

        res += SGFWriter.moves2string(game.getFirstMove()) + ")";

        return res;
    }

    /**
     * convert tree of moves to a string to use in SGF next moves are processed
     * recursive
     *
     * @param move - the start move
     * @return
     */

    static String moves2string(GoMove move) {
        String res = "";

        GoMove act_move = move;

        while (act_move != null) {

            // add the move
            if (!act_move.isFirstMove()) {
                res += ";" + (act_move.isBlackToMove() ? "B" : "W");
                if (act_move.isPassMove())
                    res += "[]";
                else
                    res += coords2SGFFragment(act_move.getCell()) + "\n";
            }

            // add the comment
            if (!act_move.getComment().isEmpty())
                res += "C[" + act_move.getComment() + "]\n";

            // add markers
            for (GoMarker marker : act_move.getMarkers()) {
                if (marker instanceof SquareMarker) {
                    res += "SQ" + coords2SGFFragment(marker);
                } else if (marker instanceof TriangleMarker) {
                    res += "TR" + coords2SGFFragment(marker);
                } else if (marker instanceof CircleMarker) {
                    res += "CR" + coords2SGFFragment(marker);
                } else if (marker instanceof TextMarker) {
                    res += "LB" + coords2SGFFragment(marker).replace("]", ":" + ((TextMarker) marker).getText() + "]");
                }
            }

            GoMove next_move = null;

            if (act_move.hasNextMove()) {
                if (act_move.hasNextMoveVariations()) {
                    for (GoMove var : act_move.getNextMoveVariations()) {
                        res += "(" + moves2string(var) + ")";
                    }
                } else {
                    next_move = act_move.getnextMove(0);
                }
            }

            act_move = next_move;
        }
        return res;
    }

    static String coords2SGFFragment(Cell cell) {
        return "[" + (char) ('a' + cell.x) + (char) ('a' + cell.y) + "]";
    }

    public static boolean saveSGF(GoGame game, String fname) {

        File f = new File(fname);

        if (f.isDirectory()) {
            throw new IllegalArgumentException("cannot write - fname is a directory");
        }

        if (f.getParentFile() == null) { // not really sure when this can be the
            // case ( perhaps only / ) - but the doc says it can be null and I would get NPE then
            throw new IllegalArgumentException("bad filename " + fname);
        }

        if (!f.getParentFile().isDirectory()) { // if  the  path is not there yet
            f.getParentFile().mkdirs();
        }

        try {
            f.createNewFile();

            FileWriter sgf_writer = new FileWriter(f);

            BufferedWriter out = new BufferedWriter(sgf_writer);

            out.write(game2sgf(game));
            out.close();
            sgf_writer.close();

        } catch (IOException e) {
            Log.i("" + e);
            return false;
        }

        game.getMetaData().setFileName(fname);
        return true;
    }
}
