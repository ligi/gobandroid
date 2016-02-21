package org.ligi.gobandroid_hd.logic.sgf;

import android.support.annotation.NonNull;

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
        txt = txt.replace("]", "\\]");
        txt = txt.replace(")", "\\)");
        txt = txt.replace("\\", "\\\\");
        return txt;
    }

    private static String getSGFSnippet(@NonNull String cmd, @NonNull String param) {
        if ((param.isEmpty()) || (cmd.isEmpty())) {
            return "";
        }
        return cmd + "[" + escapeSGF(param) + "]";
    }

    public static String game2sgf(GoGame game) {
        final StringBuilder res = new StringBuilder("(;FF[4]GM[1]AP[gobandroid:0]"); // header
        res.append(getSGFSnippet("SZ", "" + game.getBoardSize())); // board_size;
        res.append(getSGFSnippet("GN", escapeSGF(game.getMetaData().getName())));
        res.append(getSGFSnippet("DT", escapeSGF(game.getMetaData().getDate())));
        res.append(getSGFSnippet("PB", escapeSGF(game.getMetaData().getBlackName())));
        res.append(getSGFSnippet("PW", escapeSGF(game.getMetaData().getWhiteName())));
        res.append(getSGFSnippet("BR", escapeSGF(game.getMetaData().getBlackRank())));
        res.append(getSGFSnippet("WR", escapeSGF(game.getMetaData().getWhiteRank())));
        res.append(getSGFSnippet("KM", escapeSGF(Float.toString(game.getKomi()))));
        res.append(getSGFSnippet("RE", escapeSGF(game.getMetaData().getResult())));
        res.append(getSGFSnippet("SO", escapeSGF(game.getMetaData().getSource())));
        res.append("\n");

        for (Cell cell : game.getCalcBoard().getStatelessGoBoard().getAllCells()) {
            if (game.getHandicapBoard().isCellWhite(cell)) {
                res.append("AW").append(SGFWriter.coords2SGFFragment(cell)).append("\n");
            } else if (game.getHandicapBoard().isCellBlack(cell)) {
                res.append("AB").append(SGFWriter.coords2SGFFragment(cell)).append("\n");
            }
        }

        res.append(SGFWriter.moves2string(game.getFirstMove())).append(")");

        return res.toString();
    }

    /**
     * convert tree of moves to a string to use in SGF next moves are processed
     * recursive
     *
     * @param move - the start move
     * @return
     */

    static String moves2string(GoMove move) {
        StringBuilder res = new StringBuilder();

        GoMove act_move = move;

        while (act_move != null) {

            // add the move
            if (!act_move.isFirstMove()) {
                res.append(";").append(act_move.isBlackToMove() ? "B" : "W");
                if (act_move.isPassMove()) {
                    res.append("[]");
                } else {
                    res.append(coords2SGFFragment(act_move.getCell())).append("\n");
                }
            }

            // add the comment
            if (!act_move.getComment().isEmpty()) {
                res.append("C[").append(act_move.getComment()).append("]\n");
            }

            // add markers
            for (GoMarker marker : act_move.getMarkers()) {
                if (marker instanceof SquareMarker) {
                    res.append("SQ").append(coords2SGFFragment(marker));
                } else if (marker instanceof TriangleMarker) {
                    res.append("TR").append(coords2SGFFragment(marker));
                } else if (marker instanceof CircleMarker) {
                    res.append("CR").append(coords2SGFFragment(marker));
                } else if (marker instanceof TextMarker) {
                    res.append("LB");
                    res.append(coords2SGFFragment(marker).replace("]", ":" + ((TextMarker) marker).getText() + "]"));
                }
            }

            GoMove next_move = null;

            if (act_move.hasNextMove()) {
                if (act_move.hasNextMoveVariations()) {
                    for (GoMove var : act_move.getNextMoveVariations()) {
                        res.append("(").append(moves2string(var)).append(")");
                    }
                } else {
                    next_move = act_move.getnextMove(0);
                }
            }

            act_move = next_move;
        }
        return res.toString();
    }

    private static String coords2SGFFragment(Cell cell) {
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
