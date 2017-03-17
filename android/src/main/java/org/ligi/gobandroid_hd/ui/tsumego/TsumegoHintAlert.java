package org.ligi.gobandroid_hd.ui.tsumego;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import org.greenrobot.eventbus.EventBus;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.events.GameChangedEvent;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoMove;
import org.ligi.gobandroid_hd.logic.markers.TextMarker;
import org.ligi.gobandroid_hd.ui.application.GobandroidFragmentActivity;
import org.ligi.gobandroid_hd.ui.review.SGFMetaData;

public class TsumegoHintAlert {

    public static void setHintMeta(GoGame game) {
        SGFMetaData meta = new SGFMetaData(game.getMetaData().getFileName());
        meta.incHintsUsed();
        meta.persist();
    }

    public static void show(final GobandroidFragmentActivity activity, final GoMove finishing_move) {

        new AlertDialog.Builder(activity).setTitle(R.string.hint).setItems(R.array.hint_options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                setHintMeta(activity.getGame());
                switch (item) {
                    case 0:
                        mark_path(finishing_move, false, activity.getGame());
                        break;

                    case 1:
                        mark_path(finishing_move, true, activity.getGame());
                        break;

                    case 2:
                        show_numbered_solution(finishing_move, activity.getGame());
                        break;
                }
            }

        })

                .show();
    }

    private static void show_numbered_solution(GoMove finishing_move, GoGame game) {
        GoMove myActMove = finishing_move;
        int p = myActMove.getMovePos();
        while (!myActMove.isFirstMove()) {
            if(myActMove.getCell() != null) {
                finishing_move.addMarker(new TextMarker(myActMove.getCell(), Integer.toString(p)));
            }

            p--;
            myActMove = myActMove.getParent();
        }

        game.jump(finishing_move);
    }

    private static void mark_path(GoMove finishing_move, boolean complete, GoGame game) {
        GoMove myActMove = finishing_move;
        while (!myActMove.isFirstMove()) {
            if (complete || (myActMove.getCell() != null && myActMove.getParent() == game.getActMove())) {
                myActMove.getParent().addMarker(new TextMarker(myActMove.getCell(), "X"));
            }

            myActMove = myActMove.getParent();
        }

        game.jump(game.getActMove());
        EventBus.getDefault().post(GameChangedEvent.INSTANCE);
    }

}
