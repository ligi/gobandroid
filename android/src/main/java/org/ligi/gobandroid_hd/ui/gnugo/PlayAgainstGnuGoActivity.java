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
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;

import org.ligi.axt.AXT;
import org.ligi.axt.listeners.ActivityFinishingOnClickListener;
import org.ligi.gobandroid_hd.App;
import org.ligi.gobandroid_hd.R;
import org.ligi.gobandroid_hd.events.GameChangedEvent;
import org.ligi.gobandroid_hd.logic.Cell;
import org.ligi.gobandroid_hd.logic.GTPHelper;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoMove;
import org.ligi.gobandroid_hd.ui.GoActivity;
import org.ligi.gobandroid_hd.ui.GoPrefs;
import org.ligi.gobandroid_hd.ui.recording.RecordingGameExtrasFragment;
import org.ligi.gobandroid_hd.util.SimpleStopwatch;
import org.ligi.gobandroidhd.ai.gnugo.IGnuGoService;
import org.ligi.tracedroid.logging.Log;

/**
 * Activity to play vs GnoGo
 */
public class PlayAgainstGnuGoActivity extends GoActivity implements Runnable {

    private IGnuGoService service;
    private ServiceConnection connection;

    private GnuGoSetupDialog dlg;

    private boolean gnugoSizeSet = false;

    private long avgTimeInMillis = 0;

    private GnuGoGame gnuGoGame;

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

                gnuGoGame = new GnuGoGame(dlg.isBlackActive() | dlg.isBothActive(),
                                          dlg.isWhiteActive() | dlg.isBothActive(),
                                          (byte) dlg.getStrength(),
                                          getGame());


                gnuGoGame.setMetaDataForGame(getApp());
                dlg.saveRecentAsDefault();
                dialog.dismiss();
            }

        });

        dlg.setNegativeButton(R.string.cancel, new ActivityFinishingOnClickListener(this));
        dlg.show();

    }

    @Override
    public void doTouch(final MotionEvent event) {

        if (gnuGoGame != null && (gnuGoGame.gnugoNowBlack() | gnuGoGame.gnugoNowWhite())) {
            showInfoToast(R.string.not_your_turn);
        } else {
            super.doTouch(event);
        }

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
                    Log.i("Service bound to " + PlayAgainstGnuGoActivity.this.service.processGTP("version"));
                } catch (RemoteException e) {
                    Log.w("RemoteException when connecting", e);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i("Service unbound ");
            }
        };

        final Intent intent = getGnuGoIntent();
        final ResolveInfo resolveInfo = getPackageManager().resolveService(intent, 0);

        final ComponentName name = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);

        intent.setComponent(name);

        getApp().bindService(intent, connection, Context.BIND_AUTO_CREATE);

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
            getApplication().stopService(getGnuGoIntent());
        } catch (Exception e) {
            Log.w("Exception in stop()", e);
        }
        connection = null;

    }

    @NonNull
    private Intent getGnuGoIntent() {
        return AXT.at(new Intent(GnuGoHelper.INTENT_ACTION_NAME)).makeExplicit(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_game_pass).setVisible(!getGame().isFinished());
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.ingame_record, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onGameChanged(GameChangedEvent gameChangedEvent) {
        super.onGameChanged(gameChangedEvent);

        if (getGame().isFinished()) {
            switchToCounting();
        }

    }

    public Fragment getGameExtraFragment() {
        return new RecordingGameExtrasFragment();
    }

    @Override
    public GoGame.MoveStatus doMoveWithUIFeedback(Cell cell) {
        if (gnuGoGame != null) {
            if (gnuGoGame.aiIsThinking) {
                Toast.makeText(this, R.string.ai_is_thinking, Toast.LENGTH_LONG).show();
                return GoGame.MoveStatus.VALID;
            }

            if ((getGame().isBlackToMove() && (!gnuGoGame.playingBlack))) {
                processMove("black", cell);
            } else if (((!getGame().isBlackToMove()) && (!gnuGoGame.playingWhite))) {
                processMove("white", cell);
            }

        }

        return super.doMoveWithUIFeedback(cell);
    }

    public void processMove(final String color, final Cell cell) {
        try {
            service.processGTP(color + " " + coordinates2gtpstr(cell));
        } catch (Exception e) {
            Log.w("problem processing " + color + " move to " + coordinates2gtpstr(cell));
        }
    }

    private boolean checkGnuGoSync() {
        try {
            return GnuGoHelper.checkGnuGoSync(service.processGTP("showboard"), getGame());
        } catch (RemoteException e) {
            return false;
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
            if (service == null || gnuGoGame == null || getGame().isFinished() || connection == null) {
                continue;
            }

            if (gnugoSizeSet && !checkGnuGoSync()) { // check if gobandroid
                // and gnugo see the same board - otherwise tell gnugo about the truth afterwards ;-)
                try {
                    Log.i("gnugo sync check problem" + service.processGTP("showboard") + getGame().getVisualBoard().toString());
                    gnugoSizeSet = false;
                } catch (RemoteException e) {
                    Log.w("RemoteException when syncing", e);
                }
            }

            if (!gnugoSizeSet) {
                try {
                    // set the size
                    service.processGTP("boardsize " + getGame().getBoardSize());

                    GoMove currentMove = getGame().getFirstMove();

                    while (currentMove.hasNextMove()) {
                        currentMove = currentMove.getnextMove(0);

                        final String gtpMove = getGtpMoveFromMove(currentMove);

                        if (currentMove.isBlackToMove()) {
                            service.processGTP("play black " + gtpMove);
                        } else {
                            service.processGTP("play white " + gtpMove);
                        }

                    }

                    Log.i("setting level " + service.processGTP("level " + gnuGoGame.level));

                    gnugoSizeSet = true;
                } catch (Exception e) {
                    Log.w("RemoteException when configuring", e);
                }
            }

            if (gnuGoGame.gnugoNowBlack()) {
                doMove("black");
            }

            if (gnuGoGame.gnugoNowWhite()) {
                doMove("white");
            }

        }
        stop();

    }

    private String getGtpMoveFromMove(final GoMove currentMove) {
        if (currentMove.isPassMove()) {
            return "pass";
        } else {
            return coordinates2gtpstr(currentMove.getCell());
        }
    }

    private void doMove(final String color) {
        gnuGoGame.aiIsThinking = true;
        final SimpleStopwatch simpleStopwatch = new SimpleStopwatch();
        try {
            final String answer = service.processGTP("genmove " + color);

            if (!GTPHelper.doMoveByGTPString(answer, getGame())) {
                Log.w("GnuGoProblem " + answer + " board " + service.processGTP("showboard"));
                Log.w("restarting GnuGo " + answer);
                gnugoSizeSet = false; // reset
            }
            Log.i("gugoservice" + service.processGTP("showboard"));
        } catch (Exception e) {
            Log.w("RemoteException when moving", e);
        }
        final long elapsed = simpleStopwatch.elapsed();
        avgTimeInMillis = (avgTimeInMillis + elapsed) / 2;
        Log.i("TimeSpent average:" + avgTimeInMillis + " last:" + elapsed);
        gnuGoGame.aiIsThinking = false;
    }

    private String coordinates2gtpstr(Cell cell) {
        if (getGame() == null) {
            Log.w("coordinates2gtpstr called with game==null");
            return "";
        }

        if (cell == null) {
            Log.w("coordinates2gtpstr cell with game==null");
            return "";
        }
        return GTPHelper.coordinates2gtpstr(cell, getGame().getSize());
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
            final String undoResult = service.processGTP("gg-undo 2");
            Log.i("gugoservice undo " + undoResult);
        } catch (Exception e) {
            Log.w("RemoteException when undoing", e);
        }

    }

    @Override
    public boolean doAutoSave() {
        return true;
    }

    @Override
    public void initializeStoneMove() {
        // we do not want this behaviour so we override and do nothing
    }

}
