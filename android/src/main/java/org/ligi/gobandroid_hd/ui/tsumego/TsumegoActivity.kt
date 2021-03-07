package org.ligi.gobandroid_hd.ui.tsumego

import android.content.DialogInterface
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.game.*
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.events.GameChangedEvent
import org.ligi.gobandroid_hd.events.TsumegoSolved
import org.ligi.gobandroid_hd.logic.Cell
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.logic.GoMove
import org.ligi.gobandroid_hd.ui.GoActivity
import org.ligi.gobandroid_hd.ui.review.SGFMetaData
import timber.log.Timber

class TsumegoActivity : GoActivity() {

    lateinit var tsumegoController: TsumegoController

    public override fun doMoveWithUIFeedback(cell: Cell?): GoGame.MoveStatus {

        val res = super.doMoveWithUIFeedback(cell)

        // if the move was valid and we have a counter move -> we will play it
        if (res === GoGame.MoveStatus.VALID) {
            if (game.actMove.hasNextMove()) {
                game.jump(game.actMove.getnextMove(0))
            }
        }

        bus.post(GameChangedEvent)
        return res
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.ingame_tsumego, menu)
        menu.findItem(R.id.menu_game_hint).isVisible = tsumegoController.isFinishingMoveKnown() && tsumegoController.isOnPath()
        menu.findItem(R.id.menu_game_undo).isVisible = !game.actMove.isFirstMove
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_game_hint -> {
            Timber.i("FinishingMoveDebug " + tsumegoController.finishingMove)
            TsumegoHintAlert.show(this, tsumegoController.finishingMove)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun initializeStoneMove() {
        // disable stone move - not wanted in tsumego
    }

    override fun requestUndo() {
        go_board.move_stone_mode = false
        // we do not want to keep user-variations in tsumego mode- but we want
        // to keep tsumego variation
        game.undo(tsumegoController.isOnPath())

        // remove the counter-move if any
        if (!game.isBlackToMove) {
            game.undo(tsumegoController.isOnPath())
        }
    }

    override val gameExtraFragment: TsumegoGameExtrasFragment by lazy {
        TsumegoGameExtrasFragment()
    }

    override fun onResume() {
        super.onResume()

        setTitle(R.string.tsumego)

        tsumegoController = TsumegoController(game)

        // try to find the correct solution
        if (!tsumegoController.isFinishingMoveKnown()) {
            AlertDialog.Builder(this).setMessage(R.string.tsumego_sgf_no_solution)
                    .setNegativeButton(R.string.ok, null)
                    .setPositiveButton(R.string.go_back) { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                        finish()
                    }.show()
        }

        val myZoom = TsumegoHelper.calcZoom(game, true)

        go_board.zoom = myZoom
        go_board.setZoomPOI(TsumegoHelper.calcPOI(game, true))
        onGameChanged(GameChangedEvent)

    }

    private var last_move: GoMove? = null

    override fun onGameChanged(gameChangedEvent: GameChangedEvent) {
        super.onGameChanged(gameChangedEvent)
        if (game.actMove == last_move) {
            // TODO find the real cause why we got here to often for the same move
            // mainly a problem for writing the moments - otherwise too many moments where written for the same
            // solved tsumego

            return
        }
        last_move = game.actMove

        gameExtraFragment.setOffPathVisibility(!tsumegoController.isOnPath())
        gameExtraFragment.setCorrectVisibility(tsumegoController.isCorrectMove(game.actMove))

        if (tsumegoController.isCorrectMove(game.actMove)) {
            val meta = SGFMetaData(game.metaData.fileName)
            meta.isSolved = true
            meta.persist()

            bus.post(TsumegoSolved(game))
        }
        runOnUiThread { invalidateOptionsMenu() }
    }

    override fun isAsk4QuitEnabled() = false

}
