package org.ligi.gobandroid_hd.ui.gnugo;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.common.base.Stopwatch;

import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.logic.Cell;
import org.ligi.gobandroid_hd.logic.GTPHelper;
import org.ligi.gobandroid_hd.logic.GoBoard;
import org.ligi.gobandroid_hd.logic.GoGame.GoGameChangeListener;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.GoPrefs;
import org.ligi.gobandroid_hd.ui.recording.RecordingGameExtrasFragment;
import org.ligi.gobandroidhd.ai.gnugo.IGnuGoService;
import org.ligi.tracedroid.logging.Log;

import java.util.concurrent.TimeUnit;

/**
 * Activity to play vs GnoGo
 */
public class PlayAgainstGnuGoActivity extends GoActivity implements GoGameChangeListener, Runnable {

    private IGnuGoService service;
    private ServiceConnection connection;

    private boolean playingBlack = false;
    private boolean playingWhite = false;
    private byte level;

    private GnuGoSetupDialog dlg;

    private boolean gnugoSizeSet = false;

    public final static String INTENT_ACTION = "org.ligi.gobandroidhd.ai.gnugo.GnuGoService";

    private Stopwatch stopwatch = Stopwatch.createUnstarted();
    private long avgTimeInMillis = 0;

    public void stopTimeMeasure() {
        final long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        stopwatch.reset();
        avgTimeInMillis = (avgTimeInMillis + elapsed) / 2;
        Log.i("TimeSpent average:" + avgTimeInMillis + " last:" + elapsed);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO the next line works but needs investigation - i thought more of
        // getBoard().requestFocus(); - but that was not working ..
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        App.getTracker().trackEvent("ui_action", "gnugo", "play", null);

        dlg = new GnuGoSetupDialog(this);

        dlg.setPositiveButton(R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                playingBlack = dlg.isBlackActive() | dlg.isBothActive();
                playingWhite = dlg.isWhiteActive() | dlg.isBothActive();

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
        super.onResume();
        Log.i("GnuGoDebug onResume");
        connection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                PlayAgainstGnuGoActivity.this.service = IGnuGoService.Stub.asInterface(service);

                try {
                    Log.i("Service bound " + PlayAgainstGnuGoActivity.this.service.processGTP("test"));
                } catch (RemoteException e) {
                    Log.w("RemoteException when connecting",e);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i("Service unbound ");
            }
        };

        final Intent intent = new Intent(INTENT_ACTION);
        final ResolveInfo resolveInfo = getPackageManager().resolveService(intent, 0);

        final ComponentName name = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);

        intent.setComponent(name);

        getApplication().bindService(intent, connection, Context.BIND_AUTO_CREATE);

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
        if (service == null) {
            return;
        }

        service = null;
        Log.i("GnuGoDebug stopping");
        try {
            getApplication().unbindService(connection);
            getApplication().stopService(new Intent(INTENT_ACTION));
        } catch (Exception e) {
            Log.w("Exception in stop()",e);
        }
        connection = null;

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
        this.getMenuInflater().inflate(R.menu.ingame_record, menu);
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
    public byte doMoveWithUIFeedback(Cell cell) {
        if (stopwatch.isRunning()) {
            Toast.makeText(this, R.string.ai_is_thinking, Toast.LENGTH_LONG).show();
            return 0;
        }

        if ((getGame().isBlackToMove() && (!playingBlack)))
            processMove("black", cell);
        else if (((!getGame().isBlackToMove()) && (!playingWhite)))
            processMove("white", cell);

        return super.doMoveWithUIFeedback(cell);
    }

    public void processMove(final String color, final Cell cell) {
        try {
            service.processGTP(color + " " + coordinates2gtpstr(cell));
        } catch (Exception e) {
            Log.w("problem processing " + color + " move to " + coordinates2gtpstr(cell));
        }
    }

    @Override
    public void run() {
        Log.i("GnuGoDebug startthread " + connection);
        while (connection != null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // blocker for the following steps
            if ((service == null) || getGame().isFinished() || (connection == null))
                continue;

            if (gnugoSizeSet && !checkGnuGoSync()) { // check if gobandroid
                // and gnugo see the same board - otherwise tell gnugo about the truth afterwards ;-)
                try {
                    Log.i("gnugo sync check problem" + service.processGTP("showboard") + getGame().getVisualBoard().toString());
                    gnugoSizeSet = false;
                } catch (RemoteException e) {
                    Log.w("RemoteException when syncing",e);
                }
            }

            if (!gnugoSizeSet)
                try {
                    // set the size
                    service.processGTP("boardsize " + getGame().getBoardSize());

                    for (Cell cell : getGame().getCalcBoard().getAllCells()) {
                        if (getGame().getVisualBoard().isCellBlack(cell))
                            service.processGTP("black " + coordinates2gtpstr(cell));
                        else if (getGame().getVisualBoard().isCellWhite(cell))
                            service.processGTP("white " + coordinates2gtpstr(cell));

                    }

                    Log.i("setting level " + service.processGTP("level " + level));

                    gnugoSizeSet = true;
                } catch (Exception e) {
                    Log.w("RemoteException when configuring",e);
                }

            if (getGame().isBlackToMove() && playingBlack) {
                doMove("black");
            }

            if ((!getGame().isBlackToMove()) && playingWhite) {
                doMove("white");
            }

        }
        stop();

        Log.i("a stopthread  " + connection);
    }

    private void doMove(final String color) {
        stopwatch.start();
        try {
            final String answer = service.processGTP("genmove " + color);

            if (!GTPHelper.doMoveByGTPString(answer, getGame())) {
                Log.w("GnuGoProblem " + answer + " board " + service.processGTP("showboard"));
                Log.w("restarting GnuGo " + answer);
                gnugoSizeSet = false; // reset
            }
            Log.i("gugoservice" + service.processGTP("showboard"));
        } catch (Exception e) {
            Log.w("RemoteException when moving",e);
        }
        stopTimeMeasure();
    }

    public boolean checkGnuGoSync() {
        try {
            String board_str = service.processGTP("showboard");
            GoBoard b = new GoBoard((byte) getGame().getBoardSize());
            String[] split_board = board_str.split("\n");

            for (int gnugo_y = 2; gnugo_y <= b.getSize() + 1; gnugo_y++) {
                String act_line = split_board[gnugo_y].replace(" ", "").replace("" + (getGame().getBoardSize() - (gnugo_y - 2)), "");
                for (int gnugo_x = 0; gnugo_x < b.getSize(); gnugo_x++) {
                    final Cell cell = new Cell(gnugo_x, gnugo_y - 2);
                    if (act_line.charAt(gnugo_x) == '.')
                        if (!getGame().getVisualBoard().isCellFree(cell))
                            return false;

                    if (act_line.charAt(gnugo_x) == 'X')
                        if (!getGame().getVisualBoard().isCellBlack(cell))
                            return false;

                    if (act_line.charAt(gnugo_x) == 'O')
                        if (!getGame().getVisualBoard().isCellWhite(cell))
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

    private String coordinates2gtpstr(Cell cell) {
        if (getGame() == null) {
            Log.w("coordinates2gtpstr called with game==null");
            return "";
        }
        // "I" is missing decrease human OCR-error but increase computer bugs ...
        final int x_offset = (cell.x >= 8) ? 1 : 0;
        return "" + (char) ('A' + cell.x + x_offset) + "" + (getGame().getBoardSize() - cell.y);
    }

    @Override
    public void requestUndo() {

        if (getGame().canUndo()) {
            getGame().undo(GoPrefs.isKeepVariantEnabled());
        }

        if (getGame().canUndo()) {
            getGame().undo(GoPrefs.isKeepVariantEnabled());
        }

        try {
            Log.i("gugoservice undo 1" + service.processGTP("gg-undo 2"));
        } catch (Exception e) {
            Log.w("RemoteException when undoing",e);
        }

    }

    public boolean doAutoSave() {
        return true;
    }
}
