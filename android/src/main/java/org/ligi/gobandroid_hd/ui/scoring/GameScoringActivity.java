package org.ligi.gobandroid_hd.ui.scoring;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.WindowManager;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.BoardCell;
import org.ligi.gobandroid_hd.logic.FloodFillStackStack;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.tracedroid.logging.Log;

/**
 * Activity to score a Game
 *
 * @author ligi
 */
public class GameScoringActivity extends GoActivity implements
        GoGameChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO the next line works but needs investigation - i thought more of
        // getBoard().requestFocus(); - but that was not working ..
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getBoard().show_area_stones = true;
        getGame().buildAreaGroups();

    }

    @Override
    public void doTouch(MotionEvent event) {
        //super.doTouch(event); - Do not call! Not needed and breaks marking dead stones

        eventForZoomBoard(event);
        App.getInteractionScope().setTouchPosition(getBoard().pixel2boardPos(
                event.getX(), event.getY()));

        // calculate position on the field by position on the touchscreen

        if (event.getAction() == MotionEvent.ACTION_UP) {
            doMoveWithUIFeedback((byte) App.getInteractionScope().getTouchX(),
                    (byte) App.getInteractionScope().getTouchY());
            App.getInteractionScope().setTouchPosition(-1);
        }


    }

    @Override
    public byte doMoveWithUIFeedback(byte x, byte y) {
        do_score_touch(x, y);
        getGame().notifyGameChange();
        return GoGame.MOVE_VALID;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.ingame_score, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onGoGameChange() {
        super.onGoGameChange();
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                supportInvalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        // if we go back to other modes we want to have them alive again ( Zombies ?)
        for (int xg = 0; xg < getGame().getCalcBoard().getSize(); xg++)
            for (int yg = 0; yg < getGame().getCalcBoard().getSize(); yg++)
                if (getGame().getCalcBoard().isCellDead(xg, yg)) {
                    getGame().getCalcBoard().toggleCellDead(xg, yg);
                }
    }

    @Override
    public Fragment getGameExtraFragment() {
        return new GameScoringExtrasFragment();
    }


    public void do_score_touch(byte x, byte y) {
        if ((x < 0) || (x >= getGame().getCalcBoard().getSize()) || (y < 0)
                || (y >= getGame().getCalcBoard().getSize())) {
            Log.w("score touch not on board");
            return; // not on board
        }

        if ((!getGame().getCalcBoard().isCellFree(x, y)) || getGame().getCalcBoard().isCellDead(x, y)) { // if there is a stone/group
            final FloodFillStackStack stack=new FloodFillStackStack(new BoardCell(x,y,getGame().getCalcBoard()));

            while (!stack.isEmpty()) {
                final BoardCell pop = stack.pop();
                getGame().getCalcBoard().toggleCellDead(pop.x,pop.y);
                stack.pushSurroundingWithCheck(pop);
            }
        }

        getGame().buildAreaGroups();
        getGame().calculateDead();
    }

}
