package org.ligi.gobandroid_hd.ui.review;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.InteractionScope;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.Cell;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoMove;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.GobanDroidTVActivity;
import org.ligi.gobandroid_hd.ui.alerts.GameForwardAlert;
import org.ligi.gobandroid_hd.ui.fragments.CommentAndNowPlayingFragment;
import org.ligi.gobandroid_hd.ui.ingame_common.SwitchModeHelper;
import org.ligi.tracedroid.logging.Log;

public class GoGamePlayerActivity extends GoActivity {

    private boolean autoplay_active = true;

    // timings in ms
    private final static int PAUSE_FOR_LAST_MOVE = 30000;
    private final static int PAUSE_BETWEEN_MOVES = 2300;
    private final static int PAUSE_BETWEN_MOVES_EXTRA_PER_WORD = 200;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getBoard().setOnKeyListener(this);
        getBoard().do_actpos_highlight = false;

    }

    @Override
    protected void onStart() {
        if (autoplay_active) {
            new Thread(new autoPlayRunnable()).start();
        }
        super.onStart();
    }

    private Runnable mTimerProgressRunnable = new Runnable() {

        @Override
        public void run() {
            //setSupportProgress((int) (Window.PROGRESS_START + progress_to_display * (Window.PROGRESS_END - Window.PROGRESS_START)));
        }

    };

    float progress_to_display = 0.5f;

    /**
     * time in ms
     *
     * @param time
     */
    private void sleepWithProgress(int time) {
        try {
            long start_time = System.currentTimeMillis();

            while (System.currentTimeMillis() < start_time + time) {
                Thread.sleep(100);
                progress_to_display = 1f - ((float) (System.currentTimeMillis() - start_time + 1) / time);
                runOnUiThread(mTimerProgressRunnable);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class autoPlayRunnable implements Runnable {

        // private GoGame game;

        @Override
        public void run() {
            // game=;
            Log.i("gobandroid", "automove start" + getGame().getActMove().getNextMoveVariations().size());
            while (autoplay_active && (getGame().getActMove().hasNextMove())) {
                Log.i("gobandroid", "automove move" + getGame().getActMove().hasNextMove());

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        GoMove next_mve = getGame().getActMove().getnextMove(0);
                        getGame().jump(next_mve);
                    }
                });
                Log.i("gobandroid", "automove move" + getGame().getActMove().hasNextMove());
                sleepWithProgress(calcTime());

            }
            Log.i("gobandroid", "automove finish " + autoplay_active);
            try {
                Thread.sleep(PAUSE_FOR_LAST_MOVE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i("gobandroid", "automove asleep");

            if (!App.getInteractionScope().is_in_noif_mode()) {
                Intent next_intent = new Intent(GoGamePlayerActivity.this, GobanDroidTVActivity.class);

                if (autoplay_active) {
                    GoGamePlayerActivity.this.startActivity(next_intent);
                    GoGamePlayerActivity.this.finish();
                }
            } else {
                GoGamePlayerActivity.this.setResult(RESULT_OK);
                GoGamePlayerActivity.this.finish();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.ingame_review, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStop() {
        autoplay_active = false;
        super.onStop();
    }

    public Fragment getGameExtraFragment() {
        return new CommentAndNowPlayingFragment();
    }

    @Override
    public byte doMoveWithUIFeedback(Cell cell) {
        // we want the user not to be able to edit in review mode
        return GoGame.MOVE_VALID;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN)
            switch (keyCode) {
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    SwitchModeHelper.startGame(this, InteractionScope.MODE_REVIEW);
                    return true;

                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (getGame().canUndo()) {
                        getGame().undo();
                    }

                    return true;

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    GameForwardAlert.showIfNeeded(this, getGame());
                    return true;

                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    return false;

            }
        return super.onKey(v, keyCode, event);
    }

    @Override
    public boolean isAsk4QuitEnabled() {
        return false;
    }

    public int countWords(String sentence) {
        int words = 0;
        if (!App.getInteractionScope().is_in_noif_mode())
            for (int i = 0; i < sentence.length(); i++)
                if (sentence.charAt(i) == ' ')
                    words++;

        return words;
    }

    private int calcTime() {
        int res = PAUSE_BETWEEN_MOVES;
        if (getGame().getActMove().hasComment())
            res += PAUSE_BETWEN_MOVES_EXTRA_PER_WORD * countWords(getGame().getActMove().getComment());
        return res;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true; // this is a player - we do not want interaction
    }

}
