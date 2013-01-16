package org.ligi.gobandroid_hd.ui.gnugo;

import android.content.*;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import android.widget.Toast;
import com.actionbarsherlock.view.Menu;
import com.google.analytics.tracking.android.EasyTracker;
import org.ligi.gobandroid_beta.R;
import org.ligi.gobandroid_hd.logic.GTPHelper;
import org.ligi.gobandroid_hd.logic.GoBoard;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.GoPrefs;
import org.ligi.gobandroid_hd.ui.recording.RecordingGameExtrasFragment;
import org.ligi.gobandroidhd.ai.gnugo.IGnuGoService;
import org.ligi.tracedroid.logging.Log;

/**
 * the central Application-Context
 *
 * @author ligi
 */
public class PlayAgainstGnugoActivity extends GoActivity implements GoGameChangeListener, Runnable {

    private IGnuGoService gnu_service;
    private ServiceConnection conn;

    private boolean playing_black = false;
    private boolean playing_white = false;
    private byte level;

    private GnuGoSetupDialog dlg;

    private boolean gnugo_size_set = false;

    public final static String INTENT_ACTION = "org.ligi.gobandroidhd.ai.gnugo.GnuGoService";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO the next line works but needs investigation - i thought more of
        // getBoard().requestFocus(); - but that was not working ..
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        EasyTracker.getTracker().trackEvent("ui_action", "gnugo", "play", null);

        dlg = new GnuGoSetupDialog(this);

        dlg.setPositiveButton(R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                playing_black = dlg.isBlackActive() | dlg.isBothActive();
                playing_white = dlg.isWhiteActive() | dlg.isBothActive();

                if (dlg.isBlackActive()) {
                    getGame().getMetaData().setBlackName(getString(R.string.gnugo));
                    getGame().getMetaData().setBlackRank("");
                } else {
                    getGame().getMetaData().setBlackName(getApp().getSettings().getUsername());
                    getGame().getMetaData().setBlackRank(getApp().getSettings().getRank());
                }

                if (dlg.isWhiteActive()) {
                    getGame().getMetaData().setWhiteName(getString(R.string.gnugo));
                    getGame().getMetaData().setWhiteRank("");
                } else {
                    getGame().getMetaData().setWhiteName(getApp().getSettings().getUsername());
                    getGame().getMetaData().setWhiteRank(getApp().getSettings().getRank());
                }

                level = (byte) dlg.getStrength();
                dlg.saveRecentAsDefault();
                dialog.dismiss();
            }

        });

        dlg.setNegativeButton(R.string.cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }

        });
        dlg.show();

    }

    @Override
    protected void onResume() {
        Log.i("GnuGoDebug onResume");
        conn = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                gnu_service = IGnuGoService.Stub.asInterface(service);

                try {
                    Log.i("Service bound " + gnu_service.processGTP("test"));
                } catch (RemoteException e) {
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i("Service unbound ");
            }
        };

        // gnugo_size_set=false;
        getApplication().bindService(new Intent(INTENT_ACTION), conn, Context.BIND_AUTO_CREATE);

        new Thread(this).start();

        super.onStart();
    }

    @Override
    public void onPause() {
        stop();
        Log.i("GnuGoDebug onPause");
        super.onPause();
    }

    public void stop() {
        if (gnu_service == null)
            return;
        gnu_service = null;
        Log.i("GnuGoDebug stopping");
        try {
            getApplication().unbindService(conn);
            getApplication().stopService(new Intent(INTENT_ACTION));
        } catch (Exception e) {
        }
        conn = null;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_game_pass).setVisible(!getGame().isFinished());
        // menu.findItem(R.id.menu_game_results).setVisible(getGame().isFinished());
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getSupportMenuInflater().inflate(R.menu.ingame_record, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onGoGameChange() {
        super.onGoGameChange();

        if (getGame().isFinished()) {
            switchToCounting();
        }
    }

    public Fragment getGameExtraFragment() {
        return new RecordingGameExtrasFragment();
    }

    @Override
    public byte doMoveWithUIFeedback(byte x, byte y) {
        if ((getGame().isBlackToMove() && (!playing_black)))
            processMove("black", x, y);
        else if (((!getGame().isBlackToMove()) && (!playing_white)))
            processMove("white", x, y);

        return super.doMoveWithUIFeedback(x, y);
    }

    public void processMove(String color, byte x, byte y) {
        try {
            gnu_service.processGTP(color + " " + coordinates2gtpstr(x, y));
        } catch (Exception e) {
            Log.w("problem processing " + color + " move to " + coordinates2gtpstr(x, y));
        }
    }

    @Override
    public void run() {
        Log.i("GnuGoDebug startthread " + conn);
        while (conn != null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // blocker for the following steps
            if ((gnu_service == null) || getGame().isFinished() || (conn == null))
                continue;

            if (gnugo_size_set && !checkGnuGoSync()) { // check if gobandroid
                // and gnugo see the
                // same board -
                // otherwise tell gnugo
                // about the truth
                // afterwards ;-)
                try {
                    Log.i("gnugo sync check problem" + gnu_service.processGTP("showboard") + getGame().getVisualBoard().toString());
                    gnugo_size_set = false;
                } catch (RemoteException e) {
                }
            }

            if (!gnugo_size_set)
                try {
                    // set the size
                    gnu_service.processGTP("boardsize " + getGame().getBoardSize());

                    for (byte x = 0; x < getGame().getBoardSize(); x++)
                        for (byte y = 0; y < getGame().getBoardSize(); y++)
                            if (getGame().getVisualBoard().isCellBlack(x, y))
                                gnu_service.processGTP("black " + coordinates2gtpstr(x, y));
                            else if (getGame().getVisualBoard().isCellWhite(x, y))
                                gnu_service.processGTP("white " + coordinates2gtpstr(x, y));

                    Log.i("setting level " + gnu_service.processGTP("level " + level));
                    gnugo_size_set = true;
                } catch (Exception e) {
                }

            if (getGame().isBlackToMove() && playing_black) {
                try {
                    String answer = gnu_service.processGTP("genmove black");

                    if (!GTPHelper.doMoveByGTPString(answer, getGame())) {
                        Log.w("GnuGoProblem " + answer + " board " + gnu_service.processGTP("showboard"));
                        Log.w("restarting GnuGo " + answer);
                        gnugo_size_set = false; // reset
                    }
                    Log.i("gugoservice" + gnu_service.processGTP("showboard"));
                } catch (Exception e) {
                }
            }

            if ((!getGame().isBlackToMove()) && playing_white) {
                try {
                    String answer = gnu_service.processGTP("genmove white");

                    Log.i("gugoservice" + gnu_service.processGTP("showboard"));

                    if (!GTPHelper.doMoveByGTPString(answer, getGame())) {
                        Log.w("GnuGoProblem " + answer + " board " + gnu_service.processGTP("showboard"));
                        Log.w("restarting GnuGo " + answer);
                        gnugo_size_set = false; // reset
                    }
                    Log.i("gugoservice" + gnu_service.processGTP("showboard"));
                } catch (Exception e) {
                }
            }


        }
        stop();

        Log.i("a stopthread  " + conn);
    }

    public boolean checkGnuGoSync() {
        try {
            String board_str = gnu_service.processGTP("showboard");
            GoBoard b = new GoBoard((byte) getGame().getBoardSize());
            String[] split_board = board_str.split("\n");

            for (int gnugo_y = 2; gnugo_y <= b.getSize() + 1; gnugo_y++) {
                String act_line = split_board[gnugo_y].replace(" ", "").replace("" + (getGame().getBoardSize() - (gnugo_y - 2)), "");
                for (int gnugo_x = 0; gnugo_x < b.getSize(); gnugo_x++) {
                    if (act_line.charAt(gnugo_x) == '.')
                        if (!getGame().getVisualBoard().isCellFree(gnugo_x, gnugo_y - 2))
                            return false;

                    if (act_line.charAt(gnugo_x) == 'X')
                        if (!getGame().getVisualBoard().isCellBlack(gnugo_x, gnugo_y - 2))
                            return false;

                    if (act_line.charAt(gnugo_x) == 'O')
                        if (!getGame().getVisualBoard().isCellWhite(gnugo_x, gnugo_y - 2))
                            return false;
                    // Log.i("checking " +act_line.charAt(gnugo_x));
                }

            }

        } catch (Exception e) {
            Log.w("exception in check of gnugo sync " + e);
            return false;
        }
        return true;
    }

    private String coordinates2gtpstr(byte x, byte y) {
        if (getGame() == null) {
            Log.w("coordinates2gtpstr called with game==null");
            return "";
        }
        if (x >= 8)
            x++; // "I" is missing decrease human OCR-error but increase
        // computer bugs ...
        y = (byte) (getGame().getBoardSize() - (y));
        return "" + (char) ('A' + x) + "" + (y);
    }

    /**
     * @return if it is a move the mover has to process
     */
    public boolean isMoversMove() {
        return (getGame().isBlackToMove() && (playing_black)) || (!getGame().isBlackToMove() && (playing_white));
    }

    @Override
    public void requestUndo() {
        if (isMoversMove()) {
            Toast.makeText(this, "Please wait for GnuGo", Toast.LENGTH_LONG).show();
            return;
        }

        if (getGame().canUndo()) {
            getGame().undo(GoPrefs.isKeepVariantEnabled());
        }

        if (getGame().canUndo()) {
            getGame().undo(GoPrefs.isKeepVariantEnabled());
        }

        try {
            Log.i("gugoservice undo 1" + gnu_service.processGTP("gg-undo 2"));
        } catch (Exception e) {
            //
        }

    }

    public boolean doAutosave() {
        return true;
    }
}
