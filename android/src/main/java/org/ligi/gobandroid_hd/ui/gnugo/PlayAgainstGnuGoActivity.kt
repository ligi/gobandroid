package org.ligi.gobandroid_hd.ui.gnugo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.os.SystemClock
import android.view.Menu
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.events.GameChangedEvent
import org.ligi.gobandroid_hd.logic.*
import org.ligi.gobandroid_hd.ui.GoActivity
import org.ligi.gobandroid_hd.ui.GoPrefs
import org.ligi.gobandroid_hd.ui.recording.RecordingGameExtrasFragment
import org.ligi.gobandroid_hd.util.SimpleStopwatch
import org.ligi.gobandroidhd.ai.gnugo.IGnuGoService
import org.ligi.kaxt.makeExplicit
import timber.log.Timber

/**
 * Activity to play vs GnoGo
 */
class PlayAgainstGnuGoActivity : GoActivity(), Runnable {

    private var service: IGnuGoService? = null
    private var connection: ServiceConnection? = null

    private lateinit var dlg: GnuGoSetupDialog

    private var gnugoSizeSet = false

    private var avgTimeInMillis: Long = 0

    private var gnuGoGame: GnuGoGame? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO the next line works but needs investigation - i thought more of
        // getBoard().requestFocus(); - but that was not working ..
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        App.tracker.trackEvent("ui_action", "gnugo", "play", null)

        dlg = GnuGoSetupDialog(this)

        dlg.setPositiveButton(R.string.ok) { dialog ->
            gnuGoGame = GnuGoGame(dlg.isBlackActive() or dlg.isBothActive(),
                    dlg.isWhiteActive() or dlg.isBothActive(),
                    dlg.strength().toByte(),
                    game)


            gnuGoGame!!.setMetaDataForGame(app)
            dlg.saveRecentAsDefault()
            dialog.dismiss()
        }

        dlg.setNegativeButton(R.string.cancel, { dialog ->
            dialog.dismiss()
            finish()
        })
        dlg.show()

    }

    override fun doTouch(event: MotionEvent) {

        if (gnuGoGame != null && gnuGoGame!!.gnugoNowBlack() or gnuGoGame!!.gnugoNowWhite()) {
            showInfoToast(R.string.not_your_turn)
        } else {
            super.doTouch(event)
        }

    }

    override fun onResume() {
        super.onResume()
        connection = object : ServiceConnection {

            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                this@PlayAgainstGnuGoActivity.service = IGnuGoService.Stub.asInterface(service)

                try {
                    Timber.i("Service bound to " + this@PlayAgainstGnuGoActivity.service!!.processGTP("version"))
                } catch (e: RemoteException) {
                    Timber.w(e, "RemoteException when connecting")
                }
            }

            override fun onServiceDisconnected(name: ComponentName) {
                Timber.i("Service unbound ")
            }
        }

        val intent = gnuGoIntent
        val resolveInfo = packageManager.resolveService(intent, 0)

        val name = ComponentName(resolveInfo!!.serviceInfo.packageName, resolveInfo.serviceInfo.name)

        intent.component = name

        app.bindService(intent, connection!!, Context.BIND_AUTO_CREATE)

        Thread(this).start()

        super.onStart()
    }

    override fun onPause() {
        stop()
        super.onPause()
    }

    fun stop() {
        if (service == null) {
            return
        }

        service = null
        try {
            application.unbindService(connection!!)
            application.stopService(gnuGoIntent)
        } catch (e: Exception) {
            Timber.w(e, "Exception in stop()")
        }

        connection = null

    }

    private val gnuGoIntent: Intent
        get() = Intent(GnuGoHelper.INTENT_ACTION_NAME).makeExplicit(this)!!

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.menu_game_pass).isVisible = !game.isFinished
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menuInflater.inflate(R.menu.ingame_record, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onGameChanged(gameChangedEvent: GameChangedEvent) {
        super.onGameChanged(gameChangedEvent)

        if (game.isFinished) {
            switchToCounting()
        }

    }

    override val gameExtraFragment: Fragment
        get() = RecordingGameExtrasFragment()


    public override fun doMoveWithUIFeedback(cell: Cell?): GoGame.MoveStatus {
        if (gnuGoGame != null && cell != null) {
            if (gnuGoGame!!.aiIsThinking) {
                Toast.makeText(this, R.string.ai_is_thinking, Toast.LENGTH_LONG).show()
                return GoGame.MoveStatus.VALID
            }

            if (game.isBlackToMove && !gnuGoGame!!.playingBlack) {
                processMove("black", cell)
            } else if (!game.isBlackToMove && !gnuGoGame!!.playingWhite) {
                processMove("white", cell)
            }

        }

        return super.doMoveWithUIFeedback(cell)
    }

    fun processMove(color: String, cell: Cell) {
        try {
            service!!.processGTP(color + " " + coordinates2gtpstr(cell))
        } catch (e: Exception) {
            Timber.w("problem processing " + color + " move to " + coordinates2gtpstr(cell))
        }

    }

    private fun checkGnuGoSync(): Boolean {
        return try {
            GnuGoHelper.checkGnuGoSync(service!!.processGTP("showboard"), game)
        } catch (e: RemoteException) {
            false
        }
    }

    override fun run() {
        while (connection != null) {
            SystemClock.sleep(100)

            // blocker for the following steps
            if (service == null || gnuGoGame == null || game.isFinished || connection == null) {
                continue
            }

            if (gnugoSizeSet && !checkGnuGoSync()) { // check if gobandroid
                // and gnugo see the same board - otherwise tell gnugo about the truth afterwards ;-)
                try {
                    Timber.i("gnugo sync check problem" + service!!.processGTP("showboard") + game.visualBoard.toString())
                    gnugoSizeSet = false
                } catch (e: RemoteException) {
                    Timber.w(e, "RemoteException when syncing")
                }

            }

            if (!gnugoSizeSet) {
                try {
                    // set the size
                    service!!.processGTP("boardsize " + game.boardSize)
                    game.statelessGoBoard.withAllCells { statelessBoardCell ->
                        try {
                            if (game.handicapBoard.isCellDeadWhite(statelessBoardCell)) {
                                service!!.processGTP("white " + coordinates2gtpstr(statelessBoardCell))
                            } else if (game.handicapBoard.isCellBlack(statelessBoardCell)) {
                                service!!.processGTP("black " + coordinates2gtpstr(statelessBoardCell))
                            }
                        } catch (e: RemoteException) {
                            e.printStackTrace()
                        }
                    }

                    val replay_moves = ArrayList<GoMove>()
                    replay_moves.add(game.actMove)
                    var tmp_move: GoMove
                    while (true) {
                        tmp_move = replay_moves.last()
                        if (tmp_move.isFirstMove || tmp_move.parent == null) break
                        replay_moves.add(tmp_move.parent!!)
                    }
                    for (step in replay_moves.indices.reversed()) {
                        tmp_move = replay_moves[step]
                        val gtpMove = getGtpMoveFromMove(tmp_move)
                        if (tmp_move.player == GoDefinitions.PLAYER_BLACK) {
                            service!!.processGTP("play black " + gtpMove)
                        } else {
                            service!!.processGTP("play white " + gtpMove)
                        }
                    }

                    Timber.i("setting level " + service!!.processGTP("level " + gnuGoGame!!.level))
                    gnugoSizeSet = true
                } catch (e: Exception) {
                    Timber.w(e, "RemoteException when configuring")
                }
            }

            if (gnuGoGame!!.gnugoNowBlack()) {
                doMove("black")
            }

            if (gnuGoGame!!.gnugoNowWhite()) {
                doMove("white")
            }

        }
        stop()

    }

    private fun getGtpMoveFromMove(currentMove: GoMove): String {
        if (currentMove.isPassMove) {
            return "pass"
        } else {
            return coordinates2gtpstr(currentMove.cell)
        }
    }

    private fun doMove(color: String) {
        gnuGoGame!!.aiIsThinking = true
        val simpleStopwatch = SimpleStopwatch()
        try {
            val answer = service!!.processGTP("genmove " + color)

            if (!GTPHelper.doMoveByGTPString(answer, game)) {
                Timber.w("GnuGoProblem " + answer + " board " + service!!.processGTP("showboard"))
                Timber.w("restarting GnuGo " + answer)
                gnugoSizeSet = false // reset
            }
            Timber.i("gugoservice" + service!!.processGTP("showboard"))
        } catch (e: Exception) {
            Timber.w(e, "RemoteException when moving")
        }

        val elapsed = simpleStopwatch.elapsed()
        avgTimeInMillis = (avgTimeInMillis + elapsed) / 2
        Timber.i("TimeSpent average:$avgTimeInMillis last:$elapsed")
        gnuGoGame!!.aiIsThinking = false
    }

    private fun coordinates2gtpstr(cell: Cell?): String {

        if (cell == null) {
            Timber.w("coordinates2gtpstr called with cell==null")
            return ""
        }
        return GTPHelper.coordinates2gtpstr(cell, game.size)
    }

    override fun requestUndo() {

        if (game.canUndo()) {
            game.undo(GoPrefs.isKeepVariantWanted)
        }

        if (game.canUndo()) {
            game.undo(GoPrefs.isKeepVariantWanted)
        }

        try {
            val undoResult = service!!.processGTP("gg-undo 2")
            Timber.i("gugoservice undo " + undoResult)
        } catch (e: Exception) {
            Timber.w(e, "RemoteException when undoing")
        }

    }

    override fun doAutoSave(): Boolean {
        return true
    }

    override fun initializeStoneMove() {
        // we do not want this behaviour so we override and do nothing
    }

}
