package org.ligi.gobandroid_hd.ui.gnugo;

import android.content.Context;
import android.content.Intent;
import org.ligi.axt.AXT;
import org.ligi.gobandroid_hd.logic.CellImpl;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.StatefulGoBoard;

public class GnuGoHelper {

    public final static String INTENT_ACTION_NAME = "org.ligi.gobandroidhd.ai.gnugo.GnuGoService";

    public static boolean isGnuGoAvail(Context ctx) {
        return (AXT.at(new Intent(INTENT_ACTION_NAME)).isServiceAvailable(ctx.getPackageManager(), 0));
    }


    public static boolean checkGnuGoSync(final String board_str, final GoGame game) {
        final StatefulGoBoard b = new StatefulGoBoard(game.getStatelessGoBoard());
        final String[] split_board = board_str.split("\n");

        for (int gnugo_y = 2; gnugo_y <= b.getSize() + 1; gnugo_y++) {
            final String act_line = split_board[gnugo_y].replace(" ", "").replace("" + (game.getBoardSize() - (gnugo_y - 2)), "");
            for (int gnugo_x = 0; gnugo_x < b.getSize(); gnugo_x++) {
                final CellImpl cell = new CellImpl(gnugo_x, gnugo_y - 2);

                if (act_line.length() < (gnugo_x + 1) ||
                    (act_line.charAt(gnugo_x) == '.' && !game.getVisualBoard().isCellFree(cell)) ||
                    (act_line.charAt(gnugo_x) == 'X' && !game.getVisualBoard().isCellBlack(cell)) ||
                    (act_line.charAt(gnugo_x) == 'O' && !game.getVisualBoard().isCellWhite(cell))) {
                    return false;
                }

            }

        }

        return true;
    }
}
